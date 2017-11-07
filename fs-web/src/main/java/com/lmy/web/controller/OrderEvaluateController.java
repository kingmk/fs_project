package com.lmy.web.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.service.impl.OrderEvaluateServiceImpl;
import com.lmy.web.common.WebUtil;
/**
 * @author fidel
 * @since 2017/04/27
 */
@Controller
public class OrderEvaluateController {
	@Autowired
	private OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	/** 普通用户前往点评页|查看点评 **/
	@RequestMapping(value="/order/evaluate/common_view" )
	public String common_evaluate_view(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
								,@RequestParam(value = "orderId" , required = true) long orderId
								){
		JSONObject result = this.orderEvaluateServiceImpl.commonUsrView(WebUtil.getUserId(request), orderId);
		modelMap.put("result", result);
		return "/order/evaluate/common_view";
	}
	
	/** 普通用户提交点评 **/
	@RequestMapping(value="/order/evaluate/common_ajax_submit",method={RequestMethod.POST} )
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String common_evaluate_ajax_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
								,@RequestParam(value = "orderId" , required = true) long orderId
								,@RequestParam(value = "respSpeed" , required = false) Long respSpeed   //响应速度
								,@RequestParam(value = "majorLevel" , required = false) Long majorLevel  //专业水平
								,@RequestParam(value = "serviceAttitude" , required = false) Long serviceAttitude  //服务态度
								,@RequestParam(value = "evaluateWord" , required = false) String evaluateWord // 评论语
								,@RequestParam(value = "isAnonymous" , required = false) Integer isAnonymous // 评论语
								){
		return  this.orderEvaluateServiceImpl.buyUsrCommentOrder(WebUtil.getUserId(request), orderId, 
								respSpeed !=null ? respSpeed :0l, 
								majorLevel !=null ? majorLevel :0l, 
								serviceAttitude !=null ? serviceAttitude : 0l, 
								evaluateWord, isAnonymous != null ?isAnonymous: 0).toJSONString();
	}
}
