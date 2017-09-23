package com.lmy.core.model.dto;
import java.text.DecimalFormat;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

@SuppressWarnings("serial")
public class LoginCookieDto implements java.io.Serializable{
	private static Logger logger = Logger.getLogger(LoginCookieDto.class);
	/** 注册渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站 **/
	public static String USER_CHANNEL_0="0";
	/** 注册渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站 **/
	public static String USER_CHANNEL_1="1";
	/** 注册渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站 **/
	public static String USER_CHANNEL_3="3";
	/** 注册渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站 **/
	public static String USER_CHANNEL_4="4";
	/** 注册渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站 **/
	public static String USER_CHANNEL_5="5";
	
	/** 0 未登录;1 cookie 快速登录(弱登录); 2 服务窗/微信 快速登录(免登录 /弱登录) ; 3 强登录 */
	public static String loginLevel0="0";
	/** 0 未登录;1 cookie 快速登录(弱登录); 2 服务窗/微信 快速登录(免登录 /弱登录) ; 3 强登录 */
	public static String loginLevel1="1";
	/** 0 未登录;1 cookie 快速登录(弱登录); 2 服务窗/微信 快速登录(免登录 /弱登录) ; 3 强登录 */
	public static String loginLevel2="2";
	/** 0 未登录;1 cookie 快速登录(弱登录); 2 服务窗/微信 快速登录(免登录 /弱登录) ; 3 强登录 */
	public static String loginLevel3="3";
	
	
	/**  注册渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站  l**/
	private String enterType;
	/** 1 代表当前第一次登录 **/
	private Integer enterTimes =1;
	/** 0 未登录;1 cookie 快速登录(弱登录); 2 服务窗/微信 快速登录(免登录 /弱登录) ; 3 强登录 */
	private String loginLevel = "0";
	/**版本**/
	private Double version = 1d;

	private String uuid;
	
	private String openId = "";
	
	public Long userId;
	
	public Long createTime;
	
	public LoginCookieDto(){
		enterTimes = 0;
		this.uuid = UUID.randomUUID().toString().replace("_", "");
	}
	
	/**
	 * @return if not exists return null
	 */
	public String aliPayOpenId(){
		if(USER_CHANNEL_1.equals(enterType)){
			return openId;
		}else{
			return null;
		}
	}
	/**
	 * @return if not exists return null
	 */
	public String wxOpenId(){
		if(USER_CHANNEL_0.equals(enterType)){
			return openId;
		}else{
			return null;
		}
	}
	
	public LoginCookieDto(String enterType , Integer enterTimes,String openId){
		this.enterType =  StringUtils.isEmpty(enterType)? USER_CHANNEL_5: enterType;
		this.enterTimes = (enterTimes==null ? enterTimes = 0 : enterTimes);
		this.openId = !StringUtils.isEmpty(openId)? openId:"";
		this.uuid = UUID.randomUUID().toString().replace("_", "");
	}
	
	
	public String toString() {
		return JSON.toJSONString(this,
				SerializerFeature.WriteDateUseDateFormat,
				SerializerFeature.WriteMapNullValue);
	}
	
	public String buildCookieValue(){
		if(StringUtils.isEmpty(uuid)){
			this.uuid = UUID.randomUUID().toString().replace("_", "");
		}
		DecimalFormat d2 = new DecimalFormat("00");
		StringBuffer sb = new StringBuffer();
		sb.append((StringUtils.isEmpty(enterType)? USER_CHANNEL_5: enterType) )  //登录 渠道 0
		.append(","+d2.format( (enterTimes==null ? enterTimes = 0 : enterTimes)))  // 当前登录次数 1
		.append(","+uuid)   //2
		.append(","+(openId!=null ? openId:"")) //开放id 3
		.append(","+(userId!=null ? userId : "")) //用户ID 4
		.append(","+(loginLevel!=null ? loginLevel : "")) // 5
		.append(","+(version!=null ? version : "")) // 6
		.append(","+(createTime!=null ? createTime : System.currentTimeMillis())) // 7
		;
		String value = new String(org.apache.commons.codec.binary.Base64.encodeBase64(sb.toString().getBytes()));
		//随机的前四位
		return "MDEs"+value;
	}
	/**
	 * @param cookieValue
	 * @return 不会返回空的对象
	 */
	public static LoginCookieDto buildDto(String cookieValue){
		try {
			if(!StringUtils.isEmpty(cookieValue)){
				//DecimalFormat d2 = new DecimalFormat("00");
				String  planiVlaue = new String(org.apache.commons.codec.binary.Base64.decodeBase64( cookieValue.substring(4).getBytes()));
				//logger.debug(cookieValue);
				//logger.debug(planiVlaue);
				String [] strArray = planiVlaue.split(",");
				String enterType =strArray[0];
				Integer enterTimes = Integer.parseInt(strArray[1]);
				String uuid  = strArray[2] ;
				String  openId=strArray.length>3?strArray[3]:null;
				String userIdStr =  strArray.length>4?strArray[4]:null;
				String loginLevel = strArray.length>5?strArray[5]:null;
				String versionStr = strArray.length>6?strArray[6]:null;
				String createTimeStr = strArray.length>7?strArray[7]:null;
				Long userId = null;
				Double version = null;
				Long createTime = null;
				if(StringUtils.isNotEmpty(userIdStr)){
					userId = Long.valueOf(userIdStr);
				}
				if(StringUtils.isNotEmpty(versionStr)){
					version = Double.valueOf(versionStr);
				}
				if(StringUtils.isNotEmpty(createTimeStr)){
					createTime = Long.valueOf(createTimeStr);
				}
				return new LoginCookieDto(enterType, enterTimes, openId).setUuid(uuid).setUserId(userId).setLoginLevel(loginLevel).setVersion(version).setCreateTime(createTime);
			}else{
				return new LoginCookieDto();
			}
		} catch (Exception e) {
			logger.error("cookieValue="+cookieValue,e);
			return new LoginCookieDto();
		}
	}
	/**  注册/登录渠道:0 微信;1支付宝;2 ios app;3 android app; 4 其他 app;5 网站   ysh_yjf_user.create_channel **/
	public String getEnterType() {
		return enterType;
	}
	public LoginCookieDto setEnterType(String enterType) {
		this.enterType = enterType;
		return this;
	}
	public Integer getEnterTimes() {
		return enterTimes;
	}
	public LoginCookieDto setEnterTimes(Integer enterTimes) {
		this.enterTimes = enterTimes;
		return this;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getUuid() {
		return uuid;
	}
	public LoginCookieDto setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}
	public Long getUserId() {
		return userId;
	}
	public LoginCookieDto setUserId(Long userId) {
		this.userId = userId;
		return this;
	}
	public String getLoginLevel() {
		return loginLevel;
	}
	public LoginCookieDto setLoginLevel(String loginLevel) {
		this.loginLevel = loginLevel;
		return this;
	}
	public Double getVersion() {
		return version;
	}
	public LoginCookieDto setVersion(Double version) {
		this.version = version;
		return this;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public LoginCookieDto setCreateTime(Long createTime) {
		this.createTime = createTime;
		return this;
	}

	public Boolean getIsLogin() {
		/** 0 未登录;1 cookie 快速登录(弱登录); 2 服务窗/微信 快速登录(免登录 /弱登录) ; 3 强登录 */
		if( StringUtils.isNotEmpty(loginLevel) && !"0".equals(loginLevel) ){
			return true;
		}else{
			return false;
		}
	}

		
	
	public static void main(String[] args) {
		String coolieValue = "01,";
		String value = new String(org.apache.commons.codec.binary.Base64.encodeBase64(coolieValue.getBytes()));
		System.out.println(value);
		//LoginCookieDto lcd = buildDto(coolieValue);
		//System.out.println(lcd);
	}
}
