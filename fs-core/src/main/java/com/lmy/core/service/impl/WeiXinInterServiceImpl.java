package com.lmy.core.service.impl;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;  
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.HttpService;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.component.MD5Util;
import com.lmy.common.component.XmlHelper;
import com.lmy.common.redis.RedisClient;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.utils.FsEnvUtil;
/**
 * 与微信接口交互类
 * @author Administrator
 */
public class WeiXinInterServiceImpl {
	private static final Logger logger = Logger.getLogger(WeiXinInterServiceImpl.class);
	private static final String ACCESS_TOKEN_KEY1=CacheConstant.FS_WEIXIN_ACCESS_TOKEN1 ;
	private static HttpService httpService = new HttpService();
	public static String getAccessToken1() throws IOException{
		if(FsEnvUtil.isDev()){
			logger.info("开发模拟的AccessToken 。。。。。。。");
			return "T1HKeJpM3G5Sb3y7WSl4QfxjFwLdpOIpy6PnDZIZLk43N13n9FFnb2lCzUDGRwP8V57aG7p9khH9-7wll8W_qkRleRoiVA2U3BrrdL5KV9t_U67tP6-lnJUlt7JUdq2WUQQiAEAAWW";
		}
		//第一步从redis 中取得
		JSONObject result = (JSONObject)RedisClient.get(ACCESS_TOKEN_KEY1);
		if(result!=null && result.containsKey("access_token")){
			//logger.info("获取微信access_token缓存命中 result="+result);
			return (String)result.get("access_token");
		}else{
			String appId = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId");  
			String secret = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appsecret");   
			//重新获取一次
			String resp = httpService.doGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+secret, "utf-8");
			Date now = new Date();
			//logger.info("获取微信access_token响应:resp="+resp);
			JSONObject respJson = JSON.parseObject(resp);
			if(respJson.containsKey("errcode")){
				logger.info("获取微信access_token 错误:resp="+respJson);
				return null;
			}else{
				respJson.put("_cacheTime", now);
				RedisClient.set(ACCESS_TOKEN_KEY1, respJson, 7000);
				return respJson.getString("access_token");
			}
		}
	}
	/** userInfo={"subscribe":1,"openid":"osEKMwYRbYFlaiQmu0du1ryMkxKM","nickname":"周兆华","sex":1,"language":"zh_CN","city":"浦东新区","province":"上海","country":"中国","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/pqLhiccpwO1fqpQqDLSEXIgTGWY5dIfmNNNHBwA1TDeWPTMakKr8nZGpuTKyNlxsSZSFHTKnTCmMLia2jOLtGY2IwQpU21geF3\/0","subscribe_time":1456890469,"remark":"","groupid":0}
	 * **/
	public static JSONObject getUserInfo1(String openId) {
		try {
			String authorizerAccessToken = getAccessToken1();
			String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + authorizerAccessToken + "&openid=" + openId + "&lang=zh_CN";
			HttpService hs = new HttpService();
			String getResult = hs.doPost(url, null, "utf-8");
			return  JSON.parseObject(getResult);
		} catch (Exception e) {
			logger.error("openId="+openId,e);
			return null;
		}
	}
	
	public static String  pushWeixinTampleteMsg(String openId , String templateId , String clickUrl , JSONObject data) throws Exception{
		JSONObject requestJson = new JSONObject();
		requestJson.put("touser", openId);
		requestJson.put("template_id", templateId);
		requestJson.put("url", clickUrl);
		requestJson.put("data", data);
		return  httpService.doPostRequestEntity("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+getAccessToken1(), requestJson.toJSONString(), false,"utf-8");
	}
	
	/**
	 * 微信统一支付,交易有效时间 5分钟 
	 * @return
	 */
	public  static JSONObject unifiedOrder1(FsPayRecord payRecord) {
		try {	
			Date now = new Date();
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			if(uuid.length()>32){
				uuid = uuid.substring(0, 32);
			}
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("appid", payRecord.getAppId());  
			parameters.put("mch_id", payRecord.getMchId()); 
			//parameters.put("device_info",  null); // 设备号
			parameters.put("nonce_str", uuid);  //随机字符串
			parameters.put("body", payRecord.getBody());  //商品描述	
			parameters.put("detail", payRecord.getDetail());  //商品描述	N
			parameters.put("attach", payRecord.getAttach() !=null ?  payRecord.getAttach() : "");  //附加数据	N
			parameters.put("out_trade_no", payRecord.getOutTradeNo());  //商户订单号
			parameters.put("fee_type", "CNY");  //货币类型 N
			parameters.put("total_fee", payRecord.getTotalFee()+""); // 总金额 单位分
			parameters.put("spbill_create_ip",  payRecord.getSpbillCreateIp());  // 终端IP
			parameters.put("time_start", CommonUtils.dateToString(now, CommonUtils.dateFormat3, null));	  //交易起始时间 N
			parameters.put("time_expire", CommonUtils.dateToString(DateUtils.addMinutes(now, 5) , CommonUtils.dateFormat3, null));	 // 交易结束时间 N // 注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。 
			parameters.put("goods_tag", payRecord.getGoodsTag()!=null ?payRecord.getGoodsTag():"");  // 商品标记 N
			parameters.put("notify_url", payRecord.getNotifyUrl() ); //通知地址
			parameters.put("trade_type", "JSAPI"); //交易类型 
			parameters.put("product_id",  ""); // 商品ID N
			//parameters.put("limit_pay", "");  // 指定支付方式 N
			parameters.put("openid",  payRecord.getOpenId());  //用户标识 N

			Collection<String> keyset= parameters.keySet();  
	    	 List<String> list = new ArrayList<String>(keyset);  
	         //对key键值按字典升序排序  
	         Collections.sort(list);  
	         SortedMap<String,String> sortMap = new  TreeMap<String,String>();
	         for (int i = 0; i < list.size(); i++) {  
	        	 sortMap.put(list.get(i), parameters.get(list.get(i)));
	         }  
			String sign =createMd5Sign( sortMap);
			Assert.isTrue( StringUtils.isNotEmpty(sign)  );
			sortMap.put("sign", sign);
			String requestXml = getWeixinRequestXml(sortMap);

			logger.info("微信  orderId:"+payRecord.getOrderId()+" unifiedOrder requestXML="+requestXml);
			String respXml = httpService.doPostRequestEntity("https://api.mch.weixin.qq.com/pay/unifiedorder", requestXml,false,"utf-8"); 
			logger.info("微信  orderId:"+payRecord.getOrderId()+" unifiedOrder resp="+respXml);
			
			if(StringUtils.isEmpty(respXml)){
				logger.error("微信统一下单失败 下单响应为空,payRecord="+payRecord);
				return  JsonUtils.commonJsonReturn("0001","微信统一支付失败");
			}
			Element root = XmlHelper.getField(respXml);
			Element returnCodeEle = XmlHelper.child(root, "return_code");
			String return_code =  returnCodeEle!=null ? returnCodeEle.getText():null;
			Element resultCodeEle = XmlHelper.child(root, "result_code");
			String result_code =  resultCodeEle!=null ? resultCodeEle.getText():null;
			if(!"SUCCESS".equals(return_code) || !"SUCCESS".equals(result_code)){
				logger.error("微信统一下单失败 下单响应为空,payRecord="+payRecord);
				return  JsonUtils.commonJsonReturn("0001","微信统一支付失败");
			}
			Element prepayIdEle = XmlHelper.child(root, "prepay_id");
			String prepay_id =  prepayIdEle!=null ? prepayIdEle.getText():null;
			String appId = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId");  
			String payKey = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.mer.appkey"); 
			String uuid2 = UUID.randomUUID().toString().replaceAll("-", "");
			String timeStamp = new Date().getTime()/1000+"";
			if(uuid2.length()>32){
				uuid2 = uuid.substring(0, 32);
			}
			JSONObject result = JsonUtils.commonJsonReturn();
			result.getJSONObject("body").put("appId", appId);
			result.getJSONObject("body").put("package", "prepay_id="+prepay_id);
			result.getJSONObject("body").put("timeStamp", timeStamp);
			result.getJSONObject("body").put("nonceStr", uuid2);
			result.getJSONObject("body").put("signType", "MD5");
			result.getJSONObject("body").put("tradeNum", payRecord.getOutTradeNo());
			String paySignStr = "appId="+appId+"&nonceStr="+uuid2+"&package=prepay_id="+prepay_id+"&signType=MD5&timeStamp="+timeStamp+"&key="+payKey;
			String paySign = MD5Util.MD5Encode(paySignStr, "UTF-8").toUpperCase();
			result.getJSONObject("body").put("paySign", paySign);
			logger.info("微信orderId:"+payRecord.getOrderId()+" 统一下单返回页面参数result="+result);
			return result;
		} catch (Exception e) {
			logger.error("微信支付统一下单失败 payRecord="+payRecord,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}

	
	/**
	 * 微信退款申请
	 */
	public  static JSONObject refund1( FsPayRecord orgPaySuccBean , FsPayRecord refundBean) throws Exception{
		String requestXml = null;
		String respXml = null;
		String out_refund_no = refundBean.getOutTradeNo();
		try{
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			if(uuid.length()>32){
				uuid = uuid.substring(0, 32);
			}
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("appid", refundBean.getAppId());  
			parameters.put("mch_id", refundBean.getMchId()); 
			parameters.put("nonce_str", uuid);  //随机字符串
			parameters.put("transaction_id", orgPaySuccBean.getRespTradeNo());
			parameters.put("out_refund_no", out_refund_no);
			parameters.put("total_fee", refundBean.getTotalFee()+"");
			parameters.put("refund_fee", refundBean.getRefundFee()+"");
			parameters.put("refund_fee_type", orgPaySuccBean.getFeeType());
			parameters.put("op_user_id", refundBean.getMchId());
			Collection<String> keyset= parameters.keySet();  
	   	 	List<String> list = new ArrayList<String>(keyset);  
	        //对key键值按字典升序排序  
	        Collections.sort(list);  
	        SortedMap<String,String> sortMap = new  TreeMap<String,String>();
	        for (int i = 0; i < list.size(); i++) {  
	       	 sortMap.put(list.get(i), parameters.get(list.get(i)));
	        }  
			String sign =createMd5Sign( sortMap);
			Assert.isTrue( StringUtils.isNotEmpty(sign)  );
			sortMap.put("sign", sign);
			requestXml = getWeixinRequestXml(sortMap);
			logger.info("微信 refund requestXML="+requestXml);
			
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			 char[] passwd = refundBean.getMchId().toCharArray();
			 String wxCertFile = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.cert.apiclient.file") ;
			 FileInputStream instream = new FileInputStream(new File(wxCertFile));  
	         keyStore.load(instream,passwd);  
	         final SSLContext sslcontext = org.apache.http.ssl.SSLContexts.custom().loadKeyMaterial(keyStore,  passwd ).build();  
	         //SSLConnectionSocketFactory(javax.net.ssl.SSLContext, String [], String [], javax.net.ssl.HostnameVerifier)
	         @SuppressWarnings("deprecation")
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  sslcontext, new String[] { "TLSv1" }, null,  SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
	         CloseableHttpClient httpclient = HttpClients.custom() .setSSLSocketFactory(sslsf).build();  
	         HttpPost httppost = new HttpPost(   "https://api.mch.weixin.qq.com/secapi/pay/refund");  
	         StringEntity se = new StringEntity(requestXml);  
             httppost.setEntity(se);  
             CloseableHttpResponse responseEntry = httpclient.execute(httppost);  
            
             HttpEntity entity = responseEntry.getEntity();  
             StringBuffer responseBody = new StringBuffer();
             InputStream in  =  entity.getContent();
             BufferedInputStream bin = new BufferedInputStream(in);
 			byte [] _b = new byte[1024*10];
 			int length = 0;
 			while((length = bin.read(_b))>0){
 				responseBody.append(new String(_b,0,length,"utf-8"));
 			}
 			in.close();
 			bin.close();
 			respXml = responseBody.toString();
			logger.info("微信 refund resp="+respXml);
			if(StringUtils.isEmpty(respXml)){
				logger.warn("微信 refund 下单失败 下单响应为空requestXml:"+requestXml+";respXml:"+respXml);
				return  JsonUtils.commonJsonReturn("0001","退款申请失败");
			}
			Element root = XmlHelper.getField(respXml);
			Element returnCodeEle = XmlHelper.child(root, "return_code");
			String return_code =  returnCodeEle!=null ? returnCodeEle.getText():null;
			Element resultMsgEle = XmlHelper.child(root, "return_msg");
			String return_msg =  resultMsgEle!=null ? resultMsgEle.getText():null;
			if(!"SUCCESS".equals(return_code) ){
				logger.error("微信 refund 下单失败 return_code:"+return_code+";return_msg:"+return_msg);
				JSONObject result = JsonUtils.commonJsonReturn("0001","退款申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}else{
				JSONObject result =  JsonUtils.commonJsonReturn();
				JsonUtils.setBody(result, "respMsg", respXml);
				return result;
			}
		}catch(Exception e){
			if(e instanceof org.apache.commons.httpclient.NoHttpResponseException){
				logger.error("requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result = JsonUtils.commonJsonReturn("0001","退款申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			else if(e instanceof org.apache.commons.httpclient.ConnectTimeoutException){
				logger.error("连接超时 requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result = JsonUtils.commonJsonReturn("0001","退款申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			else if(e instanceof java.io.FileNotFoundException){
				logger.error("FileNotFoundException requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result = JsonUtils.commonJsonReturn("0001","退款申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			//数据等待失败 需要确认
			else if(e instanceof java.net.SocketTimeoutException){
				logger.error("数据等待失败 requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result =  JsonUtils.commonJsonReturn("1000","退款申请结果未知:需要确认");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			else if(e instanceof java.net.SocketException){
				logger.error("requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result =JsonUtils.commonJsonReturn("1000","退款申请结果未知:需要确认");			
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}else{
				logger.error("requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result =JsonUtils.commonJsonReturn("1000","退款申请结果未知:需要确认");			
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
		}
	}
	/**  0000 明确退款成功; 0001 明确退款失败; 1000 退款中; 9999 查询碰到系统错误 **/
	public static JSONObject  refundQuery1(String out_trade_no ) {
		Map<String,String> map = new HashMap<String,String>();
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		if(uuid.length()>32){
			uuid = uuid.substring(0, 32);
		}
		String requestXml = null;
		String respXml = null;
		try{
			map.put("appid", ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId"));  
			map.put("mch_id", ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.merId"));  
			map.put("nonce_str", uuid);
			map.put("out_refund_no",StringUtils.isNotEmpty( out_trade_no)?out_trade_no:"");
			Collection<String> keyset= map.keySet();  
	   	 	List<String> list = new ArrayList<String>(keyset);  
	        //对key键值按字典升序排序  
	        Collections.sort(list);  
	        SortedMap<String,String> sortMap = new  TreeMap<String,String>();
	        for (int i = 0; i < list.size(); i++) {  
	       	 sortMap.put(list.get(i), map.get(list.get(i)));
	        }  
			String sign =createMd5Sign( sortMap);
			if(StringUtils.isEmpty(sign)){
				return  JsonUtils.commonJsonReturn("9999","系统错误");
			}
			sortMap.put("sign", sign);
			 requestXml = getWeixinRequestXml(sortMap);
			logger.info("微信 refundquery requestXML="+requestXml);
			 respXml = httpService.doPostRequestEntity("https://api.mch.weixin.qq.com/pay/refundquery", requestXml,false,"utf-8"); 
			logger.info("微信 refundquery resp="+respXml);		
			Element root = XmlHelper.getField(respXml);
			Element returnCodeEle = XmlHelper.child(root, "return_code");
			String return_code =  returnCodeEle!=null ? returnCodeEle.getText():null;
			
			Element resultCodeEle = XmlHelper.child(root, "result_code");
			String result_code =  resultCodeEle!=null ? resultCodeEle.getText():null;

			Element resultMsgEle = XmlHelper.child(root, "return_msg");
			String return_msg =  resultMsgEle!=null ? resultMsgEle.getText():null;
			
			if(!"SUCCESS".equals(result_code)  && "SUCCESS".equals(return_code) ){
				logger.info("微信 refund 下单失败 return_code:"+return_code+";return_msg:"+return_msg);
				JSONObject result =   JsonUtils.commonJsonReturn("0001","退款失败");
				JsonUtils.setBody(result, "respXml", respXml);
				return result;
			}
			Element resultStatusEle = XmlHelper.child(root, "refund_status_0");
			String resultStatus =  resultStatusEle!=null ? resultStatusEle.getText():null;
			// 退款成功
			if("SUCCESS".equals(resultStatus)){
				JSONObject result =   JsonUtils.commonJsonReturn("0000","退款成功");
				JsonUtils.setBody(result, "respXml", respXml);
				JsonUtils.setBody(result, "tradeStatus", resultStatus);
				return result;
			}
			//REFUNDCLOSE—退款关闭
			else if("REFUNDCLOSE".equals(resultStatus)){
				JSONObject result =   JsonUtils.commonJsonReturn("0001","退款失败功");
				JsonUtils.setBody(result, "respXml", respXml);
				JsonUtils.setBody(result, "tradeStatus", resultStatus);
				return result;
			}
			//NOTSURE—未确定，需要商户用原退款单号重新发起退款申请
			else if("NOTSURE".equals(resultStatus)){
				JSONObject result =   JsonUtils.commonJsonReturn("0001","退款失败功");
				JsonUtils.setBody(result, "respXml", respXml);
				JsonUtils.setBody(result, "tradeStatus", resultStatus);
				return result;
			}
			//PROCESSING—退款处理中
			else if("PROCESSING".equals(resultStatus)){
				JSONObject result =   JsonUtils.commonJsonReturn("1000","退款中");
				JsonUtils.setBody(result, "respXml", respXml);
				JsonUtils.setBody(result, "tradeStatus", resultStatus);
				return result;
			}
			//CHANGE—退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，商户可以发起异常退款处理的申请，可以退款到用户的新卡或者退款商户，商户自行处理
			else if("CHANGE	".equals(resultStatus)){
				JSONObject result =   JsonUtils.commonJsonReturn("0001","退款失败功");
				JsonUtils.setBody(result, "respXml", respXml);
				JsonUtils.setBody(result, "tradeStatus", resultStatus);
				return result;
			}else{
				JSONObject result =   JsonUtils.commonJsonReturn("1000","退款中");
				JsonUtils.setBody(result, "respXml", respXml);
				JsonUtils.setBody(result, "tradeStatus", resultStatus);
				return result;
			}
		}catch(Exception e){
			logger.error("查询失败 requestXml:"+requestXml+" , respXml:"+respXml, e);
			JSONObject result =   JsonUtils.commonJsonReturn("9999","系统错误");
			return result;
		}
	}
	
	
	/**
	 * 返回的   trade_state 判断成功标志
	 * @param out_trade_no
	 * @param transaction_id
	 * @return <xml><return_code><![CDATA[SUCCESS]]></return_code>
							<return_msg><![CDATA[OK]]></return_msg>
							<appid><![CDATA[wx1b99f22989f7b28d]]></appid>
							<mch_id><![CDATA[1316766301]]></mch_id>
							<nonce_str><![CDATA[VnVEr8N0UJdTdFBN]]></nonce_str>
							<sign><![CDATA[811EA6AB3DDC15D0088CCE80D31C3E69]]></sign>
							<result_code><![CDATA[SUCCESS]]></result_code>
							<openid><![CDATA[osEKMwYRbYFlaiQmu0du1ryMkxKM]]></openid>
							<is_subscribe><![CDATA[Y]]></is_subscribe>
							<trade_type><![CDATA[JSAPI]]></trade_type>
							<bank_type><![CDATA[CMB_DEBIT]]></bank_type>
							<total_fee>100</total_fee>
							<fee_type><![CDATA[CNY]]></fee_type>
							<transaction_id><![CDATA[1000850935201602293619946876]]></transaction_id>
							<out_trade_no><![CDATA[W20160229145907994565]]></out_trade_no>
							<attach><![CDATA[{"loanId":331,"periodNum":1,"repayAmt":100,"userId":54}]]></attach>
							<time_end><![CDATA[20160229145916]]></time_end>
							<trade_state><![CDATA[SUCCESS]]></trade_state>
							<cash_fee>100</cash_fee>
						</xml>
						
						<xml>
							<return_msg><![CDATA[OK]]></return_msg>
							<appid><![CDATA[wx1b99f22989f7b28d]]></appid>
							<mch_id><![CDATA[1316766301]]></mch_id>
							<nonce_str><![CDATA[ggeoKdH17bXqNtex]]></nonce_str>
							<sign><![CDATA[AAEE4F47D75629F771E746095E6FDCEE]]></sign>
							<result_code><![CDATA[SUCCESS]]></result_code>
							<out_trade_no><![CDATA[W20160406100212107350]]></out_trade_no>
							<trade_state><![CDATA[NOTPAY]]></trade_state>
							<trade_state_desc><![CDATA[订单未支付]]></trade_state_desc>
						</xml>
	 * @throws IOException
	 */
	public static String payOrderQuery1(String out_trade_no , String transaction_id) throws Exception{
		Map<String,String> map = new HashMap<String,String>();
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		if(uuid.length()>32){
			uuid = uuid.substring(0, 32);
		}
		map.put("appid", ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId"));  
		map.put("mch_id", ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.merId"));  
		map.put("transaction_id", StringUtils.isNotEmpty(transaction_id)?transaction_id:"");
		map.put("out_trade_no",StringUtils.isNotEmpty( out_trade_no)?out_trade_no:"");
		map.put("nonce_str", uuid);
		
		Collection<String> keyset= map.keySet();  
   	 	List<String> list = new ArrayList<String>(keyset);  
        //对key键值按字典升序排序  
        Collections.sort(list);  
        SortedMap<String,String> sortMap = new  TreeMap<String,String>();
        for (int i = 0; i < list.size(); i++) {  
       	 sortMap.put(list.get(i), map.get(list.get(i)));
        }  
		String sign =createMd5Sign( sortMap);
		if(StringUtils.isEmpty(sign)){
			return null;
		}
		sortMap.put("sign", sign);
		String requestXml = getWeixinRequestXml(sortMap);
		logger.info("微信 orderquery requestXML="+requestXml);
		String respXml = httpService.doPostRequestEntity("https://api.mch.weixin.qq.com/pay/orderquery", requestXml,false,"utf-8"); 
		logger.info("微信 orderquery resp="+respXml);
		return respXml;
	}
	/**
	 * 企业付款API
	 * @param payBean
	 * @return 0000 succ; 0001 fail;1000 需确认 ; key respMsg
	 */
	public static JSONObject transfers1(FsPayRecord payBean){
		String requestXml = null;
		String respXml = null;
		Date now =new Date();
		try{
			if(now.after(  CommonUtils.stringToDate("2017-09-11", CommonUtils.dateFormat1)  )){
				logger.warn("客人要求 不需要做系统打款 outTradeNo:"+payBean.getOutTradeNo()+", orderId"+payBean.getOrderId());
				JSONObject result = JsonUtils.commonJsonReturn("0000","客人要求 不需要做系统打款");
				JsonUtils.setBody(result, "respMsg", "客人要求 不需要做系统打款");
				return  result;
			}
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			if(uuid.length()>32){
				uuid = uuid.substring(0, 32);
			}
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("mch_appid", payBean.getAppId());  
			parameters.put("mchid", payBean.getMchId()); 
			parameters.put("nonce_str", uuid);  //随机字符串
			parameters.put("partner_trade_no", payBean.getOutTradeNo());  //商户订单号，需保持唯一性
			parameters.put("openid", payBean.getOpenId());  //商户appid下，某用户的openid
			parameters.put("check_name", "NO_CHECK");  //NO_CHECK：不校验真实姓名 //FORCE_CHECK：强校验真实姓名
			//parameters.put("re_user_name", payBean.getReUserName()!=null ? payBean.getReUserName() : "");  //收款用户真实姓名。 如果check_name设置为FORCE_CHECK，则必填用户真实姓名
			parameters.put("amount", payBean.getTotalFee().toString());  //企业付款金额，单位为分
			parameters.put("desc", payBean.getTradeDesc()!=null ? payBean.getTradeDesc():"转账"  );  //企业付款操作说明信息。必填。
			parameters.put("spbill_create_ip", payBean.getSpbillCreateIp());  //企业付款操作说明信息。必填。
			Collection<String> keyset= parameters.keySet();  
	   	 	List<String> list = new ArrayList<String>(keyset);  
	        //对key键值按字典升序排序  
	        Collections.sort(list);  
	        SortedMap<String,String> sortMap = new  TreeMap<String,String>();
	        for (int i = 0; i < list.size(); i++) {  
	       	 sortMap.put(list.get(i), parameters.get(list.get(i)));
	        }  
			String sign =createMd5Sign( sortMap);
			Assert.isTrue( StringUtils.isNotEmpty(sign)  );
			sortMap.put("sign", sign);
			requestXml = getWeixinRequestXml(sortMap);
			logger.info("微信 orderId:"+payBean.getOrderId()+",transfers requestXML="+requestXml);
			
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			 char[] passwd = payBean.getMchId().toCharArray();
			 String wxCertFile = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.cert.apiclient.file") ;
			 FileInputStream instream = new FileInputStream(new File(wxCertFile));  
	         keyStore.load(instream,passwd);  
	         final SSLContext sslcontext = org.apache.http.ssl.SSLContexts.custom().loadKeyMaterial(keyStore,  passwd ).build();  
	         //SSLConnectionSocketFactory(javax.net.ssl.SSLContext, String [], String [], javax.net.ssl.HostnameVerifier)
	         @SuppressWarnings("deprecation")
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  sslcontext, new String[] { "TLSv1" }, null,  SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
	         CloseableHttpClient httpclient = HttpClients.custom() .setSSLSocketFactory(sslsf).build();  
	         HttpPost httppost = new HttpPost(   "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers");  
	         StringEntity se = new StringEntity(requestXml,"utf-8");  
            httppost.setEntity(se);  
            CloseableHttpResponse responseEntry = httpclient.execute(httppost);  
           
            HttpEntity entity = responseEntry.getEntity();  
            StringBuffer responseBody = new StringBuffer();
            InputStream in  =  entity.getContent();
            BufferedInputStream bin = new BufferedInputStream(in);
			byte [] _b = new byte[1024*10];
			int length = 0;
			while((length = bin.read(_b))>0){
				responseBody.append(new String(_b,0,length,"utf-8"));
			}
			in.close();
			bin.close();
			respXml = responseBody.toString();
			logger.info("微信 orderId:"+payBean.getOrderId()+",transfers respXml="+respXml);
			if(StringUtils.isEmpty(respXml)){
				logger.warn("微信 transfers 下单失败 下单响应为空 requestXml:"+requestXml+",respXml:"+respXml);
				return  JsonUtils.commonJsonReturn("0001","转账申请失败");
			}
			Element root = XmlHelper.getField(respXml);
			Element returnCodeEle = XmlHelper.child(root, "return_code");
			String return_code =  returnCodeEle!=null ? returnCodeEle.getText():null;
			Element resultCodeEle = XmlHelper.child(root, "result_code");
			String result_code =  resultCodeEle!=null ? resultCodeEle.getText():null;
			if(!"SUCCESS".equals(return_code) || !"SUCCESS".equals(result_code)){
				logger.error("微信转账失败 下单响应为空,payRecord="+payBean);
				return  JsonUtils.commonJsonReturn("0001","微信转账失败");
			}else{
				JSONObject result =  JsonUtils.commonJsonReturn();
				JsonUtils.setBody(result, "respMsg", respXml);
				return result;
			}
		}catch(Exception e){
			if(e instanceof org.apache.commons.httpclient.NoHttpResponseException){
				logger.error("orderId:"+payBean.getOrderId()+  ",requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result = JsonUtils.commonJsonReturn("0001","转账申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			else if(e instanceof org.apache.commons.httpclient.ConnectTimeoutException){
				logger.error("orderId:"+payBean.getOrderId()+  ",连接超时 requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result = JsonUtils.commonJsonReturn("0001","转账申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			//数据等待失败 需要确认
			else if(e instanceof java.net.SocketTimeoutException){
				logger.error("orderId:"+payBean.getOrderId()+  ",数据等待失败 requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result =  JsonUtils.commonJsonReturn("1000","转账申请结果未知:需要确认");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
			else if(e instanceof java.net.SocketException){
				logger.error("orderId:"+payBean.getOrderId()+  ",requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result =JsonUtils.commonJsonReturn("1000","转账申请结果未知:需要确认");			
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}else{
				logger.error("orderId:"+payBean.getOrderId()+  ",requestXml:"+requestXml+", respXml:"+respXml,e);
				JSONObject result =JsonUtils.commonJsonReturn("1000","转账申请结果未知:需要确认");			
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}
		}
	}
	/**
	 *  查询企业付款API
	 * @param partner_trade_no
	 * @return 0001 转账(申请)失败;0000 转账成功; 1000 PROCESSING 需再一次确认
	 */
	public static JSONObject gettransferinfo(String partner_trade_no){
		String requestXml = null;
		String respXml = null;
		try{
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			if(uuid.length()>32){
				uuid = uuid.substring(0, 32);
			}
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("appid", ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId"));   
			parameters.put("mch_id", ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.merId"));   
			parameters.put("partner_trade_no", partner_trade_no);   //商户调用企业付款API时使用的商户订单号
			Collection<String> keyset= parameters.keySet();  
	   	 	List<String> list = new ArrayList<String>(keyset);  
	        //对key键值按字典升序排序  
	        Collections.sort(list);  
	        SortedMap<String,String> sortMap = new  TreeMap<String,String>();
	        for (int i = 0; i < list.size(); i++) {  
	       	 sortMap.put(list.get(i), parameters.get(list.get(i)));
	        }  
			String sign =createMd5Sign( sortMap);
			Assert.isTrue( StringUtils.isNotEmpty(sign)  );
			sortMap.put("sign", sign);
			requestXml = getWeixinRequestXml(sortMap);
			logger.info("微信 gettransferinfo requestXML="+requestXml);
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			 char[] passwd = parameters.get("mch_id").toCharArray();
			 String wxCertFile = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.cert.apiclient.file") ;
			 FileInputStream instream = new FileInputStream(new File(wxCertFile));  
	         keyStore.load(instream,passwd);  
	         final SSLContext sslcontext = org.apache.http.ssl.SSLContexts.custom().loadKeyMaterial(keyStore,  passwd ).build();  
	         //SSLConnectionSocketFactory(javax.net.ssl.SSLContext, String [], String [], javax.net.ssl.HostnameVerifier)
	         @SuppressWarnings("deprecation")
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(  sslcontext, new String[] { "TLSv1" }, null,  SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);  
	         CloseableHttpClient httpclient = HttpClients.custom() .setSSLSocketFactory(sslsf).build();
	         HttpPost httppost = new HttpPost(   "https://api.mch.weixin.qq.com/secapi/pay/refund");  
	         StringEntity se = new StringEntity(requestXml,"utf-8");  
            httppost.setEntity(se);  
            CloseableHttpResponse responseEntry = httpclient.execute(httppost);  
            HttpEntity entity = responseEntry.getEntity();  
            StringBuffer responseBody = new StringBuffer();
            InputStream in  =  entity.getContent();
            BufferedInputStream bin = new BufferedInputStream(in);
			byte [] _b = new byte[1024*10];
			int length = 0;
			while((length = bin.read(_b))>0){
				responseBody.append(new String(_b,0,length,"utf-8"));
			}
			in.close();
			bin.close();
			respXml = responseBody.toString();
			logger.info("微信 gettransferinfo respXml="+respXml);
			Element root = XmlHelper.getField(respXml);
			Element returnCodeEle = XmlHelper.child(root, "return_code");
			String return_code =  returnCodeEle!=null ? returnCodeEle.getText():null;
			Element resultCodeEle = XmlHelper.child(root, "result_code");
			String result_code =  resultCodeEle!=null ? resultCodeEle.getText():null;
			if(!"SUCCESS".equals(return_code) || !"SUCCESS".equals(result_code)){
				logger.warn("微信 transfers 查询明确失败 requestXml:"+requestXml+";respXml:"+respXml);
				JSONObject result =   JsonUtils.commonJsonReturn("0001","转账申请失败");
				JsonUtils.setBody(result, "respMsg", respXml);
				return  result;
			}else{
				Element statusEle = XmlHelper.child(root, "status");
				String status =  statusEle!=null ? statusEle.getText():null;
				if(StringUtils.equals("SUCCESS", status)){
					JSONObject result =   JsonUtils.commonJsonReturn("0000","SUCCESS");
					JsonUtils.setBody(result, "respMsg", respXml);
					return  result;
				}
				else if(StringUtils.equals("FAILED", status)){
					JSONObject result =   JsonUtils.commonJsonReturn("0001","FAILED");
					JsonUtils.setBody(result, "respMsg", respXml);
					return  result;
				}
				else if(StringUtils.equals("PROCESSING", status)){
					JSONObject result =   JsonUtils.commonJsonReturn("1000","PROCESSING");
					JsonUtils.setBody(result, "respMsg", respXml);
					return  result;
				}
				else{
					logger.warn("响应参数无法解析 partner_trade_no:"+partner_trade_no+",requestXml:"+requestXml+";respXml:"+respXml);
					throw new IllegalArgumentException("响应参数无法解析");
				}
			}
		}catch(Exception e){
			logger.error("响应参数无法解析 partner_trade_no:"+partner_trade_no+",requestXml:"+requestXml+";respXml:"+respXml,e);
			JSONObject result =JsonUtils.commonJsonReturn("1000","结果未知:需要确认");			
			JsonUtils.setBody(result, "respMsg", respXml);
			return  result;
		}
	}
	
	private  static String getWeixinRequestXml(SortedMap<String, String> parameters)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set<Entry<String, String>> es = parameters.entrySet();
		Iterator<Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
		}
		sb.append("</xml>");
		return sb.toString();
	}

	private  static String createMd5Sign(SortedMap<String, String> parameters) {
		try {
			StringBuffer sb = new StringBuffer();
			Set<Entry<String, String>> es = parameters.entrySet();
			Iterator<Entry<String, String>> it = es.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry<String, String>) it
						.next();
				String k = entry.getKey();
				String v = entry.getValue();
				if (StringUtils.isNotEmpty(v) && !"sign".equals(k)
						&& !"key".equals(k) && !k.startsWith("_")) {
					sb.append(k + "=" + v + "&");
				}
			}
			sb.append("key="+ ResourceUtils.getValue(ResourceUtils.LMYCORE,"fs.wechat.mer.appkey"));
			logger.debug("微信参与验签value:" + sb.toString());
			return MD5Util.MD5Encode(sb.toString(), "utf-8").toUpperCase();
		} catch (Exception e) {
			logger.error("微信统一下单 签名出错parameters=" + parameters, e);
			return null;
		}
	}
	
	
	
	/**
	 *  0000 支付成功; 0001 支付失败;1000 需要再次确认;
	 */
	public static JSONObject analysitWeiXinQueryResp(String respXml){
		try{
			Element root = XmlHelper.getField(respXml);
			String return_code =  XmlHelper.child(root, "return_code").getText();  // 此字段是通信标识，非交易标识，交易是否成功需要查看trade_state来判断  SUCCESS/FAIL 
			//String result_code = XmlHelper.child(root, "result_code")!=null ? XmlHelper.child(root, "result_code").getText():null;  // 业务结果  SUCCESS/FAIL 
			String trade_state = XmlHelper.child(root, "trade_state")!=null ? XmlHelper.child(root, "trade_state").getText():null;
			String trade_state_desc = XmlHelper.child(root, "trade_state_desc")!=null ? XmlHelper.child(root, "trade_state_desc").getText():null;  //支付失败的时候有
			//已下参数 在支付成功的情况下有....  begin
			Element bank_type_ele = XmlHelper.child(root, "bank_type");
			String bank_type = bank_type_ele!=null ? bank_type_ele.getText():null;
			Element transaction_id_ele = XmlHelper.child(root, "transaction_id");
			String transaction_id = transaction_id_ele!=null ? transaction_id_ele.getText():null;
			//已上参数 在支付成功的情况下有....  end
			
			if("SUCCESS".equals(return_code)){
				//SUCCESS—支付成功 
				if("SUCCESS".equals(trade_state)){
					logger.info("支付成功respXml="+respXml);
					JSONObject result = JsonUtils.commonJsonReturn();
					JsonUtils.setBody(result, "bank_type", bank_type);
					JsonUtils.setBody(result, "transaction_id", transaction_id);
					return result;
				}
				//REFUND—转入退款  NOTPAY—未支付 CLOSED—已关闭  REVOKED—已撤销（刷卡支付）PAYERROR--支付失败(其他原因，如银行返回失败)
				else if("REFUND".equals(trade_state) || "NOTPAY".equals(trade_state) || "CLOSED".equals(trade_state)|| "REVOKED".equals(trade_state) ||  "PAYERROR".equals(trade_state)){
					logger.info("支付失败respXml="+respXml);
					JSONObject result = JsonUtils.commonJsonReturn("0001","支付失败");
					JsonUtils.setBody(result, "bank_type", bank_type);
					JsonUtils.setBody(result, "transaction_id", transaction_id);
					JsonUtils.setBody(result, "trade_state", trade_state);
					JsonUtils.setBody(result, "trade_state_desc", trade_state_desc);
					return result;
				}
				//USERPAYING--用户支付中 ,
				else if("USERPAYING".equals(trade_state) ){
					logger.error("支付中状态,等待下次确认 此处不应该有支付中订单 respXml="+respXml);
					return JsonUtils.commonJsonReturn("1000","需要再次确认");
				}else{
					logger.warn("未知支付状态 等待下次确认 respXml="+respXml);
					return JsonUtils.commonJsonReturn("1000","需要再次确认");
				}
			}else{
				return JsonUtils.commonJsonReturn("1000","需要再次确认");
			}
		}catch(Exception e){
			logger.error("respXml:"+respXml, e);
			return JsonUtils.commonJsonReturn("1000", "需要再次确认");
		}
	}
	
	
	
	
	
	/**
	 * @param respXml  eg :<xml><appid><![CDATA[wxf3b757c9aa106b04]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[2]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[1457841802]]></mch_id><nonce_str><![CDATA[884d33b75d0d40f9a7fd122910d05edc]]></nonce_str><openid><![CDATA[oCpYx1OpdaLOIadVtMMHyGX_bca4]]></openid><out_trade_no><![CDATA[201704161600000110]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[82E5157FCE276FED7A620B27419AC4B9]]></sign><time_end><![CDATA[20170416164607]]></time_end><total_fee>2</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4000852001201704167230430405]]></transaction_id></xml>
	 * @return  0001 验签失败; 0002 需要再次通知/确认;  0003 支付失败 ; 1000 	支付成功;	9999 系统错误 ;
	 */
	public static JSONObject analysisPayNotify(String respXml){
		try{
			SortedMap<String, String> sortedMap = weiXinNotifyRespGetMap1(respXml);
			boolean checkSign = varifyWeiXinNotifySign1(sortedMap);
			if(!checkSign){
				logger.warn("还款微信异步回调响应 验签不通过:respXml="+respXml);
				return JsonUtils.commonJsonReturn("0001", "验签失败");
			}
			String return_code =sortedMap.get("return_code") ;  //SUCCESS/FAIL 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
			String result_code =sortedMap.get("result_code") ;
			String transaction_id = sortedMap.get("transaction_id") ;  //微信返回 微信端订单号 
			String bank_type = sortedMap.get("bank_type") ;  //微信返回 支付银行
			JSONObject result = null;
			if("SUCCESS".equals( return_code)){
				if("SUCCESS".equals(result_code)){
					result =  JsonUtils.commonJsonReturn("1000","支付成功");
				}else if("FAIL".equals(  result_code)){
					result =  JsonUtils.commonJsonReturn("0003","支付失败");
				}else{
					logger.warn("通知返回未知状态respXml="+respXml);
					result =  JsonUtils.commonJsonReturn("0002","需要再次通知/确认");
				}
			}else{
				result =  JsonUtils.commonJsonReturn("0002","需要再次通知/确认");
			}
			JsonUtils.setBody(result, "transaction_id", transaction_id);
			JsonUtils.setBody(result, "bank_type", bank_type);
			JsonUtils.setBody(result, "out_trade_no", sortedMap.get("out_trade_no"));
			return result;
		}catch(Exception e){
			logger.error("respXml="+respXml,e  );
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	
	private  static boolean varifyWeiXinNotifySign1(SortedMap< String, String> sortedMap){
		String sign = createMd5Sign(sortedMap);
		logger.debug("微信响应本地sigin="+sign);
		logger.debug("微信响应微信sigin="+sortedMap.get("sign"));
		return sign.trim().equals(sortedMap.get("sign"));
	}
	
 	private static SortedMap<String, String> weiXinNotifyRespGetMap1(String respXml) throws Exception {
		Map<String, String> parameters = new HashMap<String,String>();
		Element root = XmlHelper.getField(respXml);
		Iterator<Element> eleIte = root.elementIterator();
		while (eleIte.hasNext()) {
			Element ele = eleIte.next();
			String key = ele.getName();
			String value = ele.getText();
			parameters.put(key, StringUtils.trim(value));
		}
		
		Collection<String> keyset= parameters.keySet();  
		ArrayList<String> list = new ArrayList<String>(keyset);  
        //对key键值按字典升序排序  
        Collections.sort(list);
        logger.debug("微信响应对key键值按字典升序排序list="+list);
        SortedMap<String,String> sortMap = new  TreeMap<String,String>();
        for (int i = 0; i < list.size(); i++) {  
       	 sortMap.put(list.get(i), parameters.get(list.get(i)));
        }  
        logger.debug("微信响应sortMap="+sortMap);
		return sortMap;
	}
 	
	public static JSONObject sendTextMsg1(String touser, String template_id,String clickUrl, JSONObject data) {
		try {
			JSONObject requestJson = new JSONObject();
			requestJson.put("touser", touser);
			requestJson.put("template_id", template_id);
			requestJson.put("url", clickUrl);
			requestJson.put("data", data);
			logger.debug(requestJson);
			HttpService httpService = new HttpService();
			String resp = httpService.doPostRequestEntity("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+ getAccessToken1(), requestJson.toJSONString(),	false, "utf-8");
			if (StringUtils.isEmpty(resp)) {
				return JsonUtils.commonJsonReturn("0001", "微信信息推送失败");
			}
		    // eg : {"errcode":0,   "errmsg":"ok", "msgid":200228332  }
			JSONObject respJson = JSON.parseObject(resp);
			String errcode = respJson.getString("errcode");
			JSONObject result = ( StringUtils.equals("0", errcode) )  ? JsonUtils.commonJsonReturn() : JsonUtils.commonJsonReturn("0001", "微信信息推送失败");
			JsonUtils.setBody(result, "errcode", errcode);
			JsonUtils.setBody(result, "errmsg", respJson.getString("errmsg"));
			JsonUtils.setBody(result, "msgid", respJson.getString("msgid"));
			return result;
		} catch (Exception e) {
			if (e instanceof org.apache.commons.httpclient.NoHttpResponseException) {
				logger.error("微信信息推送失败 touser:" + touser + ",template_id:"+ template_id + ",clickUrl:" + clickUrl + ",data:"+ data, e);
				return JsonUtils.commonJsonReturn("0001", "微信信息推送失败");
			} else if (e instanceof org.apache.commons.httpclient.ConnectTimeoutException) {
				logger.error("微信信息推送失败 touser:" + touser + ",template_id:"+ template_id + ",clickUrl:" + clickUrl + ",data:"+ data, e);
				return JsonUtils.commonJsonReturn("0001", "微信信息推送失败");
			} else {
				logger.error("微信信息推送失败 touser:" + touser + ",template_id:"+ template_id + ",clickUrl:" + clickUrl + ",data:"+ data, e);
				return JsonUtils.commonJsonReturn("1000", "微信信息推送结果未知");
			}
		}
	}
}
