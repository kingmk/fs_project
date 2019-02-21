package com.lmy.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ErrorController {

	private static final Logger logger = Logger.getLogger(ErrorController.class);
	

	@RequestMapping(value="/common/error")
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String error(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "error_msg" , required = false) String error_msg
			) throws Exception{
		if (error_msg!=null && error_msg.length()>0) {
			modelMap.put("error_msg", error_msg);
		}
		return "/common/error";
	}
}
