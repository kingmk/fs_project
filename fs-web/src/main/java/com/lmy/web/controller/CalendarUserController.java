package com.lmy.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.CalendarAidUtil;
import com.lmy.core.service.impl.CalendarOrderServiceImpl;
import com.lmy.core.service.impl.CalendarUserServiceImpl;
import com.lmy.web.common.WebUtil;

@Controller
public class CalendarUserController {

	private static final Logger logger = Logger.getLogger(CalendarUserController.class);
	@Autowired
	private CalendarUserServiceImpl calendarUserServiceImpl;
	@Autowired
	private CalendarOrderServiceImpl calendarOrderServiceImpl;

	@RequestMapping(value="/calendar/user/login")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String user_login(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "code" , required = true) String code
			) throws Exception{
		
		JSONObject result = calendarUserServiceImpl.userLogin(code);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/user/sms_send")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String sms_send(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "mobile" , required = true) String mobile
			,@RequestParam(value = "userinfoId" , required = false) Long userinfoId
			,@RequestParam(value = "type" , required = false) String type
			) throws Exception{
		
		if (StringUtils.isEmpty(type)) {
			type = "register";
		}
		
		JSONObject result = calendarUserServiceImpl.smsSend(mobile, userinfoId, type);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/user/get_by_openId")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String get_by_openId(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "openId" , required = true) String openId
			) throws Exception{
		
		JSONObject result = calendarUserServiceImpl.getUserByOpenId(openId);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/user/get_userinfo")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String get_userinfo(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "openId" , required = true) String openId
			,@RequestParam(value = "infoId" , required = false) Long infoId
			,@RequestParam(value = "type" , required = false) String type
			) throws Exception{
		JSONObject result = calendarUserServiceImpl.getUserInfo(openId, infoId, type);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	@RequestMapping(value="/calendar/user/create_user_info")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String create_user_info(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "openId" , required = true) String openId
			,@RequestParam(value = "type" , required = false) String type
			,@RequestParam(value = "gender" , required = false) String gender
			,@RequestParam(value = "birthProvince" , required = false) String birthProvince
			,@RequestParam(value = "birthCity" , required = false) String birthCity
			,@RequestParam(value = "birthArea" , required = false) String birthArea
			,@RequestParam(value = "birthLongitude" , required = false) Integer birthLongitude
			,@RequestParam(value = "birthLatitude" , required = false) Integer birthLatitude
			,@RequestParam(value = "birthTime" , required = false) String birthTime
			,@RequestParam(value = "mobile" , required = false) String mobile
			,@RequestParam(value = "smsCode" , required = false) String smsCode
			) throws Exception{
		
		JSONObject result = calendarUserServiceImpl.createUserinfo(openId, gender, birthProvince, birthCity, 
				birthArea, birthLongitude, birthLatitude, CalendarAidUtil.dateFormatFull.parse(birthTime), mobile, 
				type, smsCode);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/user/update_user_info")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String update_user_info(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "openId" , required = true) String openId
			,@RequestParam(value = "infoId" , required = true) Long infoId
			,@RequestParam(value = "gender" , required = false) String gender
			,@RequestParam(value = "birthProvince" , required = false) String birthProvince
			,@RequestParam(value = "birthCity" , required = false) String birthCity
			,@RequestParam(value = "birthArea" , required = false) String birthArea
			,@RequestParam(value = "birthLongitude" , required = false) Integer birthLongitude
			,@RequestParam(value = "birthLatitude" , required = false) Integer birthLatitude
			,@RequestParam(value = "birthTime" , required = false) String birthTime
			,@RequestParam(value = "mobile" , required = false) String mobile
			,@RequestParam(value = "smsCode" , required = false) String smsCode
			) throws Exception{
		
		JSONObject result = calendarUserServiceImpl.updateUserInfo(openId, infoId, gender, birthProvince, birthCity, 
				birthArea, birthLongitude, birthLatitude, CalendarAidUtil.dateFormatFull.parse(birthTime), mobile,
				smsCode);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	

	/** 微信统一下单 **/
	@RequestMapping(value="/calendar/order/unifiedorder_weixin")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String unifiedorder_weixin(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "openId" , required = true) String openId
									,@RequestParam(value = "infoId" , required = true) Long infoId
			) {
		JSONObject result = calendarOrderServiceImpl.unifiedOrder(openId, infoId, WebUtil.getIpAddr(request));
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	/** 支付完成后变更业务订单状态及用户信息状态 **/
	@RequestMapping(value="/calendar/order/succeed")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String succeed(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "openId" , required = true) String openId
									,@RequestParam(value = "orderId" , required = true) Long orderId
			) {
		JSONObject result = calendarOrderServiceImpl.orderSucceed(openId, orderId);
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	

	/** 微信下单回调 **/
	@RequestMapping(value="/calendar/order/notify_url" )
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String notify_url(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response) {
		//<xml><appid><![CDATA[wxf3b757c9aa106b04]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[2]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[1457841802]]></mch_id><nonce_str><![CDATA[884d33b75d0d40f9a7fd122910d05edc]]></nonce_str><openid><![CDATA[oCpYx1OpdaLOIadVtMMHyGX_bca4]]></openid><out_trade_no><![CDATA[201704161600000110]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[82E5157FCE276FED7A620B27419AC4B9]]></sign><time_end><![CDATA[20170416164607]]></time_end><total_fee>2</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4000852001201704167230430405]]></transaction_id></xml>
		JSONObject result = calendarOrderServiceImpl.handleWxNotify(WebUtil.getReqData(request));
		logger.info("小程序支付回调处理结果:"+result  );
		return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
}
