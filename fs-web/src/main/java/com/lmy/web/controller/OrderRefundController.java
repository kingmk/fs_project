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
import com.lmy.core.service.impl.OrderRefundServiceImpl;
import com.lmy.web.common.WebUtil;
/**
 * @author fidel
 * @since 2017/04/27
 */
@Controller
public class OrderRefundController {
	@Autowired
	private OrderRefundServiceImpl orderRefundServiceImpl;
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
}
