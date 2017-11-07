package com.lmy.core.service.impl;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.common.utils.SecretConvert;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsUsr;
/**
 * @author fidel
 * @since 2017/05/01
 */
public class UsrAidUtil {
	private static final Logger  logger = Logger.getLogger(UsrAidUtil.class);
	/*** 并且转义  **/
	public static String getMasterNickName(FsMasterInfo masterInfo , FsUsr usr , String defValue){
		if( masterInfo !=null  ){
			if(StringUtils.isNotEmpty(  masterInfo.getNickName()  )){
				return StringEscapeUtils.escapeHtml4(   masterInfo.getNickName()   );
			}
			else if(StringUtils.isNotEmpty(  masterInfo.getName()  )){
				return StringEscapeUtils.escapeHtml4(   SecretConvert.convertUserName(masterInfo.getName() )  );
			}
			else{
				getNickName(usr, defValue);
			}
		}
		return getNickName(usr, defValue);
	}
	public static String getMasterHeadImg(FsMasterInfo masterInfo , FsUsr usr , String defValue){
		return (masterInfo!=null && StringUtils.isNotEmpty(masterInfo.getHeadImgUrl())) ? masterInfo.getHeadImgUrl() : getUsrHeadImgUrl(usr, "");
	}
	/** 用户自定义昵称>用户英文名>用户真实名>用户注册手机号  **/
	public static String getNickName( FsUsr usr , String defValue){
		if( usr !=null  ){
			if( StringUtils.isNotEmpty( usr.getNickName() ) ){
				return StringEscapeUtils.escapeHtml4(usr.getNickName());
			}
			else if( StringUtils.isNotEmpty( usr.getEnglishName() ) ){
				return StringEscapeUtils.escapeHtml4(usr.getEnglishName());
			}
			else if(   StringUtils.isNotEmpty( usr.getRealName() ) ) {
				return StringEscapeUtils.escapeHtml4(  SecretConvert.convertUserName(usr.getRealName() )  );
			}
			else if(   StringUtils.isNotEmpty( usr.getRegisterMobile() ) ) {
				return StringEscapeUtils.escapeHtml4(  SecretConvert.convertMobile(  usr.getRegisterMobile() )  );
			}
		}
		return defValue;
	}
	/**  微信昵称>用户昵称>用户英文名>用户真实姓名 **/
	public static JSONObject getCacheWeiXinInfo(FsUsr usr){
		if( usr !=null  && StringUtils.isNotEmpty(usr.getWxOpenId()) ){
			return  (JSONObject)RedisClient.hget(CacheConstant.FS_WEIXIN_INFO1_HKEY, usr.getWxOpenId());
		}
		return null;
	}	
	/**  微信昵称>用户昵称>用户英文名>用户真实姓名 并且转义  **/
	public static String getNickName2( FsUsr usr ,JSONObject cacheWeiXinUsrInfo,String defValue){
		String wxNickname = cacheWeiXinUsrInfo !=null ? cacheWeiXinUsrInfo.getString("nickname") : null;
		return StringUtils.isNotEmpty(wxNickname) ? StringEscapeUtils.escapeHtml4(wxNickname)  : getNickName(usr, defValue);
	}
	/**  微信昵称>用户昵称>用户英文名>用户真实姓名 并且转义**/
	public static String getNickName2( FsUsr usr ,  String defValue){
		return getNickName2(usr, getCacheWeiXinInfo(usr), defValue);
	}

	public static String getUsrHeadImgUrl( FsUsr usr , String defValue){
		if(usr!=null && StringUtils.isNotEmpty(usr.getUsrHeadImgUrl())){
			return usr.getUsrHeadImgUrl();
		}
		return defValue;
	}
	
	/** 微信头像> 用户自定义 **/
	public static String getUsrHeadImgUrl2( FsUsr usr , JSONObject cacheWeiXinUsrInfo,String defValue){
		String headimgurl = cacheWeiXinUsrInfo !=null ? cacheWeiXinUsrInfo.getString("headimgurl") : null;
		return StringUtils.isNotEmpty(headimgurl) ? headimgurl : getUsrHeadImgUrl(usr, defValue);
	}
	/** 微信头像> 用户自定义 **/
	public static String getUsrHeadImgUrl2( FsUsr usr , String defValue){
		return getUsrHeadImgUrl2(usr, getCacheWeiXinInfo(usr), defValue);
	}
	public static String getWorkYearStr(Date workDate ){
		if(workDate !=null){
			Long diffDays = CommonUtils.calDiffDays(workDate, new Date());
			Double diffYear = diffDays / 365d;
			return CommonUtils.numberFormat(diffYear, "###,##0", "0");
		}else{
			return null;
		}
	}
	public static Double getEvaluateFromMap(Map map ,String key , Double defVale){
		if(map == null || map.get(key) ==null){
			return defVale;
		}
		return Double.valueOf( map.get( key ) .toString() );
	}
	/**
	 * 是否完善了个人资料
	 * @param masterInfo
	 * @return
	 */
	public static boolean isPerfectPersonalData(FsMasterInfo  masterInfo){
		if(masterInfo == null || "refuse".equals(masterInfo.getAuditStatus()) ){
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getName() )){
			logger.info("姓名为空 需完善个人资料");
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getNickName() )){
			logger.info("昵称为空 需完善个人资料");
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getCertNo() )){
			logger.info("证件号码为空 需完善个人资料");
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getCertImg1Url() ) || StringUtils.isEmpty( masterInfo.getCertImg2Url() )){
			logger.info("证件照为空 需完善个人资料");
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getLiveAddress() ) ){
			logger.info("现居住地为空 需完善个人资料");
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getContactMobile() ) ){
			logger.info("手机号码为空 需完善个人资料");
			return false;
		}
		if(StringUtils.isEmpty( masterInfo.getHeadImgUrl() ) ){
			logger.info("头像为空 需完善个人资料");
			return false;
		}
		/*
		 * 2017/05/24 18:42 确认 从业年限 是非必填资料
		 * if( masterInfo.getWorkDate()==null){
			logger.info("从业年限 需完善个人资料");
			return false;
		}*/		
		if(StringUtils.isEmpty( masterInfo.getAchievement() ) ){
			logger.info("所取得成就为空 需完善个人资料");
			return false;
		}		
		if(StringUtils.isEmpty( masterInfo.getExperience() ) ){
			logger.info("相关经历为空 需完善个人资料");
			return false;
		}		
		if(StringUtils.isEmpty( masterInfo.getGoodAt() ) ){
			logger.info("擅长领域为空 需完善个人资料");
			return false;
		}		
		if(StringUtils.isEmpty( masterInfo.getSchool() ) ){
			logger.info("所学门派为空 需完善个人资料");
			return false;
		}		
		if(StringUtils.isEmpty( masterInfo.getIsFullTime() ) ){
			logger.info("是否专职为空 需完善个人资料");
			return false;
		}		
		if(StringUtils.isEmpty( masterInfo.getIsSignOther() ) ){
			logger.info("是否已签约其他风水平台为空 需完善个人资料");
			return false;
		}		
		return true;
	}
	
}
