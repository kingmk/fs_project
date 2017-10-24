package com.lmy.core.beanstalkd.job;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.queue.beanstalkd.QueueHandler;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.service.impl.AlidayuSmsFacadeImpl;
import com.lmy.core.service.impl.UsrAidUtil;

@Service
public class OrderQueueManagerServiceImpl extends QueueHandler {
	private static Logger logger = LoggerFactory.getLogger(OrderQueueManagerServiceImpl.class);
	@Autowired
	private FsOrderDao fsOrderDao;
	
	@Autowired
	private FsUsrDao fsUsrDao;
	
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;

	@Override
	public String getQueueName() {
		return QueueNameConstant.QUEUE_ORDER;
	}

	@Override
	public Object handle(JSONObject data) throws Exception {
		try{
			if(data == null || data.isEmpty() || !data.containsKey("orderId") || !data.containsKey("msgType")){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			Long orderId  =  data.getLong("orderId");
			String msgType = data.getString("msgType");
			if(orderId == null || msgType == null){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			FsOrder order = this.fsOrderDao.findById(orderId);	
			if(order == null ){
				logger.warn("查无相关订单 data:{}", data);
				return null;				
			}
			if (msgType.equals(QueueNameConstant.MSG_ORDER_INFO_CHECK)) {
				checkOrderInfo(order);
			} else if (msgType.equals(QueueNameConstant.MSG_ORDER_BEGIN_CHECK)) {
				checkOrderBegin(order, data);
			}
			
		}catch(Exception e){
			logger.error("处理出错 data:{}", data,e);
			pushInQueueAgain(data);
		}
		return null;
	}

	private void checkOrderInfo(FsOrder order) {
		if (order.getOrderExtraInfo() == null || order.getOrderExtraInfo().length()==0) {
			// no extra info inputed, should notify the customer
			Long buyerId = order.getBuyUsrId();
			FsUsr user = fsUsrDao.findById(buyerId);
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
			JSONObject smsParamJson = new JSONObject();
			smsParamJson.put("time", formatter.format(order.getCreateTime()));
			smsParamJson.put("category",order.getGoodsName());
			AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, user.getRegisterMobile(), "SMS_101155069", null);
		}
	}
	
	private void checkOrderBegin(FsOrder order, JSONObject data) {
		if (order.getSellerFirstReplyTime() == null && order.getStatus().equals(OrderStatus.pay_succ.getStrValue())) {
			Map<Long,FsUsr> usrIdMap =  this.fsUsrDao.findByUsrIdsAndConvert( Arrays.asList( order.getSellerUsrId(),order.getBuyUsrId()  )  );
			String sellerUsrOpenId = usrIdMap.get( order.getSellerUsrId() ) .getWxOpenId();
			String buyUsrName = UsrAidUtil.getNickName2(usrIdMap.get( order.getBuyUsrId() ), "匿名");
			wxNoticeManagerImpl.masterWaitFirstReplyWxMsg(order, sellerUsrOpenId, buyUsrName);
			this.put(null, 60*30, null, data);
		}
	}
	
	private void pushInQueueAgain(JSONObject data){
		Integer _errorTimes = data.getInteger("_errorTimes");
		if(_errorTimes == null ){
			_errorTimes = 0;
		}
		_errorTimes = _errorTimes + 1;
		if(_errorTimes>3){
			logger.error("订单队列消息 3次确认未成功 本次放弃 等待 人工处理 data:{}", data);
			return ;
		}
		data.put("_errorTimes", _errorTimes);
		logger.info("订单队列消息 碰到问题 3 秒后再一次确认 data:{}", data);
		this.put(null, 3, null, data);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
