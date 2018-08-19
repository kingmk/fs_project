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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.OrderEvaluateServiceImpl;
import com.lmy.core.service.impl.OrderRefundServiceImpl;
import com.lmy.core.service.impl.OrderServiceImpl;
import com.lmy.web.common.WebUtil;
/**
 * @author kingmk
 * @since 2017/04/27
 */
@Controller
public class OrderOperController {
	@Autowired
	private OrderRefundServiceImpl orderRefundServiceImpl;
	@Autowired
	private OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	/** 普通用户前往退款申请页 **/
	@RequestMapping(value="/order/common_refund_nav" )
	public String common_refund_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
								,@RequestParam(value = "orderId" , required = true) long orderId
								){
		JSONObject result = orderRefundServiceImpl.commonUsrRefundNav(WebUtil.getUserId(request), orderId);
		modelMap.put("result", result);
		modelMap.put("resultStr",   JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue)  );
		return "/order/common_refund_nav";
	}
	
	/** 普通用户 提交退款申请 **/
	@RequestMapping(value="/order/common_refund_apply_ajax_submit",method={RequestMethod.POST} )
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String common_refund_apply_ajax_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
								,@RequestParam(value = "orderId" , required = true) long orderId
								,@RequestParam(value = "refundReason" , required = true) String refundReason
								){
		return orderRefundServiceImpl.commonUsrRefundApply(WebUtil.getUserId(request), orderId, refundReason).toJSONString();
	}
	

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
	

	/** 普通用户删除订单 **/
	@RequestMapping(value="/order/buyer_delete_ajax",method={RequestMethod.POST} )
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String buyer_delete_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
								,@RequestParam(value = "orderId" , required = true) long orderId
								){
		return this.orderServiceImpl.deleteOrderByBuyer(WebUtil.getUserId(request), orderId).toJSONString();
	}
}
