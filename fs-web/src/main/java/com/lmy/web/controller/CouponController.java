package com.lmy.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.model.FsZxCate;
import com.lmy.core.model.dto.LoginCookieDto;
import com.lmy.core.service.impl.CouponServiceImpl;
import com.lmy.web.common.WebUtil;

@Controller
public class CouponController {
	@Autowired
	private CouponServiceImpl couponServiceImpl;

	@RequestMapping(value="/coupon/template_detail")
	public String template_detail(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "templateId" , required = true) Long templateId
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		JSONObject result = couponServiceImpl.getCouponTmplDetail(loginDto.getUserId(), templateId);
		modelMap.put("result", result);
		modelMap.put("resultStr", JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		return "/usr/coupon/coupon_template_detail";
	}

	@RequestMapping(value="/coupon/fetch_ajax")
	@ResponseBody
	public String fetch_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "templateId" , required = true) Long templateId
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		JSONObject result = couponServiceImpl.fetchCoupon(loginDto.getUserId(), templateId);
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/coupon/my_coupons")
	public String my_coupons(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		JSONObject result = couponServiceImpl.userCouponsNav(loginDto.getUserId());
		modelMap.put("result", result);
		return "/usr/coupon/my_coupons";
	}

	@RequestMapping(value="/coupon/my_coupons_ajax")
	@ResponseBody
	public String my_coupons_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "usable" , required = false) String usable
			,@RequestParam(value = "page" , required = false) Integer page
			,@RequestParam(value = "pagesize" , required = false) Integer pagesize
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		if (usable == null) {
			usable = "Y";
		}
		if (page == null) {
			page = 0;
		}
		if (pagesize == null) {
			pagesize = 10;
		}
		JSONObject result = couponServiceImpl.getUserCoupons(loginDto.getUserId(), usable, page, pagesize);

		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/coupon/coupon_detail")
	public String coupon_detail(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "couponId" , required = true) Long couponId
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		JSONObject result = couponServiceImpl.getUserCouponDetail(loginDto.getUserId(), couponId);
		modelMap.put("result", result);
		modelMap.put("resultStr", JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		return "/usr/coupon/coupon_detail";
	}

	@RequestMapping(value="/coupon/order_coupons_ajax")
	@ResponseBody
	public String order_coupons_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "orderPayAmt" , required = true) Long orderPayAmt
			,@RequestParam(value = "cateId" , required = true) Long cateId
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		JSONObject result = couponServiceImpl.getCouponsForOrder(loginDto.getUserId(), orderPayAmt, cateId);

		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
