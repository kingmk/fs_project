package com.lmy.web.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
public class IndexController {
	private static final Logger logger = Logger.getLogger(IndexController.class);
	@RequestMapping("/")
	@ResponseBody
	public String temp_index(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response ){
		if(logger.isDebugEnabled())	logger.debug("/ temp_index");
		return "hello";
	}
}
