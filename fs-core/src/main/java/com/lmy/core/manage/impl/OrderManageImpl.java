package com.lmy.core.manage.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.core.beanstalkd.job.QueueNameConstant;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.service.impl.OrderServiceImpl;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;
@Service
public class OrderManageImpl {
	private static final Logger logger = Logger.getLogger(OrderManageImpl.class);
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	public JSONObject unifiedorderWeixin(long buyUsrId, final String buyUsrOpenId ,final String registerMobile,final String buyUsrIp,  long masterInfoId , long masterServiceCateId){
		//本地db 持久化
		JSONObject unifiedorderLocalDbPersisResult =  orderServiceImpl.unifiedorderWeixin(buyUsrId, buyUsrOpenId,registerMobile,buyUsrIp, masterInfoId, masterServiceCateId);
		if(!JsonUtils.equalDefSuccCode(unifiedorderLocalDbPersisResult)){
			return unifiedorderLocalDbPersisResult;
		}
		FsPayRecord payRecord=(FsPayRecord) JsonUtils.getBodyValue(unifiedorderLocalDbPersisResult, "payRecord");
		FsOrder order=(FsOrder) JsonUtils.getBodyValue(unifiedorderLocalDbPersisResult, "order");
		//微信统一下单接口
		JSONObject weixinUnifiedOrderResult = WeiXinInterServiceImpl.unifiedOrder1(payRecord);
		JsonUtils.setBody(weixinUnifiedOrderResult, "orderId", payRecord.getOrderId());
		JsonUtils.setBody(weixinUnifiedOrderResult, "chatSessionNo", order.getChatSessionNo());
		//压入队列 msg 异步确认支付结果
		pushMsgToConfirmWxUnifiedOrder(payRecord.getId());
		return weixinUnifiedOrderResult;
	}
	
	private void pushMsgToConfirmWxUnifiedOrder( long payRecordId ){
		JSONObject data = new JSONObject();
		data.put("payRecordId", payRecordId);
		BeanstalkClient.put(QueueNameConstant.QUEUE_WXPAY_CONFIRM, null, 5*60 +1, 60, data);
	}
	
	public JSONObject supplyOrderZXUsrInfo(long orderId , String chatSessionNo, long buyUsrId ,JSONArray dataList){
		return this.orderServiceImpl.supplyOrderZXUsrInfo(orderId, chatSessionNo, buyUsrId, dataList);
	}
	
	
	public JSONObject zxOrderHandWeiXinNotify(String respXml){
		//0001 验签失败; 0002 需要再次通知/确认; 0003 支付失败 ; 1000 支付成功; 9999 系统错误 ;
		JSONObject  notifyResult = WeiXinInterServiceImpl.analysisPayNotify(respXml);
		String out_trade_no =(String) JsonUtils.getBodyValue(notifyResult, "out_trade_no");
		String bank_type =(String) JsonUtils.getBodyValue(notifyResult, "bank_type");
		String transaction_id =(String) JsonUtils.getBodyValue(notifyResult, "transaction_id");
		if(JsonUtils.codeEqual(notifyResult, "0001") || JsonUtils.codeEqual(notifyResult, "9999") || JsonUtils.codeEqual(notifyResult, "0002")){
			logger.info("notifyResult:"+notifyResult+",respXml:"+respXml+",  无需处理");
			return notifyResult;
		}
		//0003 支付失败
		else if(JsonUtils.codeEqual(notifyResult, "0003")  ){
			JSONObject result = orderServiceImpl.zxOrderHandWeiXinNotify(out_trade_no, bank_type ,transaction_id, false);
			logger.info("notifyResult:"+notifyResult+",respXml:"+respXml+",  处理结果:"+result);
			return result;
		}
		//1000 支付成功
		else if ( JsonUtils.codeEqual(notifyResult, "1000")){
			JSONObject result =orderServiceImpl.zxOrderHandWeiXinNotify(out_trade_no, bank_type ,transaction_id, true);
			logger.info("notifyResult:"+notifyResult+",respXml:"+respXml+",  处理结果:"+result);
			return result;
		}else{
			logger.warn("notifyResult:"+notifyResult+",respXml:"+respXml+", 进入未知分支");
			return JsonUtils.commonJsonReturn("0004", "进入未知分支");
		}
	}
}
