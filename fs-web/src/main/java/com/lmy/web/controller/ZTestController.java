package com.lmy.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.service.impl.CalendarServiceImpl;
import com.lmy.core.utils.FsEnvUtil;

@Controller
public class ZTestController {

	private static final Logger logger = Logger.getLogger(ZTestController.class);
	@Autowired
	private CalendarServiceImpl calendarServiceImpl;

	@RequestMapping(value="/test/create_calendar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String test_create_calendar(HttpServletRequest request,HttpServletResponse response
			) throws Exception{
		if (!FsEnvUtil.isDev()) {
			return "only for test environment";
		}
		

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date startDate = dateFormat.parse("2018-01-01 00:00:00");
		Date endDate = dateFormat.parse("2020-12-31 23:59:59");
		calendarServiceImpl.randomCreateCalendar(startDate, endDate);
		
		return "ok";
	}
	
	@RequestMapping(value="/test/get_month_calendar")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String get_month_calendar(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "y" , required = true) int year
			,@RequestParam(value = "m" , required = true) int month
			) throws Exception{
		if (!FsEnvUtil.isDev()) {
			return "only for test environment";
		}
		
		JSONObject result = null; 
//				calendarServiceImpl.getCommonMonthCalendars(year, month);
		
		return result.toJSONString();
	}
	
//	@RequestMapping(value="/test/calc_user_he")
//	@ResponseBody
//	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
//	public String calc_user_he(HttpServletRequest request,HttpServletResponse response
//			,@RequestParam(value = "date" , required = true) String datestr
//			) throws Exception{
//		if (!FsEnvUtil.isDev()) {
//			return "only for test environment";
//		}
//		
//		FsCalendarUser fsUser = new FsCalendarUser();
//		Date date = CalendarAidUtil.dateFormatFull.parse(datestr);
//		fsUser.setBirthTime(date);
//		
//		JSONObject result = calendarServiceImpl.calUserHeavenEarth(fsUser);
//		
//		return result.toJSONString();
//	}
}
