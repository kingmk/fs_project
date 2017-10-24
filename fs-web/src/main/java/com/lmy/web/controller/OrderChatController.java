package com.lmy.web.controller;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.OrderChatQueryServiceImpl;
import com.lmy.core.service.impl.OrderChatServiceImpl;
import com.lmy.core.service.impl.UsrAidUtil;
import com.lmy.web.common.SessionUtil;
import com.lmy.web.common.WebUtil;
@Controller
public class OrderChatController {
	private static final Logger logger = Logger.getLogger(OrderChatController.class);
	@Autowired
	private OrderChatQueryServiceImpl orderChatQueryServiceImpl;
	@Autowired
	private OrderChatServiceImpl orderChatServiceImpl;
	//TODO 强验证 2017/05/22
	/**订单详情页 **/
	@RequestMapping(value="/order/chat_index" , method={RequestMethod.GET})
	public String chat_index(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "chatSessionNo" , required = true) String chatSessionNo
									,@RequestParam(value = "orderId" , required = true) long orderId
									,@RequestParam(value = "afterCommUsrSupplyInfo" , required = false) Boolean afterCommUsrSupplyInfo
			) {
		FsUsr usr = SessionUtil.getFsUsr(request);
		JSONObject result =   orderChatQueryServiceImpl.queryForChatIndex(orderId, usr.getId() , chatSessionNo,afterCommUsrSupplyInfo);
		if( ! JsonUtils.equalDefSuccCode(result)){
			//咨询人待补充数据
			if( JsonUtils.codeEqual(result, "1000") ){
				return "redirect:/order/order_pay_succ?orderId="+orderId;
			}
			else if( JsonUtils.codeEqual(result, "0010") ){
				logger.warn("查询聊天记录错误result"+result);
				return WebUtil.failedResponse(response,"");		
			}
			else{
				logger.warn("查询聊天记录错误");
				return WebUtil.failedResponse(response,"");				
			}
		}
		modelMap.put("body", result.get("body"));
		modelMap.put("loginUsr", usr);
		modelMap.put("chatSessionNo", chatSessionNo);
		modelMap.put("orderId", orderId);
		boolean currUsrIsMaster = (Boolean)JsonUtils.getBodyValue(result, "currUsrIsMaster");
		if(!currUsrIsMaster){
			modelMap.put("usrImgUrl",  UsrAidUtil.getUsrHeadImgUrl2(usr, "") );
			//logger.info("currUsrIsMaster:"+currUsrIsMaster+",请问buyUsr page");
			return "/order/chat_common_index";	
		}else{
			modelMap.put("usrImgUrl",  UsrAidUtil.getUsrHeadImgUrl(usr, "") );
			//logger.info("currUsrIsMaster:"+currUsrIsMaster+",请问sellerUsr page");
			//前往老师端聊天
			return "/order/chat_master_index";
		}
	}
	
	/** 聊天记录异步加载  **/
	@RequestMapping(value="/order/chat_query_html_fragment_ajax" , method={RequestMethod.POST})
	public String chat_query_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "chatSessionNo" , required = true) String chatSessionNo
									,@RequestParam(value = "orderId" , required = true) long orderId
									,@RequestParam(value = "gtChatId" , required = false) Long gtChatId              				//需要大于的 id
									,@RequestParam(value = "ltChatId" , required = false) Long ltChatId              					 //需要小于的id 
									//,@RequestParam(value = "currentPage" , required = false) Integer currentPage   //当 提供了参数 gtId 则该参数无需提供;否则为必填项 从 0 开始
									//,@RequestParam(value = "perPageNum" , required = false) Integer perPageNum //每页显示条数 默认 20
									,@RequestParam(value = "excludeChatIds" , required = false) List<Long>  excludeChatIds
			) {
		Long loginUsrId = WebUtil.getUserId(request);
		if(logger.isDebugEnabled())	logger.debug("excludeChatIds:"+excludeChatIds);
		if(logger.isDebugEnabled())	logger.debug("req:"+WebUtil.getRequestParams(request));
		JSONObject result  =  orderChatQueryServiceImpl.queryAjax(gtChatId,  ltChatId, excludeChatIds, loginUsrId , orderId, chatSessionNo, 0, 50);
		modelMap.put("body", result.getJSONObject("body"));
		modelMap.put("loginUsrId", loginUsrId);
		return "/order/chat_query_html_fragment_ajax";
	}
	
	/** 提交聊天记录异步加载  **/
	@RequestMapping(value="/order/chat_submit" , method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String chat_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "clientUniqueNo" , required = true) String clientUniqueNo    //消息类型为 img 为必须
									,@RequestParam(value = "chatSessionNo" , required = true) String chatSessionNo
									,@RequestParam(value = "orderId" , required = true) long orderId
									,@RequestParam(value = "msgType" , required = true) String msgType     //消息类型 text 普通文本(含普通表情文);img 图片; imgtext 图文; richtext 富文本 当前仅支持text,img
									,@RequestParam(value = "isEscape" , required = false ) String isEscape     //是否开启转义 Y;N 默认值 default Y
									,@RequestParam(value = "content" , required = false) String content          //消息类型为 text ,imgtext,richtext 时为必须
									,@RequestParam(value = "img" , required = false) CommonsMultipartFile img   //消息类型为 img 为必须
									
									
			) {
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = orderChatServiceImpl.handMsg(clientUniqueNo,chatSessionNo, orderId, msgType, isEscape, content, img, loginUsrId);
		return result.toJSONString();
	}
	
	
	/**
	 * 查询未读数
	 * @param chatSessionNo  chatSessionNo  if null 统计所有未读数;否则 统计某一次回话未读数
	 * @return
	 */
	@RequestMapping(value="/order/chat_unread_num_query" , method={RequestMethod.POST})
	@ResponseBody
	public String chat_unread_num(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "chatSessionNo" , required = false) String chatSessionNo
									,@RequestParam(value = "isMaster" , required = false) String isMaster                   //当前用户是否为master 角色登录 Y|N, default is N
			) {
		Long loginUsrId = WebUtil.getUserId(request);
		if(!StringUtils.equals(isMaster, "Y")){
			isMaster = "N";
		}
		return this.orderChatQueryServiceImpl.queryUnReadNum(chatSessionNo, isMaster,loginUsrId ).toJSONString();
	}

	/** 提前结束订单  **/
	@RequestMapping(value="/order/pre_end_chat" , method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String pre_end_chat(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "chatSessionNo" , required = true) String chatSessionNo
									,@RequestParam(value = "orderId" , required = true) long orderId
									
									
			) {
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = this.orderChatServiceImpl.endOrderByBuyer(orderId, chatSessionNo, loginUsrId);
		return result.toJSONString();
	}

	/** 老师延长订单  **/
	@RequestMapping(value="/order/extend_chat" , method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String extend_chat(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "chatSessionNo" , required = true) String chatSessionNo
									,@RequestParam(value = "orderId" , required = true) long orderId
									,@RequestParam(value = "hours" , required = true) int hours
									
									
			) {
		Long loginUsrId = WebUtil.getUserId(request);
		JSONObject result = this.orderChatServiceImpl.extendOrderByMaster(orderId, chatSessionNo, loginUsrId, hours);
		return result.toJSONString();
	}
	
}
