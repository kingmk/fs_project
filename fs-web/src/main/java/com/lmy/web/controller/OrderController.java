package com.lmy.web.controller;
import java.io.UnsupportedEncodingException;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.manage.impl.OrderManageImpl;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsMasterServiceCate;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.CouponServiceImpl;
import com.lmy.core.service.impl.FsZxCateQueryServiceImpl;
import com.lmy.core.service.impl.MasterQueryServiceImpl;
import com.lmy.core.service.impl.OrderAidUtil;
import com.lmy.core.service.impl.OrderQueryServiceImpl;
import com.lmy.core.service.impl.UsrAidUtil;
import com.lmy.web.common.SessionUtil;
import com.lmy.web.common.WebUtil;

@Controller
public class OrderController {
	private static  final Logger logger = Logger.getLogger(OrderController.class);
	@Autowired
	private FsZxCateQueryServiceImpl fsZxCateQueryServiceImpl;
	@Autowired
	private MasterQueryServiceImpl masterQueryServiceImpl;
	@Autowired
	private OrderQueryServiceImpl orderQueryServiceImpl;
	@Autowired
	private OrderManageImpl orderManageImpl;
	@Autowired
	private CouponServiceImpl couponServiceImpl;
	@RequestMapping(value="/order/confirm_nav")
	public String confirm_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "masterServiceCateId" , required = true) long masterServiceCateId
									,@RequestParam(value = "masterInfoId" , required = true) long masterInfoId 
			) throws UnsupportedEncodingException{
		//判断用户是否已经注册过主机号码
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		if(usr == null){
			return WebUtil.failedResponse(response, "");
		}
		if(StringUtils.isEmpty(	usr.getRegisterMobile() )){
			logger.info("usr is not null usr.id:"+usr.getId()+ ", 	usr.getRegisterMobile():"+ usr.getRegisterMobile());
			JSONObject reqParams = WebUtil.getRequestParams(request);
			String backUrl = WebUtil.getBackUrl(request.getRequestURI() , reqParams);
			return "forward:/usr/register/mobile_nav?backUrl="+backUrl;
		}
		FsMasterInfo masterInfo = 	masterQueryServiceImpl.findByMasterInfoId(masterInfoId);
		FsMasterServiceCate masterServiceCate = fsZxCateQueryServiceImpl.findByMasterInfoIdAndId(masterInfoId, masterServiceCateId);
		Long orderAmt = masterServiceCate.getAmt();
		JSONObject couponsForOrder = couponServiceImpl.getCouponsForOrder(usr.getId(), masterServiceCate.getAmt(), masterServiceCate.getFsZxCateId());
		JSONObject couponSel = null;
		JSONArray jCoupons = (JSONArray) JsonUtils.getBodyValue(couponsForOrder, "list");
		if (jCoupons.size() > 0) {
		couponSel = (JSONObject) jCoupons.get(0);
			if (couponSel.getString("useForOrder").equals("N")) {
				couponSel = null;
			}
		}
		int countUsable = (int) JsonUtils.getBodyValue(couponsForOrder, "countUsable");
		
		modelMap.put("masterInfo", masterInfo);
		modelMap.put("masterNickName", UsrAidUtil.getMasterNickName(masterInfo, null, ""));
		modelMap.put("masterServiceCate", masterServiceCate);
		modelMap.put("couponSel", couponSel);
		modelMap.put("countUsable", countUsable);
		modelMap.put("orderAmt", orderAmt);
		modelMap.put("coupons", jCoupons);
		modelMap.put("couponSize", jCoupons.size());
		modelMap.put("strCoupons", JSON.toJSONString(jCoupons,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		return "/order/confirm_nav";
	}
	
	/** 微信统一下单 **/
	@RequestMapping(value="/order/unifiedorder_weixin" , method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String unifiedorder_weixin(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "masterServiceCateId" , required = true) long masterServiceCateId
									,@RequestParam(value = "masterInfoId" , required = true) long masterInfoId
									,@RequestParam(value = "couponId" , required = false) Long couponId
			) {
		FsUsr usr = SessionUtil.getFsUsr(request);
		JSONObject result = orderManageImpl.unifiedorderWeixin(usr.getId(), usr.getWxOpenId(),usr.getRegisterMobile(), WebUtil.getIpAddr(request), masterInfoId, masterServiceCateId, couponId);
		return result.toJSONString();
	}
	
	/**
	 * 统一下单 后页面轮询结果(支付结果) , 由这个页面发起轮询 call --> /order/unifiedorder_polling_ajax_query
	 */
	@RequestMapping(value="/order/unifiedorder_polling_nav" , method={RequestMethod.GET})
	public String unifiedorder_query(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "orderId" , required = true) long orderId 
			) {
		modelMap.put("orderId", orderId);
		return "/order/unifiedorder_polling_nav";
	}
	@RequestMapping(value="/order/unifiedorder_polling_ajax_query" , method={RequestMethod.POST})
	@ResponseBody
	public String unifiedorder_polling_ajax_query(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "orderId" , required = true) long orderId 
			) {
		Long usrId = WebUtil.getUserId(request);
		FsOrder order = this.orderQueryServiceImpl.findByOrderIdAndBuyUsrId(orderId, usrId);
		if(order == null){
			logger.warn("orderId:"+orderId+",usrId:"+usrId+",未查询到订单数据 攻击？响应空白页面");
			return WebUtil.failedResponse(response, "");
		}
		JSONObject result =  JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "status", order.getStatus());
		JsonUtils.setBody(result, "orderId", orderId);
		return result.toJSONString();
	}
	
	/**
	 * 统一下单 支付成功 结果页
	 */
	@RequestMapping(value="/order/order_pay_succ" , method={RequestMethod.GET})
	public String order_pay_succ(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "orderId" , required = true) long orderId 
			) {
		FsUsr usr = SessionUtil.getFsUsr(request);
		FsOrder order = orderQueryServiceImpl.findByOrderIdAndBuyUsrId(orderId, usr.getId());
		if(order ==null || !"pay_succ".equals(order.getStatus()) ) {
			logger.warn("orderId:"+orderId+",usrId:"+usr.getId()+",未查询到订单数据 攻击？响应空白页面");
			return WebUtil.failedResponse(response,"");
		}
		//add by fidel at 2017/05/31 19:29
		if( !OrderAidUtil.getNeedSupplyOrderInfoZxCateIds().contains( order.getZxCateId()  ) ){
			return "forward:/order/chat_index?chatSessionNo="+order.getChatSessionNo()+"&orderId="+order.getId()+"&afterCommUsrSupplyInfo=true";
		}
		modelMap.put("order", order);
		modelMap.put("usr", usr);
		if(usr.getBirthYear()!=null && StringUtils.isNotBlank(usr.getBirthDate())){
			modelMap.put("usrBirthDate",   CommonUtils.dateToString( 
					CommonUtils.stringToDate(usr.getBirthYear()+usr.getBirthDate(), "yyyyMMdd") , "yyyy-MM-dd", "")             );	
		}
		
		return "/order/order_pay_succ";
	}
	
	/**
	 * 统一下单 支付成功 结果页 补重需咨询/预测人的信息
	 */
	@RequestMapping(value="/order/supply_zx_usrinfo" , method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String supply_zx_usrinfo(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
									,@RequestParam(value = "orderId" , required = true) long orderId 
									,@RequestParam(value = "chatSessionNo" , required = true) String chatSessionNo 
									,@RequestParam(value="data" , required = true) String data  
			) {
		FsUsr sessionUsr = 	SessionUtil.getFsUsr(request);
		JSONObject result = orderManageImpl.supplyOrderZXUsrInfo(orderId, chatSessionNo ,sessionUsr.getId(), JSON.parseArray(data));
		if(JsonUtils.equalDefSuccCode(result)){
			FsUsr usrForUpdate = (FsUsr)JsonUtils.getBodyValue(result, "usrForUpdate");
			if( usrForUpdate!=null  ){
				sessionUsr.setBirthAddress( usrForUpdate.getBirthAddress() );
				sessionUsr.setBirthDate(usrForUpdate.getBirthDate());
				sessionUsr.setBirthTime(usrForUpdate.getBirthTime());
				sessionUsr.setBirthTimeType(usrForUpdate.getBirthTimeType());
				sessionUsr.setBirthYear(usrForUpdate.getBirthYear());
				sessionUsr.setRealName( usrForUpdate.getRealName() );
				sessionUsr.setFamilyRank( usrForUpdate.getFamilyRank() );
				sessionUsr.setMarriageStatus(usrForUpdate.getMarriageStatus() );
				sessionUsr.setEnglishName(usrForUpdate.getEnglishName());
				sessionUsr.setSex(usrForUpdate.getSex()  );
				//logger.info("usrId:"+sessionUsr.getId()+", 补充订单信息,重置session "+sessionUsr);
				SessionUtil.mainSession(request, "usr", sessionUsr);
			}
		}
		return result.toJSONString();
	}
	
	
	/**
	 * 
	 */
	@RequestMapping(value="/order/notify_url" )
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@ResponseBody
	public String notify_url(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response) {
		//<xml><appid><![CDATA[wxf3b757c9aa106b04]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[2]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[Y]]></is_subscribe><mch_id><![CDATA[1457841802]]></mch_id><nonce_str><![CDATA[884d33b75d0d40f9a7fd122910d05edc]]></nonce_str><openid><![CDATA[oCpYx1OpdaLOIadVtMMHyGX_bca4]]></openid><out_trade_no><![CDATA[201704161600000110]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[82E5157FCE276FED7A620B27419AC4B9]]></sign><time_end><![CDATA[20170416164607]]></time_end><total_fee>2</total_fee><trade_type><![CDATA[JSAPI]]></trade_type><transaction_id><![CDATA[4000852001201704167230430405]]></transaction_id></xml>
		JSONObject result = this.orderManageImpl.zxOrderHandWeiXinNotify(WebUtil.getReqData(request));
		logger.info("回调处理结果:"+result  );
		return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
}
