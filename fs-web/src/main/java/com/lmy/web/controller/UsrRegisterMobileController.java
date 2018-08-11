package com.lmy.web.controller;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.AlidayuSmsFacadeImpl;
import com.lmy.core.service.impl.TencentSmsFacadeImpl;
import com.lmy.core.service.impl.UsrQueryImpl;
import com.lmy.core.service.impl.UsrServiceImpl;
import com.lmy.core.utils.FsEnvUtil;
import com.lmy.web.common.SessionUtil;
import com.lmy.web.common.WebUtil;
@Controller
public class UsrRegisterMobileController {
	private static Logger logger = Logger.getLogger(UsrRegisterMobileController.class);
	private static final String sendSmsResultSessionKey = CacheConstant.FS_USR_REG_MOBILE_SEDN_CODE_RESULT;
	private static final String checkSmsResultSessionKey = CacheConstant.FS_USR_REG_MOBILE_CHECK_CODE_RESULT;
	
	@Autowired
	private UsrQueryImpl usrQueryImpl;
	@Autowired
	private UsrServiceImpl usrServiceImpl;
	@RequestMapping(value="/usr/register/mobile_nav")
	public String mobile_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "backUrl" , required = true) String backUrl) {
		modelMap.put("backUrl", backUrl);
		return "/usr/register/register";
	}
	
	@RequestMapping(value="/usr/register/mobile_sent_code",method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String mobile_sent_code(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "mobile" , required = true) String mobile
			) {
		if(!CommonUtils.checkForMobile(mobile)){
			logger.warn("mobile:"+mobile+", 手机格式错误");
			return JsonUtils.commonJsonReturn("0001","手机格式错误").toJSONString();
		}
		boolean isReg = usrQueryImpl.isMobileRegister(mobile);
		if(isReg){
			logger.warn("mobile:"+mobile+", 已被注册");
			return JsonUtils.commonJsonReturn("1000","已被注册").toJSONString();
		}
		JSONObject check1Result	 = send_sms_check1( request);
		if(!JsonUtils.equalDefSuccCode(check1Result)){
			return check1Result.toJSONString();
		}
		JSONArray jarray =(JSONArray) JsonUtils.getBodyValue(check1Result, "jarray");
		boolean sendSmsResult = sendSms(mobile, jarray, request);
		if(sendSmsResult){
			return JsonUtils.commonJsonReturn().toJSONString();		
		}else{
			logger.warn("mobile:"+mobile+", 验证码发送失败");
			return JsonUtils.commonJsonReturn("0005","验证码发送失败").toJSONString();
		}
	}
	

	@RequestMapping(value="/usr/register/mobile_check_code",method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String mobile_check_code(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "mobile" , required = true) String mobile
			,@RequestParam(value = "code" , required = true) String code
			,@RequestParam(value = "source" , required = false) String source
			) {
		JSONObject checkbase1Result  = 	edit_mobile_check_base1(mobile, code, request);
		if(! JsonUtils.equalDefSuccCode(checkbase1Result) ){
			logger.warn(checkbase1Result);
			return checkbase1Result.toJSONString();
		}
		//最近发送短信 详情
		JSONObject lastSendInfo =(JSONObject) JsonUtils.getBodyValue(checkbase1Result, "lastSendInfo");
		//校验 频率控制
		JSONObject checkFrequency  = edit_mobile_check_frequency(request);
		JSONObject checkResult =(JSONObject) JsonUtils.getBodyValue(checkFrequency, "checkResult");
		JSONArray checkFailureResultList =(JSONArray) JsonUtils.getBodyValue(checkFrequency, "checkFailureResultList");
		Integer curErrorTimes =(Integer) JsonUtils.getBodyValue(checkFrequency, "curErrorTimes");
		if(curErrorTimes>6){
			return JsonUtils.commonJsonReturn("0005","今日已锁定").toJSONString();
		}
		// check
		if(! JsonUtils.equalDefSuccCode(checkFrequency)   ){
			logger.info("【用户修注册手机号码】step2校验失败lastSendMsgInfo:"+lastSendInfo+",当前传入手机号码:"+mobile+",当前传入手机验证码:"+code);
			handCheckSmsFailure(checkFailureResultList, curErrorTimes, checkResult, request);
			return checkFrequency.toJSONString();
		}
		if(	!(StringUtils.equals( code , lastSendInfo.getString("code"))  && StringUtils.equals( mobile , lastSendInfo.getString("mobile")))  ){
			logger.info("【用户注册手机号码】step3校验失败lastSendMsgInfo:"+lastSendInfo+",当前传入手机号码:"+mobile+",当前传入手机验证码:"+code);
			handCheckSmsFailure(checkFailureResultList, curErrorTimes, checkResult, request);
			return JsonUtils.commonJsonReturn("0004","校验失败").toJSONString();
		}
		FsUsr usr  = SessionUtil.getFsUsr(request);
		JSONObject regMobileResult =  usrServiceImpl.usrRegMobileWithNoCheck(usr.getId(), mobile, source);
		if(JsonUtils.equalDefSuccCode(regMobileResult)){
			logger.info("【用户注册手机号码】  注册成功 开始 清除redis , 更新 session fsUsr 信息");
			//WebUtil.removeSessionKey(request, sendSmsResultSessionKey);
			//WebUtil.removeSessionKey(request, checkSmsResultSessionKey);
			usr.setRegisterMobile(mobile).setUpdateTime(new Date()).setRegisterTime(new Date());
			SessionUtil.mainSession(request, "usr", usr);
			RedisClient.delete( sendSmsResultSessionKey+usr.getId()  ,  checkSmsResultSessionKey + usr.getId());			
		}
		return regMobileResult.toJSONString();
	}
	private void handCheckSmsFailure(JSONArray checkFailureResultList , Integer curErrorTimes , JSONObject checkResult ,HttpServletRequest request){
		if(checkFailureResultList == null ){
			checkFailureResultList = new JSONArray();
		}
		if(checkResult == null ){
			checkResult = new JSONObject();
		}
		if(curErrorTimes == null || curErrorTimes < 1){
			curErrorTimes = 0;
		}
		JSONObject curCheckResult = new JSONObject();
		curCheckResult.put("time", new Date());
		checkFailureResultList.add(curCheckResult);
		checkResult.put("curErrorTimes", curErrorTimes +  1);
		checkResult.put("errorList", checkFailureResultList);
		//WebUtil.maintainSession(request, checkSmsResultSessionKey, checkResult);
		RedisClient.set(checkSmsResultSessionKey + WebUtil.getUserId(request), checkResult, 24 * 60  * 60) ;
	}
	
	private JSONObject edit_mobile_check_base1(String mobile , String code  , HttpServletRequest request){
		Date now = new Date();
		if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code) ){
			logger.warn("【用户注册手机号码】 参数错误手机号码/短信验证码未空 reqParams="+WebUtil.getRequestParams(request));
			return JsonUtils.commonJsonReturn("0001","非法请求");
		}
		//发送记录   
		JSONArray jarray =	(JSONArray)RedisClient.get(sendSmsResultSessionKey + WebUtil.getUserId(request))	;	//(JSONArray) 	WebUtil.getFromSession(request,sendSmsResultSessionKey )	;
		if(CollectionUtils.isEmpty(jarray)){
			logger.warn("【用户注册手机号码】 没有短信发送记录reqParams="+WebUtil.getRequestParams(request) ) ;
			return JsonUtils.commonJsonReturn("0001","请先获取短信验证码");
		}
		//上一次发送短信记录
		JSONObject last = jarray.getJSONObject( jarray.size()-1 );
		if(last == null){
			logger.warn("【用户注册手机号码】没有短信发送记录 非法请求 reqParams="+WebUtil.getRequestParams(request) ) ;
			return JsonUtils.commonJsonReturn("0001","请先获取短信验证码");
		}
		if(! StringUtils.equals(  mobile, last.getString("mobile")) ){
			logger.warn("【用户注册手机号码】非法请求 手机号码不一致reqParams="+WebUtil.getRequestParams(request)+ ",last="+last);
			return JsonUtils.commonJsonReturn("0001","注册异常，请刷新后重试");
		}
		//码的有效时长  校验 加上时间 2016/10/17 16:09  BEGIN
		int checkCodeEffectiveSec = 90;
		if(  last.getDate("time")!=null &&  CommonUtils.calculateDiffSeconds( now , last.getDate("time") ) > checkCodeEffectiveSec  )   {
			logger.warn("【用户注册手机号码】验证码已过有效期 上一次发送详情:"+last+",WebUtil.calculateDiffSeconds(now, last.getDate('time') ) =" +CommonUtils.calculateDiffSeconds(now, last.getDate("time") )+">"+checkCodeEffectiveSec+"秒");
			return JsonUtils.commonJsonReturn("0006","验证码已过期");  //请重新发送验证码 
		}
		//码的有效时长  校验 加上时间 2016/10/17 16:09  end
		JSONObject result =  JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "lastSendInfo", last);
		JsonUtils.setBody(result, "sendListInfo", jarray);
		return result;
	}
	
	private boolean sendSms(String mobile ,JSONArray jarray ,HttpServletRequest request){
		String  code =CommonUtils.getRandom6Code();
		JSONObject curSms = new JSONObject();
		curSms.put("code", code);
		curSms.put("mobile", mobile);
		curSms.put("time", new Date() );
		jarray.add(curSms);
//		JSONObject smsParamJson = new JSONObject();
//		smsParamJson.put("code",code);
//		smsParamJson.put("product","雷门易");

        JSONArray params = new JSONArray();
        params.add(code);
        params.add("雷门易");
        
		boolean smsSendResult = false;
		if(FsEnvUtil.isDev()){
			//logger.info(FsEnvUtil.getEnv() + "环境模拟发送短信,内容:"+smsParamJson.toJSONString()+"结果:"+smsSendResult);
		}
		else if(!FsEnvUtil.isDev()){
			//smsSendResult = AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, mobile, "SMS_68210019 " , null);
		}
		smsSendResult = TencentSmsFacadeImpl.sendSms(119228, params, mobile);
//		smsSendResult = AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, mobile, "SMS_68210019 " , null);
		if(smsSendResult){
			//发送短信验证码 
//			request.getSession().setAttribute(sendSmsResultSessionKey, jarray);
			logger.info("=====短信发送成功，"+jarray.toJSONString()+"=====");
			RedisClient.set(sendSmsResultSessionKey + WebUtil.getUserId(request), jarray, 91);
		}
		return smsSendResult;
	}
	
	private JSONObject send_sms_check1( HttpServletRequest request){
		//发送手机验证码
		JSONArray jarray = (JSONArray)RedisClient.get(sendSmsResultSessionKey + WebUtil.getUserId(request))	;	// (JSONArray)	WebUtil.getFromSession(request, sendSmsResultSessionKey)	 ;
		if(CollectionUtils.isEmpty(jarray)){
			jarray = new JSONArray();
		}
		JSONObject last = CollectionUtils.isNotEmpty(jarray)	?	(JSONObject) jarray.get( jarray.size() -1 ) : new JSONObject();
		//90秒有效
		Date now = new Date();
		Date lastTime = ( last == null ? null : (Date)last.get("time") ) ;
		int allowIntervalSec = 90;
		if(lastTime !=null && 	CommonUtils.calculateDiffSeconds(now, lastTime ) < allowIntervalSec){
			logger.warn("【用户注册手机号码】短信发送请求过于频繁，本次拒接  "+WebUtil.getRequestParams(request) +",上一次发送短信于"+CommonUtils.calculateDiffSeconds(now, lastTime ) +"秒前 <"+allowIntervalSec+"秒" );
			return JsonUtils.commonJsonReturn("0003","过于频繁");
		}
		JSONObject  frequencyResult =  this.edit_mobile_check_frequency(request);
		JSONObject result =  JsonUtils.commonJsonReturn();
		if(!JsonUtils.equalDefSuccCode(frequencyResult)){
			long unlockRemainingTime = allowIntervalSec - CommonUtils.calculateDiffSeconds(now, lastTime ) ;
			logger.warn("【用户注册手机号码】短信发送 check 已锁定 "+unlockRemainingTime+",后解锁" );
			JsonUtils.setBody(result, "unlockRemainingTime",unlockRemainingTime); //解锁剩余秒数
			JsonUtils.setHead(result, "0003","已锁定");
		}
		JsonUtils.setBody(result, "jarray", jarray);
		return result;
	}
	
	private JSONObject edit_mobile_check_frequency( HttpServletRequest request){
		Date now = new Date();
		//校验失败频率控制
		JSONObject checkResult=   null ;//(JSONObject) WebUtil.getFromSession(request, checkSmsResultSessionKey)	;
		if(checkResult == null ){
			checkResult = new JSONObject();
		}
		JSONArray checkFailureResultList = checkResult.getJSONArray("errorList")  ;
		int curErrorTimes = checkResult.getInteger( "curErrorTimes" ) !=null ? checkResult.getInteger( "curErrorTimes" ) : 0 ;
		JSONObject lastCheck = 	CollectionUtils.isNotEmpty(checkFailureResultList) ?	checkFailureResultList.getJSONObject( checkFailureResultList.size() - 1  ) : new JSONObject();
		Date lastCheckTime = ( lastCheck!=null ?	lastCheck.getDate("time") : null) ;
		int allowIntervalSec = 60 * 60 * 2;
		JSONObject result =  JsonUtils.commonJsonReturn();
		if(curErrorTimes > 2  &&   (lastCheckTime != null &&   CommonUtils.calculateDiffSeconds(now, lastCheckTime ) < allowIntervalSec  )){
			logger.warn("【用户注册手机号码】校验验证码过于频繁已锁定"+allowIntervalSec+" 秒后再试 reqParams="+WebUtil.getRequestParams(request)
					+",上一次校验发生在"+ CommonUtils.calculateDiffSeconds(now, lastCheckTime )+"秒前<" +allowIntervalSec+"秒"  );
			 lastCheck.put("time", now);
			 JsonUtils.setHead(result, "0003","已锁定");
		}
		JsonUtils.setBody(result, "checkResult", checkResult);
		JsonUtils.setBody(result, "lastCheckResult", lastCheck);
		JsonUtils.setBody(result, "curErrorTimes", curErrorTimes);
		JsonUtils.setBody(result, "checkFailureResultList", checkFailureResultList);
		return result;
	}
	
	
	
}
