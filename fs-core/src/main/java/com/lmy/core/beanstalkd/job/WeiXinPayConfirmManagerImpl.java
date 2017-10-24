package com.lmy.core.beanstalkd.job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.exception.BizException;
import com.lmy.common.queue.beanstalkd.QueueHandler;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsPayRecordDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.service.impl.OrderServiceImpl;
import com.lmy.core.service.impl.OrderSettlementServiceImpl;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;
import com.lmy.core.utils.FsEnvUtil;
@Service
public class WeiXinPayConfirmManagerImpl extends QueueHandler{
	private static Logger logger = LoggerFactory.getLogger(WeiXinPayConfirmManagerImpl.class);
	@Autowired
	private FsPayRecordDao fsPayRecordDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	@Autowired 
	private OrderSettlementServiceImpl orderSettlementServiceImpl;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	@Override
	public String getQueueName() {
		return QueueNameConstant.QUEUE_WXPAY_CONFIRM;
	}
	/**
	 * data key:payRecordId
	 */
	@Override
	public Object handle(JSONObject data) throws Exception {
		try{
			if(data == null || data.isEmpty() || !data.containsKey("payRecordId")){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			Long payRecordId = data.getLong("payRecordId");
			// 数据类型 ， 状态判断 check begin
			FsPayRecord payRecord = fsPayRecordDao.findById(payRecordId);
			if(payRecord ==null || !"weixin".equals(payRecord.getPayChannel())){
				logger.warn("查无 微信支付交易 data:{}", data);
				return null;
			}
			if( !"ing".equals(payRecord.getTradeStatus())){
				if(logger.isDebugEnabled())	logger.debug("微信支付 交易已处理 data:{},当前交易状态:tradeStatus:{} , 期待交易状态:{}", data,payRecord.getTradeStatus(),"ing");
				return null;
			}
			if("unifiedorder".equals(payRecord.getTradeType() )){
				doUnifiedorderConfirm(payRecord, data);
			}
			else if("refund".equals(payRecord.getTradeType() )){
				doRefundConfirm(payRecord, data);
			}
			else if("transfers".equals(payRecord.getTradeType() )){
				doTransfersConfirm(payRecord, data);
			}
			else{
				logger.warn("微信支付 本地交易类型错误 data:{},payRecordId:{}, payRecord.getTradeType:{} ", data,payRecord.getId() , payRecord.getTradeType()  );
			}
			return null;
		}catch(Exception e){
			logger.error("微信支付确认错误data:{}", data, e);
			pushInQueueAgain(data);
			return null;
		}
	}
	private void doTransfersConfirm(FsPayRecord payRecord,JSONObject jobData){
		//查询微信 转账结果 0001 转账(申请)失败;0000 转账成功; 1000 PROCESSING 需再一次确认
		JSONObject  transferQueryResult = WeiXinInterServiceImpl.gettransferinfo(payRecord.getOutTradeNo());
		if(JsonUtils.equalDefSuccCode(transferQueryResult)){
			//this.orderSettlementServiceImpl.handWxTransfers1(payRecord.getOrderId(), true, payRecord.getId(),
				//	(String)JsonUtils.getBodyValue(transferQueryResult, "respMsg"), "队列查询确认succ");
		}
		else if(JsonUtils.codeEqual(transferQueryResult,"0001")){
			//this.orderSettlementServiceImpl.handWxTransfers1(payRecord.getOrderId(), false, payRecord.getId(),
				//	(String)JsonUtils.getBodyValue(transferQueryResult, "respMsg"), "队列查询确认fail");
		}
		else if(JsonUtils.codeEqual(transferQueryResult,"1000")){
			//this.orderSettlementServiceImpl.handWxTransfer1ForPushQueueMsg(payRecord.getId());
		}
	}
	private void doUnifiedorderConfirm(FsPayRecord payRecord,JSONObject data) throws Exception{
		FsOrder order = this.fsOrderDao.findById( payRecord.getOrderId()  );
		if(order == null){
			logger.warn("查无 订单交易数据 data:{},orderId:{}", data ,  payRecord.getOrderId()  );
			return ;
		}
		if(!"init".equals(  order.getStatus())){
			logger.warn("查无 订单状态错误交易数据 data:{},当前交易状态::{} , 期待交易状态:{} , orderId:{}", data ,  order.getStatus() , "init"  , order.getId() );
			return ;
		}
		//查询 微信交易状态 
		String weixinRespXml = WeiXinInterServiceImpl.payOrderQuery1(payRecord.getOutTradeNo(), null);
		JSONObject weiXinQueryResult = WeiXinInterServiceImpl.analysitWeiXinQueryResp(weixinRespXml);
		//支付成功
		if(JsonUtils.equalDefSuccCode(weiXinQueryResult)){
			JSONObject result = orderServiceImpl.zxOrderHandWeiXinNotify(payRecord.getOutTradeNo(),  
					(String)JsonUtils.getBodyValue(weiXinQueryResult, "bank_type") ,
					(String)JsonUtils.getBodyValue(weiXinQueryResult, "transaction_id") , true);
			if(JsonUtils.codeEqual(result, "9999")){
				pushInQueueAgain(data);
			}
		}
		//支付失败
		else if(  JsonUtils.codeEqual(weiXinQueryResult, "0001") ){
			JSONObject result = orderServiceImpl.zxOrderHandWeiXinNotify(payRecord.getOutTradeNo(),  
					(String)JsonUtils.getBodyValue(weiXinQueryResult, "bank_type") , 
					(String)JsonUtils.getBodyValue(weiXinQueryResult, "transaction_id") , false);
			if(JsonUtils.codeEqual(result, "9999")){
				pushInQueueAgain(data);
			}
		}
		//需要再次确认
		else if(  JsonUtils.codeEqual(weiXinQueryResult, "1000") ){
			//再一次入队列
			if(JsonUtils.codeEqual(weiXinQueryResult, "9999")){
				pushInQueueAgain(data);
			}
		}else{
			throw new BizException("未知code");
		}
	}
	private void doRefundConfirm(FsPayRecord payRecord,JSONObject data){
		FsOrder order = this.fsOrderDao.findById( payRecord.getOrderId()  );
		if(order == null){
			logger.warn("查无 订单交易数据 data:{},orderId:{}", data ,  payRecord.getOrderId()  );
			return ;
		}
		if( OrderStatus.refund_applied.equals(  order.getStatus()) ){
			logger.warn("查无 订单状态错误交易数据 data:{},当前交易状态::{} , 期待交易状态:{} , orderId:{}", data ,  order.getStatus() , "refund_applied"  , order.getId() );
			return ;
		}
		logger.info("begin comfirm refund ... orderId:"+payRecord.getOrderId());
		//退款微信交易查询
		//0000 明确退款成功; 0001 明确退款失败; 1000 退款中; 9999 查询碰到系统错误 
		JSONObject refundQueryResult = WeiXinInterServiceImpl.refundQuery1(payRecord.getOutTradeNo());
		String respXml = (String)JsonUtils.getBodyValue(refundQueryResult, "respXml");
		if(!FsEnvUtil.isPro() && !JsonUtils.equalDefSuccCode(refundQueryResult)){
			logger.warn("非生产环境 模拟微信退款成功.....orderId:{}", order.getId());
			JsonUtils.setHead(refundQueryResult, "0000", "模拟成功...");
		}
		//0000 明确退款成功
		if(JsonUtils.codeEqual(refundQueryResult, "0000")){
			this.orderServiceImpl.handWeiXinRefundConfirm(payRecord, true, null, null, respXml, null);
			//wxNoticeManagerImpl.orderRefundedMsgToMaster(order.getId(), order.getZxCateId(), order.getSellerUsrId(), order.getSettlementMasterServiceFee(), order.getRefundReason());
			//wxNoticeManagerImpl.orderRefundMsgToBuyUsr(order, true);
		}
		//0001 明确退款失败
		else if(JsonUtils.codeEqual(refundQueryResult, "0001")){
			this.orderServiceImpl.handWeiXinRefundConfirm(payRecord, false, null, null, respXml, null);
			//wxNoticeManagerImpl.orderRefundedMsgToMaster(order.getId(), order.getZxCateId(), order.getSellerUsrId(), order.getSettlementMasterServiceFee(), order.getRefundReason());
			//wxNoticeManagerImpl.orderRefundMsgToBuyUsr(order, false);
		}
		//1000 退款中
		else if(JsonUtils.codeEqual(refundQueryResult, "1000")){
			//用零钱支付的退款20分钟内到账，银行卡支付的退款3个工作日后重新查询退款状态
			//零钱 财富通
			if("CFT".equals(payRecord.getBankType())){
				this.put(null, 2* 60 , null, data);
			}else{
				this.put(null,  1800   , null, data);
			}
		}
		//9999 查询碰到系统错误 
		else if(JsonUtils.codeEqual(refundQueryResult, "0001")){
			pushInQueueAgain(data);
		}else{
			logger.error("未知状态refundQueryResult:{},payRecord:{},data:{}", refundQueryResult,payRecord,data);
		}
	}
	
	
	private void pushInQueueAgain(JSONObject data){
		Integer _errorTimes = data.getInteger("_errorTimes");
		if(_errorTimes == null ){
			_errorTimes = 0;
		}
		_errorTimes = _errorTimes + 1;
		if(_errorTimes>3){
			logger.error("微信支付确认错误达到 3次 本次放弃 等待 人工出路 data:{}", data);
			return ;
		}
		data.put("_errorTimes", _errorTimes);
		logger.info("微信支付确认 碰到问题 3 秒后再一次确认 data:{}", data);
		this.put(null, 3, null, data);
	}
	
}
