package com.lmy.common.utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
/**
 *
 */
public  class RSADatagram {
	private static Logger logger = Logger.getLogger(RSADatagram.class);
	/** 15位发起机构号  :保留域:今后可能出现多家商户,需要通过商家号找到商家公钥 */
	private String merId;
	
	/** 8位交易码  : 保留域*/
	private String serverCode;
	
	/**交易请求报文体*/
	private String reqText;
	
	/** 加解密对象*/
	private RSAHelper cipher;
	
	/** 报文主体明文*/
	private String plaintext;
	
	private String charSet = "utf-8" ;
	/**
	 * 构造器
	 * @param merId			商户号
	 * @param serverCode	服务交易码
	 * @param reqXml		请求XML报文(明文)
	 */
	public RSADatagram(String merId, String serverCode, String reqText) {
		this.merId = merId;
		this.serverCode = serverCode;
		this.reqText = reqText;
	}
	
	public RSADatagram(String merId) {
		this.merId = merId;
	}
	
	public RSADatagram() {
	}
	/*private void initKey(String merId) {
		String platPublicKey =ResourceUtils.getValue(ResourceUtils.ELIFE_THIRD, "ebuy.platform.inside.server.rsa.public.key");
		//TODO get it with merId , 每个商家有自己的
		String merPrivateKey = ResourceUtils.getValue(ResourceUtils.ELIFE_THIRD, "jintong.card.check.elife.rsa.private.key");
		initKey(merPrivateKey, platPublicKey);
	}*/
	/**
	 * 初始化加密对象
	 * @param merKeyPath	商户私钥
	 * @param pubKeyPath	系统公钥
	 * 备注： 系统公钥->加密 	商户私钥->签名
	 * @throws Exception 
	 */
    public void initKey(String localPrivKeyBase64Str, String peerPubKeyBase64Str) {
    	try{
        	cipher = new RSAHelper();	
    		cipher.initKey(localPrivKeyBase64Str, peerPubKeyBase64Str, 2048);    		
    	}catch(Exception e){
    		logger.error("localPrivKeyBase64Str="+localPrivKeyBase64Str);
    		logger.error("peerPubKeyBase64Str="+peerPubKeyBase64Str);
    		logger.error(e);
    	}
    }
	
	/**
	 * 功能：报文加密
	 * @return
	 */
	public String encrypt() {
		/*
		 *                                    报文头
		 *                  ____________________________________________
		 * 报文结构：8位报文总长度	15位发起机构号	 8位交易码	  4位签名域长度	  344位签名域值	   n位json报文数据主体密文
		 *        |       | |        |   |    |   |       |   |        |   |            |
		 *        0       7 8        22  23   30  31      34  35       378 379          n
		 */
		try {
			merId = leftAppendSpace(merId,15);
			serverCode = leftAppendSpace(serverCode,8);
			String data = cipher.encrypt(reqText,charSet);
			String sign = cipher.sign(reqText,charSet);
			String signLen = leftAppendZero(sign.length() + "", 4);
			int gramaLen = merId.length() + serverCode.length() + signLen.length() + sign.length() + data.length();
			String gramaLenStr = leftAppendZero(gramaLen + "", 8);
			
			// 组装报文
			return new StringBuilder("")			
			.append(gramaLenStr)	// 8位报文总长度
			.append(merId)				//15位发起机构号
			.append(serverCode)	    // 8位交易码
			.append(signLen)			// 4位签名域长度
			.append(sign)				//签名域值
			.append(data)				// XML报文数据主体密文
			.toString();
		} catch (Exception e) {
			logger.error("【error】【流程：交易报文加密签名】出现未处理异常",e);
		}
		return null;
	}
	/** 左补0 */
	private  String leftAppendZero(String str, int strLength) {
		if(str == null) str = "";
		str = str.trim();
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
	/** 左补空格 */
	private  String leftAppendSpace(String str, int strLength) {
		if(str == null) str = "";
		str = str.trim();
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append(" ").append(str);// 左补空格
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
	/**
	 * 功能：报文验签
	 * @param data	验签报文
	 * @return
	 * @throws Exception
	 */
	public boolean verifySign(String data) throws Exception {
		try{
			// 系统公钥：验签 	商户私钥：解密
			if(StringUtils.isEmpty(data)){
				logger.warn("data="+data);
			}
			String signStr =  data.substring(35, 379);			// 签名域值
			String cipertext = data.substring(379);				// 密文
			plaintext = cipher.decrypt(cipertext,charSet);				// 解密->明文
			boolean flag =  cipher.verify(plaintext, signStr,charSet);
			Assert.isTrue(flag);
			return flag;			
		}catch(Exception e){
			logger.error("data="+data,e);
			return false;
		}
	}
	
	public String getMerId() {
		return merId;
	}
	public RSADatagram setMerId(String merId) {
		this.merId = merId;
		return this;
	}
	public String getServerCode() {
		return serverCode;
	}
	public RSADatagram setServerCode(String serverCode) {
		this.serverCode = serverCode;
		return this;
	}
	public String getReqText() {
		return reqText;
	}
	public RSADatagram setReqText(String reqText) {
		this.reqText = reqText;
		return this;
	}
	public String getPlaintext() {
		return plaintext;
	}
	public RSADatagram setCipher(RSAHelper cipher) {
		this.cipher = cipher;
		return this;
	}
	public RSADatagram setCharSet(String charSet) {
		this.charSet = charSet;
		return this;
	}
	

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
