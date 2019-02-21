package com.lmy.core.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.dao.FsCalendarSolarDao;
import com.lmy.core.dao.FsCalendarUserDao;
import com.lmy.core.dao.FsCalendarUserinfoDao;
import com.lmy.core.model.FsCalendarSolar;
import com.lmy.core.model.FsCalendarUser;
import com.lmy.core.model.FsCalendarUserinfo;

@Service
public class CalendarUserServiceImpl {

	private static final Logger logger = Logger.getLogger(CalendarUserServiceImpl.class);

	@Autowired
	private FsCalendarUserDao fsCalendarUserDao;
	@Autowired
	private FsCalendarUserinfoDao fsCalendarUserinfoDao;
	@Autowired
	private FsCalendarSolarDao fsCalendarSolarDao;
	
	
	
	public JSONObject userLogin(String code) {

		String openId = null;
		String sessionKey = null;
		try {
			JSONObject jRlt = WXAppletServiceImpl.code2Session(code);
			if(jRlt.getIntValue("errcode") != 0) {
				return JsonUtils.commonJsonReturn("9999", jRlt.getString("errmsg"));
			}
			openId = jRlt.getString("openid");
			sessionKey = jRlt.getString("session_key");
		} catch (IOException e) {
			return JsonUtils.commonJsonReturn("0101", "微信身份信息获取异常");
		}

		JSONObject result = getUserByOpenId(openId);
		JsonUtils.setBody(result, "openId", openId);
		JsonUtils.setBody(result, "sessionKey", sessionKey);
		return result;
	}
	
	public JSONObject smsSend(String mobile, Long userinfoId, String type) {
		if(!CommonUtils.checkForMobile(mobile)){
			logger.warn("mobile:"+mobile+", 手机格式错误");
			return JsonUtils.commonJsonReturn("0001","手机格式错误");
		}
		
		FsCalendarUserinfo userinfo = fsCalendarUserinfoDao.findByMobile(mobile);
		
		if (type.equals("register")) {
			if (userinfo != null) {
				logger.warn("mobile:"+mobile+", 已被注册");
				return JsonUtils.commonJsonReturn("1000","该手机号已被注册");
			}
		} else if (type.equals("update")) {
			FsCalendarUserinfo userinfo2 = fsCalendarUserinfoDao.findById(userinfoId);
			if (userinfo != null && !userinfo2.getMobile().equals(mobile)) {
				logger.warn("mobile:"+mobile+", 已被注册");
				return JsonUtils.commonJsonReturn("1000","该手机号已被注册");
			}
		}
		
		
		String redisKey = CacheConstant.FS_USR_REG_MOBILE_SEDN_CODE_RESULT + "_applet" + "_" + mobile;
		JSONArray jarray = (JSONArray)RedisClient.get(redisKey);
		if(CollectionUtils.isEmpty(jarray)){
			jarray = new JSONArray();
		}
		JSONObject last = CollectionUtils.isNotEmpty(jarray)?(JSONObject) jarray.get( jarray.size() -1 ) : new JSONObject();
		//90秒有效
		Date now = new Date();
		Date lastTime = ( last == null ? null : (Date)last.get("time") ) ;
		int allowIntervalSec = 90;
		if(lastTime !=null && 	CommonUtils.calculateDiffSeconds(now, lastTime ) < allowIntervalSec){
			logger.warn("【用户注册手机号码】短信发送请求过于频繁，本次拒接  "+mobile +",上一次发送短信于"+CommonUtils.calculateDiffSeconds(now, lastTime ) +"秒前 <"+allowIntervalSec+"秒" );
			return JsonUtils.commonJsonReturn("0003","验证码获取过于频繁");
		}
		// 30分钟内最多允许获取5次
		if (jarray.size() >= 5) {
			logger.warn("【用户注册手机号码】30分钟内短信发送请求过于频繁，本次拒接  "+mobile );
			return JsonUtils.commonJsonReturn("0004","验证码获取过于频繁");
		}
		
		String  code =CommonUtils.getRandom6Code();
		JSONObject curSms = new JSONObject();
		curSms.put("code", code);
		curSms.put("mobile", mobile);
		curSms.put("time", new Date() );
		jarray.add(curSms);

        JSONArray params = new JSONArray();
        params.add(code);
        params.add("雷门易");
        
		boolean smsSendResult = false;
		smsSendResult = TencentSmsFacadeImpl.sendSms(119228, params, mobile);
		if(smsSendResult){
			//发送短信验证码 
			logger.info("=====短信发送成功，"+jarray.toJSONString()+"=====");
			// 缓存30分钟
			RedisClient.set(redisKey, jarray, 60*30);
		} else {
			logger.warn("mobile:"+mobile+", 验证码发送失败");
			return JsonUtils.commonJsonReturn("0005","验证码发送失败");
		}

		return JsonUtils.commonJsonReturn();	
	}
	
	public JSONObject getUserByOpenId(String openId) {
		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			fsUser = new FsCalendarUser();
			Date now = new Date();
			fsUser.setOpenId(openId).setCreateTime(now);
			Long id = fsCalendarUserDao.insert(fsUser);
			fsUser.setId(id);
		}

		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("user", fsUser);
		result.put("body", body);
		return result;
	}
	
	public JSONObject getUserInfo(String openId, Long infoId, String type) {
		
		if (StringUtils.isEmpty(type) && infoId == null) {
			return JsonUtils.commonJsonReturn("0011", "用户信息不存在");
		}
		
		if (!StringUtils.isEmpty(type) && !type.equals(FsCalendarUserinfo.TYPE_MAIN) && infoId == null) {
			return JsonUtils.commonJsonReturn("0011", "用户信息不存在");
		}
		
		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			return JsonUtils.commonJsonReturn("0010", "用户不存在");
		}
		
		FsCalendarUserinfo fsUserinfo = null;
		
		if (!StringUtils.isEmpty(type) && type.equals(FsCalendarUserinfo.TYPE_MAIN)) {
			fsUserinfo = fsCalendarUserinfoDao.findUserMainInfo(fsUser.getId());
		} else {
			fsUserinfo = fsCalendarUserinfoDao.findById(infoId);
		}
		
		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("user", fsUser);
		body.put("userinfo", fsUserinfo);
		result.put("body", body);
		return result;
		
	}
	
	public JSONObject createUserinfo(String openId, String gender, String birthProvince, 
			String birthCity, String birthArea, Integer birthLongitude, Integer birthLatitude,
			Date birthTime, String mobile, String type, String smsCode) {
		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			return JsonUtils.commonJsonReturn("0010", "用户不存在");
		}
		
		if (!StringUtils.isEmpty(type) && type.equals(FsCalendarUserinfo.TYPE_MAIN)) {
			FsCalendarUserinfo fsUserinfo = fsCalendarUserinfoDao.findUserMainInfo(fsUser.getId());
			if (fsUserinfo != null) {
				return JsonUtils.commonJsonReturn("0011", "用户信息已存在，无需新增");
			}
		}

		String redisKey = CacheConstant.FS_USR_REG_MOBILE_SEDN_CODE_RESULT + "_applet" + "_" + mobile;
		JSONArray jarray = (JSONArray)RedisClient.get(redisKey);
		if(CollectionUtils.isEmpty(jarray)){
			return JsonUtils.commonJsonReturn("0020", "验证码已过期或还未获取");
		}
		JSONObject last = CollectionUtils.isNotEmpty(jarray)?(JSONObject) jarray.get( jarray.size() -1 ) : new JSONObject();
		if (!last.getString("code").equals(smsCode)) {
			return JsonUtils.commonJsonReturn("0021", "验证码错误，请输入正确的验证码");
		}

		Date now = new Date();
		FsCalendarUserinfo fsUserinfo = new FsCalendarUserinfo();
		fsUserinfo.setUserId(fsUser.getId());
		fsUserinfo.setType(type);
		fsUserinfo.setStatus(FsCalendarUserinfo.STATUS_PAID);
		fsUserinfo.setGender(gender);
		fsUserinfo.setBirthProvince(birthProvince);
		fsUserinfo.setBirthCity(birthCity);
		fsUserinfo.setBirthArea(birthArea);
		fsUserinfo.setBirthLongitude(birthLongitude);
		fsUserinfo.setBirthLatitude(birthLatitude);
		fsUserinfo.setBirthTime(birthTime);
		fsUserinfo.setMobile(mobile);
		fsUserinfo.setCreateTime(now);
		fsUserinfo.setCountUpdate(0);
		calcUserHeavenEarth(fsUserinfo);
		
		Long id = fsCalendarUserinfoDao.insert(fsUserinfo);
		fsUserinfo.setId(id);

		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("userinfo", fsUserinfo);
		result.put("body", body);
		return result;
	}
	
	public JSONObject updateUserInfo(String openId, Long infoId, String gender, String birthProvince, 
			String birthCity, String birthArea, Integer birthLongitude, Integer birthLatitude,
			Date birthTime, String mobile, String smsCode) {

		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			return JsonUtils.commonJsonReturn("0010", "用户不存在");
		}

		FsCalendarUserinfo fsUserinfoTmp = fsCalendarUserinfoDao.findById(infoId);
		if (fsUserinfoTmp == null) {
			return JsonUtils.commonJsonReturn("0011", "用户信息不存在");
		}
		
//		if (fsUserinfoTmp.getCountUpdate() >= 1) {
//			return JsonUtils.commonJsonReturn("0012", "用户信息只允许变更一次，目前无法再变更");
//		}

		String redisKey = CacheConstant.FS_USR_REG_MOBILE_SEDN_CODE_RESULT + "_applet" + "_" + mobile;
		JSONArray jarray = (JSONArray)RedisClient.get(redisKey);
		if(CollectionUtils.isEmpty(jarray)){
			return JsonUtils.commonJsonReturn("0020", "验证码已过期或还未获取");
		}
		JSONObject last = CollectionUtils.isNotEmpty(jarray)?(JSONObject) jarray.get( jarray.size() -1 ) : new JSONObject();
		if (!last.getString("code").equals(smsCode)) {
			return JsonUtils.commonJsonReturn("0021", "验证码错误，请输入正确的验证码");
		}

		Date now = new Date();
		FsCalendarUserinfo fsUserinfo = new FsCalendarUserinfo();
		
		if (fsUserinfoTmp.getCountUpdate() >= 1) {
			fsUserinfo.setId(infoId);
			fsUserinfo.setMobile(mobile);
			fsUserinfo.setUpdateTime(now);
		} else {
			fsUserinfo.setId(infoId);
			fsUserinfo.setGender(gender);
			fsUserinfo.setBirthProvince(birthProvince);
			fsUserinfo.setBirthCity(birthCity);
			fsUserinfo.setBirthArea(birthArea);
			fsUserinfo.setBirthLongitude(birthLongitude);
			fsUserinfo.setBirthLatitude(birthLatitude);
			fsUserinfo.setBirthTime(birthTime);
			fsUserinfo.setMobile(mobile);
			fsUserinfo.setUpdateTime(now);
			calcUserHeavenEarth(fsUserinfo);
			if (CalendarAidUtil.needRecalcUser(fsUserinfoTmp, fsUserinfo)) {
				calcUserHeavenEarth(fsUserinfo);
			}
			fsUserinfo.setCountUpdate(fsUserinfoTmp.getCountUpdate()+1);
		}
		
		fsCalendarUserinfoDao.update(fsUserinfo);
		fsUserinfo = fsCalendarUserinfoDao.findById(infoId);

		JSONObject result = JsonUtils.commonJsonReturn();
		JSONObject body = new JSONObject();
		body.put("userinfo", fsUserinfo);
		result.put("body", body);
		return result;
	}

	private void calcUserHeavenEarth(FsCalendarUserinfo fsUser) {
		
		Date birthTime = fsUser.getBirthTime();
		Date birthRealTime = CalendarAidUtil.calcRealTime(birthTime, ((double)fsUser.getBirthLongitude())/100000.0);
		fsUser.setBirthRealTime(birthRealTime);
		
		// get year, month h & e from solar infos
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthRealTime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.add(Calendar.MONTH, -1);
		Date dStart = calendar.getTime();
		
		List<FsCalendarSolar> fsSolarList = fsCalendarSolarDao.getSolarByPeriod(dStart, birthRealTime);
		FsCalendarSolar fsSolar = fsSolarList.get(fsSolarList.size()-1);
		
		fsUser.setYearHeaven(fsSolar.getYearHeaven());
		fsUser.setYearEarth(fsSolar.getYearEarth());
		fsUser.setMonthHeaven(fsSolar.getMonthHeaven());
		fsUser.setMonthEarth(fsSolar.getMonthEarth());
		
		// get day h & e 
		String dayHE = CalendarAidUtil.calcDayHeavenEarth(birthRealTime);
		fsUser.setDayHeaven(dayHE.substring(0, 1));
		fsUser.setDayEarth(dayHE.substring(1, 2));
		
		// get hour h & e
		String hourHE = CalendarAidUtil.calcHourHeavenEarth(hour, dayHE);
		fsUser.setHourHeaven(hourHE.substring(0, 1));
		fsUser.setHourEarth(hourHE.substring(1, 2));
		
		// get tai h & e
		String taiHE = CalendarAidUtil.calcTaiHeavenEarth(fsUser.getMonthHeaven(), fsUser.getMonthEarth());
		fsUser.setTaiHeaven(taiHE.substring(0, 1));
		fsUser.setTaiEarth(taiHE.substring(1, 2));
		
		String lifeEarth = CalendarAidUtil.calcLifeEarth(hour, fsSolar.getName());
		fsUser.setLifeEarth(lifeEarth);
	}
	
}
