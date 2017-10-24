package com.lmy.core.beanstalkd.job;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.QueueHandler;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.dao.FsChatSessionDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.dto.FsChatRecordDto;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.service.impl.OrderEvaluateServiceImpl;
import com.lmy.core.service.impl.OrderRefundServiceImpl;
/**
 * 订单进行 24小时后 设置订单状态为completed 或者自动退款 如果master 超过24小时没有回复
 * @author fidel 
 */
@Service
public class PayAfter24HoursManagerImpl extends QueueHandler{
	private static Logger logger = LoggerFactory.getLogger(PayAfter24HoursManagerImpl.class);
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsChatSessionDao fsChatSessionDao;	
	@Autowired
	private FsUsrDao fsUsrDao;	
	@Autowired
	private FsChatRecordDao fsChatRecordDao;
	@Autowired
	private OrderRefundServiceImpl orderRefundServiceImpl;
	@Autowired
	private OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	private List<String> expectStatusList = Arrays.asList(OrderStatus.pay_succ.getStrValue(),OrderStatus.refund_fail.getStrValue());
	@Override
	public String getQueueName() {
		return QueueNameConstant.masterNoReply24HoursAutoRefund;
	}
	/**
	 * key: orderId;
	 */
	@Override
	public Object handle(JSONObject data) throws Exception {
		try{
			Date now = new Date();
			if(data == null || data.isEmpty() || !data.containsKey("orderId")){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			Long orderId = data.getLong("orderId");
			if(orderId == null){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			FsOrder order = this.fsOrderDao.findById(orderId);	
			if(order == null ){
				logger.warn("查无数据 data:{}", data);
				return null;				
			}
			if(!expectStatusList.contains(order.getStatus() )){
				logger.warn("订单状态错误 data:{},当前状态:{} , 期待状态:{}", data , order.getStatus() , ArrayUtils.toString(expectStatusList));
				return null;
			}
			//判断 是自动的退款(大师没有回复的话) , 还是 订单变更 --> completed 状态 订单完成
			//查询最近一次回复
			FsChatRecordDto chatRecord = fsChatRecordDao.findUsrReceLastReply(order.getChatSessionNo(), order.getBuyUsrId());
			///需要做自动退款
			if(chatRecord == null || chatRecord.getCreateTime().after( order.getEndChatTime() )){
				logger.info("======master 24小时内没回复 系统自动的发起退款======being 退款orderId:{};chatSessionNo:{},当前状态:{}",orderId,order.getChatSessionNo(),order.getStatus());
				doAfter24HoursUnreadMsgAutoRefund(order,data);
			} else {
				logger.info("======将订单转为完成状态===== "+data.toJSONString());
				doSetOrderStatusToCompleted(order, now, data);
			}
		}catch(Exception e){
			logger.error("处理出错 data:{}", data,e);
			pushInQueueAgain(data);
		}
		return null;
	}
	
	private void doSetOrderStatusToCompleted(FsOrder order , Date now , JSONObject data){
		//see OrderChatServiceImpl #asynHandIfMasterFirstReply # _handIfMasterFirstReply L177
		//老师有回复 则服务截止时间为 老师第一次回复 24小时后  bugfix at 2017/05/30 22:39
		logger.info("=====now is "+CommonUtils.dateToString(now, CommonUtils.dateFormat2, "")+"=====");
		if( order.getSellerFirstReplyTime()!=null && now.before( order.getEndChatTime() )){
			logger.info("=====当前时间不满足完成状态: first reply: {}, orderId:{};chatSessionNo:{},end_chat_time:{}=====",  
					CommonUtils.dateToString(order.getSellerFirstReplyTime(), CommonUtils.dateFormat2, ""),  
					order.getId(), order.getChatSessionNo(),
					CommonUtils.dateToString(order.getEndChatTime(), CommonUtils.dateFormat2, ""));
			return;
		}
		if(!expectStatusList.contains( order.getStatus())){
			logger.info("=====当前状态不满足完成状态: orderId:{};chatSessionNo:{},当前状态:{},期待状态:{}=====",  
					CommonUtils.dateToString(order.getSellerFirstReplyTime(), CommonUtils.dateFormat2, ""), 
					order.getId(), order.getChatSessionNo(),
					order.getStatus(), ArrayUtils.toString(expectStatusList));
			return ;
		}
		FsOrder orderForUpate = new FsOrder();
		orderForUpate.setId( order.getId() );
		orderForUpate.setStatus(OrderStatus.completed.getStrValue());
		orderForUpate.setCompletedTime(now).setUpdateTime(now);
		this.fsOrderDao.update(orderForUpate);
	}
	
	/** 可以发起多次退款申请 但是只能成功一笔 --->退款交易表中 至多同时只存在 一条 ing|succ 的退款记录**/
	private void doAfter24HoursUnreadMsgAutoRefund(FsOrder order ,JSONObject data){
		try{
			fsOrderDao.updateForRefundApplied(order.getId(), "Y", "老师未接单", order.getPayRmbAmt(), new Date());
			order.setStatus( OrderStatus.refund_applied.getStrValue() );
			JSONObject  refundResult = orderRefundServiceImpl.refundAudit(order, true,true,"老师未接单", "系统自动退款");
			if(JsonUtils.codeEqual(refundResult, "9999")){
				pushInQueueAgain(data);
				return ;
			}
			if(!JsonUtils.equalDefSuccCode(refundResult)){
				logger.warn(refundResult.toJSONString());
				return ;
			}
		}catch(Exception e){
			logger.error("", e);
			pushInQueueAgain(data);
		}
	}
	
	
	private void pushInQueueAgain(JSONObject data){
		Integer _errorTimes = data.getInteger("_errorTimes");
		if(_errorTimes == null ){
			_errorTimes = 0;
		}
		_errorTimes = _errorTimes + 1;
		if(_errorTimes>3){
			logger.error("订单进行 24小时后 3次确认未成功 本次放弃 等待 人工处理 data:{}", data);
			return ;
		}
		data.put("_errorTimes", _errorTimes);
		logger.info("订单进行 24小时后 碰到问题 3 秒后再一次确认 data:{}", data);
		this.put(null, 3, null, data);
	}
}
