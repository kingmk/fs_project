package com.lmy.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.CalendarServiceImpl;

@Controller
public class CalendarController {

	private static final Logger logger = Logger.getLogger(CalendarController.class);
	@Autowired
	private CalendarServiceImpl calendarServiceImpl;
	

	@RequestMapping(value="/calendar/get_month_calendar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String get_month_calendar(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "y" , required = true) Integer year
			,@RequestParam(value = "m" , required = true) Integer month
			,@RequestParam(value = "openId" , required = true) String openId
			,@RequestParam(value = "infoId" , required = true) Long infoId
			) throws Exception{
		
		JSONObject result = calendarServiceImpl.getCustomMonthCalendars(openId, infoId, year, month);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	@RequestMapping(value="/calendar/get_year_calendar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String get_year_calendar(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "y" , required = true) int year
			) throws Exception{
		
		JSONObject result = calendarServiceImpl.getCommonYearCalendar(year);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/get_solar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String get_solar(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "y" , required = true) int year
			) throws Exception{
		
		JSONObject result = calendarServiceImpl.getSolarByYear(year);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/save_calendar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String save_calendar(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "data" , required = true) String data
			) throws Exception{
		
		JSONObject result = calendarServiceImpl.saveCalendar(data);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}

	@RequestMapping(value="/calendar/save_solar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String save_solar(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "data" , required = true) String data
			) throws Exception{
		
		JSONObject result = calendarServiceImpl.saveSolar(data);
		
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
