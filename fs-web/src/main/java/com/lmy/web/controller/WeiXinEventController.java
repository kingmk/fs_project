package com.lmy.web.controller;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.constant.WechatConstants;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;
import com.lmy.core.utils.FsExecutorUtil;
import com.lmy.web.common.WebUtil;
@Controller
public class WeiXinEventController {
	private static Logger logger = Logger.getLogger(WeiXinEventController.class);
	@RequestMapping(value = "/MP_verify_aAvBoLsv08VYqYhV.txt")
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@ResponseBody
	public String wxverify(HttpServletResponse response, HttpServletRequest request, ModelMap modelMap) throws IOException {
		return "aAvBoLsv08VYqYhV";
	}
	@RequestMapping(value = "/weixin/event/receive")
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@ResponseBody
	public String receive(HttpServletResponse response, HttpServletRequest request, ModelMap modelMap) throws IOException {
		String respXml = WebUtil.getReqData(request);
		if(logger.isDebugEnabled()){
			logger.debug("respXml="+respXml);
		}	
		String method = request.getMethod();
		if(logger.isDebugEnabled()){
			logger.debug("method="+method);
		}
		String token = "989f7b28d030fjjren15454refdjfjd";
		//logger.info(WebUtil.getRequestParams(request));
		if (method.equals("GET")) {
			try {
				// 微信加密签名
			} catch (Exception e) {
				logger.error("微信事件推送处理错误 token:"+token, e);
			}
			return WebUtil.getRequestParams(request).getString("echostr");
		} else {
			try {
				// 将请求、响应编码均设置为UTF-8
				Map<String, String> requestMap = parseXml(respXml);
				// 消息类型
				String msgType = requestMap.get("MsgType");
				String openId = requestMap.get("FromUserName");
				if (WechatConstants.REQ_MESSAGE_TYPE_EVENT.equals(msgType)) {
					String event = requestMap.get("Event");
					asynUpdateUserInfo(openId);
					if (WechatConstants.EVENT_TYPE_SUBSCRIBE.equals(event)) {
						logger.info("订阅事件 requestMap="+requestMap);
					} else if (WechatConstants.EVENT_TYPE_CLICK.equals(event)) {
						logger.info("点击菜单拉取消息时的事件推送 requestMap="+requestMap);
						asynUpdateUserInfo(openId);
					}
				}else if(msgType.equals(WechatConstants.REQ_MESSAGE_TYPE_TEXT)){
					logger.info("文本事件推送 requestMap="+requestMap);
				}else if(msgType.equals(WechatConstants.REQ_MESSAGE_TYPE_IMAGE)){
					logger.info("图文事件推送 requestMap="+requestMap);
				}else if(msgType.equals(WechatConstants.REQ_MESSAGE_TYPE_LOCATION)){
					logger.info("地理位置的事件推送 requestMap="+requestMap);
				}else{
					logger.warn("未知事件推送 requestMap="+requestMap);
				}
			} catch (Exception e) {
				logger.error("微信事件推送处理错误", e);
			}
			return "SUCCESS";
		}
	}
	private  void asynUpdateUserInfo(final String openId){
		Thread  thread = new Thread(new Runnable(){
			public void run(){
				try {
					_getAndUpdateUserInfo(openId);
				} catch (Exception e) {
					logger.error("获取用户信息 from weixin exception，openID:"+openId, e);
				}
			}
		 });
		FsExecutorUtil.getExecutor().execute(thread);
	}
	/**
	 * 会自动更新  缓存中 微信信息
	 * userInfo={"subscribe":1,"openid":"osEKMwYRbYFlaiQmu0du1ryMkxKM","nickname":"周兆华","sex":1,"language":"zh_CN","city":"浦东新区","province":"上海","country":"中国","headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/pqLhiccpwO1fqpQqDLSEXIgTGWY5dIfmNNNHBwA1TDeWPTMakKr8nZGpuTKyNlxsSZSFHTKnTCmMLia2jOLtGY2IwQpU21geF3\/0","subscribe_time":1456890469,"remark":"","groupid":0}
	 * @param openId
	 * @return
	 */
	private  JSONObject _getAndUpdateUserInfo(String openId) {
		try {
			//第一步 从缓存中获取
			JSONObject info = (JSONObject)RedisClient.hget(CacheConstant.FS_WEIXIN_INFO1_HKEY, openId);
			if(info==null || (info.getDate("_cacheTime")!=null && !DateUtils.isSameDay(new Date(), info.getDate("_cacheTime")))  ||! info.containsKey("subscribe")){
				logger.debug("同步取得微信用户信息...");
				JSONObject obj = WeiXinInterServiceImpl.getUserInfo1(openId);
				if(obj.containsKey("subscribe") && 1==obj.getIntValue("subscribe")){
					obj.put("_cacheTime",new Date());
					RedisClient.hset(CacheConstant.FS_WEIXIN_INFO1_HKEY, openId, obj);
					return obj;
				}else{
					return info;
				}
			}else{
				logger.debug("缓存中命中:info="+info);
				return info;
			}
		} catch (Exception e) {
			logger.error("openId="+openId,e);
			return null;
		}
	}
	
	
	public static boolean checkSignature(String signature,String timestamp,String nonce,String token){
		String[] param = new String[]{token, timestamp, nonce};
		Arrays.sort(param);
		String temp = param[0].concat(param[1]).concat(param[2]);
		return DigestUtils.shaHex(temp).equals(signature);
	}
	@SuppressWarnings("unchecked")
	private Map<String, String> parseXml(String receivedXml) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();
		// 读取输入流
		Document document = DocumentHelper.parseText(receivedXml);
		// 得到xml根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList)
			map.put(e.getName(), e.getText());
		// 释放资源
		return map;
	}
}
