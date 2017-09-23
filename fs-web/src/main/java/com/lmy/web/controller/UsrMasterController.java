package com.lmy.web.controller;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import com.lmy.common.component.JsonUtils;
import com.lmy.core.model.FsMasterCard;
import com.lmy.core.service.impl.MasterQueryServiceImpl;
import com.lmy.core.service.impl.OrderEvaluateServiceImpl;
import com.lmy.core.service.impl.OrderQueryServiceImpl;
import com.lmy.web.common.WebUtil;

@Controller
public class UsrMasterController {
	private static final Logger logger = Logger.getLogger(UsrMasterController.class);
	@Autowired
	private MasterQueryServiceImpl masterQueryServiceImpl;
	@Autowired
	private OrderQueryServiceImpl orderQueryServiceImpl;
	@Autowired
	private OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	/**
	 * 大师端 我的账号
	 */
	@RequestMapping(value="/usr/master/account")
	public String usr_master_account(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = 	masterQueryServiceImpl.masterAct(loginUsrId);
		if(JsonUtils.codeEqual(result, "0001") ){
			return "/usr/common/my";
		}
		if( JsonUtils.codeEqual(result, "9999")){
			return WebUtil.failedResponse(response, "");
		}
		String auditStatus =(String) JsonUtils.getBodyValue(result, "auditStatus");
		 if("ing".equals(auditStatus) || "refuse".equals(auditStatus)){
			 return "/usr/master/recruit/apply_nav";
		}
		modelMap.put("result", result);
		return "/usr/master/account";
	}
	
	/**
	 * 大师端 我的收入
	 */
	@RequestMapping(value="/usr/master/my_income")
	public String my_income(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = orderQueryServiceImpl.statMasterIncome(loginUsrId);
		if(!JsonUtils.equalDefSuccCode(result)){
			return WebUtil.failedResponse(response, "");
		}
		modelMap.put("result", result);
		return "/usr/master/my_income";
	}
	/**
	 * 大师端 我的收入明细 
	 */
	@RequestMapping(value="/usr/master/my_income_detail_query_ajax" , method = {RequestMethod.POST})
	@ResponseBody
	public String my_income_detail_query_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //当前页 从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum   //每页显示条数
			,@RequestParam(value = "orderBy" , required = false) String orderBy){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = orderQueryServiceImpl.masterIncomeDetail(loginUsrId, currentPage, perPageNum, orderBy);
		return result.toJSONString();
	}
	
	/**
	 * 
	 */
	@RequestMapping(value="/usr/master/my_bill_list_query_ajax" , method = {RequestMethod.POST})
	@ResponseBody
	public String my_bill_list_query_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //当前页 从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum   //每页显示条数
			){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = orderQueryServiceImpl.findMasterUsrBillList(loginUsrId, currentPage, perPageNum);
		return result.toJSONString();
	}

	/**
	 * 
	 */
	@RequestMapping(value="/usr/master/my_bill_detail_nav")
	public String my_bill_detail_query_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "orderSettlementId" , required = false) Long orderSettlementId   
			){
		modelMap.put("orderSettlementId", orderSettlementId);
		return "/usr/master/my_bill_detail_nav";
	}
	
	/**
	 * 
	 */
	@RequestMapping(value="/usr/master/my_bill_detail_query_ajax" , method = {RequestMethod.POST})
	@ResponseBody
	public String my_bill_detail_query_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //当前页 从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum   //每页显示条数
			,@RequestParam(value = "orderSettlementId" , required = false) Long orderSettlementId   
			){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = orderQueryServiceImpl.findMasterUsrBillDetailList(loginUsrId, orderSettlementId, currentPage, perPageNum);
		return result.toJSONString();
	}
	
	/**
	 * 大师端 我的订单
	 */
	@RequestMapping(value="/usr/master/order_list_nav")
	public String order_list(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		return "/usr/master/order_list_nav";
	}
	/**
	 * 大师端 我的订单
	 */
	@RequestMapping(value="/usr/master/order_list_ajax_query",method={RequestMethod.POST})
	@ResponseBody
	public String order_list_ajax_query(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum //每页显示条数 默认 20
			){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = orderQueryServiceImpl.findMasterUsrOrderList(loginUsrId, currentPage, perPageNum, 0, null);
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	/**
	 * 大师端 查看评分 非单笔交易
	 */
	@RequestMapping(value="/usr/master/evaluate_summary")
	public String evaluate_summary(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "masterUsrId" , required = false) Long masterUsrId   //从 0 开始
			){
		Long loginUsrId = WebUtil.getUserId(request);
		if(masterUsrId == null ){
			masterUsrId = loginUsrId;
		}
		modelMap.put("masterUsrId", masterUsrId);
		JSONObject result =orderEvaluateServiceImpl.masterEvaluateSummary(masterUsrId);
		modelMap.put("result", result);
		return "/usr/master/evaluate_summary";
	}
	@RequestMapping(value="/usr/master/evaluate_list",method={RequestMethod.POST})
	@ResponseBody
	public String evaluate_list(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //当前页 从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum   //每页显示条数
			,@RequestParam(value = "masterUsrId" , required = false) Long masterUsrId   //每页显示条数
			
			){
		Long loginUsrId = WebUtil.getUserId(request);
		if(masterUsrId == null ){
			masterUsrId = loginUsrId;
		}
		return orderEvaluateServiceImpl.masterEvaluateList(masterUsrId, currentPage, perPageNum).toJSONString();
	}
	/** master 用户前往点评页|查看点评 **/
	@RequestMapping(value="/usr/master/evaluate_single_detail")
	public String evaluate_single_detail(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "orderId" , required = true) long orderId
			){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result =orderEvaluateServiceImpl.masterViewSingleEvaluateDetail(loginUsrId, orderId);
		if(!JsonUtils.equalDefSuccCode(result)){
			logger.warn("result"+result+", 响应空白页面");
			WebUtil.failedResponse(response, "");
		}
		modelMap.put("result", result);
		modelMap.put("resultStr", result.toJSONString());
		return "/usr/master/evaluate_single_detail";
	}
	/**
	 * 大师端  大师个人主页
	 */
	@RequestMapping(value="/usr/master/personal_home_page")
	public String personal_home_page(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "masterInfoId" , required = true) long masterInfoId){
		JSONObject result = this.masterQueryServiceImpl.masterPersonalHomePage(WebUtil.getUserId(request));
		modelMap.put("result", result);
		return "/usr/master/personal_home_page";
	}
	
	/**
	 * Master get bank card info
	 */
	@RequestMapping(value="/usr/master/my_bankcard")
	public String save_bankcard(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = masterQueryServiceImpl.masterCard(loginUsrId);
		modelMap.put("result", result);
		return "/usr/master/my_bankcard";
	}
	
	/**
	 * Master save bank card info
	 */
	@RequestMapping(value="/usr/master/save_bankcard",method={RequestMethod.POST})
	@ResponseBody
	public String my_bankcard(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "holderName" , required = true) String holderName,
			@RequestParam(value = "bankName" , required = true) String bankName,
			@RequestParam(value = "bankNo" , required = true) String bankNo,
			@RequestParam(value = "province" , required = true) String province,
			@RequestParam(value = "city" , required = true) String city){
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = null;
		FsMasterCard masterCard = new FsMasterCard();
		masterCard.setMasterUsrId(loginUsrId);
		masterCard.setHolderName(holderName);
		masterCard.setBankName(bankName);
		masterCard.setBankNo(bankNo);
		masterCard.setProvince(province);
		masterCard.setCity(city);
		result = masterQueryServiceImpl.saveMasterCard(masterCard);
		modelMap.put("result", result);
		return result.toJSONString();
	}
}
