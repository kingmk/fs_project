package com.lmy.core.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.dao.FsCalendarOrderDao;
import com.lmy.core.dao.FsCalendarUserDao;
import com.lmy.core.dao.FsCalendarUserinfoDao;
import com.lmy.core.dao.FsPayRecordDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsCalendarOrder;
import com.lmy.core.model.FsCalendarUser;
import com.lmy.core.model.FsCalendarUserinfo;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.utils.UniqueNoUtil;

@Service
public class CalendarOrderServiceImpl {

	private static final Logger logger = Logger.getLogger(CalendarOrderServiceImpl.class);

	@Autowired
	private FsCalendarUserDao fsCalendarUserDao;
	@Autowired
	private FsCalendarUserinfoDao fsCalendarUserinfoDao;
	@Autowired
	private FsCalendarOrderDao fsCalendarOrderDao;
	@Autowired 
	private FsPayRecordDao fsPayRecordDao;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	
//	private Long memberFee = ;
	
	public JSONObject unifiedOrder(String openId, Long infoId, final String userIp) {
		
		JSONObject resultOrder = createOrderPayRecord(openId, infoId, userIp);
		
		if (!JsonUtils.equalDefSuccCode(resultOrder)){
			return resultOrder;
		}
		FsPayRecord payRecord = (FsPayRecord) JsonUtils.getBodyValue(resultOrder, "payRecord");
		JSONObject resultWX = WeiXinInterServiceImpl.unifiedOrder1(payRecord);
		if (!JsonUtils.equalDefSuccCode(resultWX)) {
			return resultWX;
		}
		
		JsonUtils.setBody(resultWX, "orderId", payRecord.getOrderId());
		
		return resultWX;
	}
	
	
	private JSONObject createOrderPayRecord(final String openId, Long infoId, final String userIp) {
		
		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			return JsonUtils.commonJsonReturn("0010", "参数错误：用户不存在");
		}
		
		FsCalendarUserinfo fsInfo = fsCalendarUserinfoDao.findById(infoId);
		if (fsInfo == null) {
			return JsonUtils.commonJsonReturn("0011", "参数错误：用户信息不存在");
		}
		
		if (!fsInfo.getUserId().equals(fsUser.getId())) {
			logger.info("fail to make payment, info's userId="+fsInfo.getUserId()+" not equal with user's id="+fsUser.getId());
			return JsonUtils.commonJsonReturn("0012", "参数错误：用户信息不属于当前用户");
		}
		
		
		final FsCalendarOrder fsOrder = buildOrder(fsUser.getId(), infoId, FsCalendarOrder.TYPE_MEMBER_FEE);
		try{
			return 
			fsTransactionTemplate.execute(new TransactionCallback<JSONObject>() {
				@Override
				public JSONObject doInTransaction(TransactionStatus status) {
					//fsChatSessionDao.batchInsert(listChatSession);
					fsCalendarOrderDao.insert(fsOrder);
					final FsPayRecord payRecord = buildPayRecord(fsOrder, openId, userIp);
					fsPayRecordDao.insert(payRecord);
					JSONObject result = JsonUtils.commonJsonReturn();
					JsonUtils.setBody(result, "order", fsOrder);
					JsonUtils.setBody(result, "payRecord", payRecord);
					return result;
				}
			});
		}catch(Exception e){
			logger.error("userId="+fsUser.getId()+",infoId="+infoId,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	
	private FsCalendarOrder buildOrder(Long userId, Long infoId, String orderType) {
		FsCalendarOrder fsOrder = new FsCalendarOrder();
		Date now = new Date();
		Long memberFee = Long.parseLong(ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.applet.price"));
		fsOrder.setUserId(userId).setInfoId(infoId).setOrderNum(UniqueNoUtil.getSimpleUniqueNo())
			.setOrderType(orderType).setName("雷历年费").setDetail("雷历年费 "+CalendarAidUtil.dateFormatFull.format(now))
			.setPayRmbAmt(memberFee).setDiscountRmbAmt(0l).setStatus(FsCalendarOrder.STATUS_INIT)
			.setCreateTime(now);
		
		return fsOrder;
	}
	
	private FsPayRecord buildPayRecord(FsCalendarOrder fsOrder, String openId, String userIp) {
		FsPayRecord payRecord = new FsPayRecord();
		payRecord.setAppId( ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.applet.appId") ) ; 
		payRecord.setAttach(null) ; 
		payRecord.setMchId( ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.merId") )  ;
		payRecord.setNotifyUrl(ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.service.basehost") + ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.applet.pay.notify.url") )  ;
		payRecord.setBody(fsOrder.getName()).setCreateTime(fsOrder.getCreateTime());
		payRecord.setDetail(fsOrder.getDetail()).setFeeType("CNY").setGoodsTag(null) ;
		payRecord.setOpenId(openId).setOrderId(fsOrder.getId()).setOutTradeNo("LEILI_"+UniqueNoUtil.getSimpleUniqueNo()) .setPayChannel("weixin")
		.setRefundFee(0l).setRemark(null).setSpbillCreateIp(userIp).setTotalFee(fsOrder.getPayRmbAmt()).setTradeStatus("ing")
		.setTradeType("unifiedorder").setUsrId(fsOrder.getUserId()).setUpdateTime(fsOrder.getCreateTime());
		
		return payRecord;
	}

	// make payment order and business order succeed
	public JSONObject handleWxNotify(String respXml){
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
			JSONObject result = handleWxNotify(out_trade_no, bank_type ,transaction_id, false);
			logger.info("notifyResult:"+notifyResult+",respXml:"+respXml+",  处理结果:"+result);
			return result;
		}
		//1000 支付成功
		else if ( JsonUtils.codeEqual(notifyResult, "1000")){
			JSONObject result =handleWxNotify(out_trade_no, bank_type ,transaction_id, true);
			logger.info("notifyResult:"+notifyResult+",respXml:"+respXml+",  处理结果:"+result);
			return result;
		}else{
			logger.warn("notifyResult:"+notifyResult+",respXml:"+respXml+", 进入未知分支");
			return JsonUtils.commonJsonReturn("0004", "进入未知分支");
		}
	}
	

	private JSONObject handleWxNotify(String out_trade_no , final String bank_type ,final String transaction_id  , final boolean paySucc){
		try{
			if( StringUtils.isEmpty(out_trade_no) && StringUtils.isEmpty(transaction_id) ){
				logger.warn("out_trade_no:"+out_trade_no+", transaction_id:"+transaction_id+" ,bank_type:"+bank_type+", paySucc:"+paySucc +"  参数错误");
				return JsonUtils.commonJsonReturn("0001", "参数错误");
			}
			final FsPayRecord payRecord = this.fsPayRecordDao.findByOutTradeNo(out_trade_no);
			if(payRecord==null){
				logger.warn("查无记录  out_trade_no:"+out_trade_no+", transaction_id:"+transaction_id+" ,bank_type:"+bank_type+", paySucc:"+paySucc );
				return JsonUtils.commonJsonReturn("0002", "数据错误|查无记录");				
			}
			if(!"ing".equals(payRecord.getTradeStatus())  || !"unifiedorder".equals( payRecord.getTradeType())){
				logger.warn("状态或|类型错误  out_trade_no:"+out_trade_no+", transaction_id:"+transaction_id+" ,bank_type:"+bank_type+", paySucc:"+paySucc
						+",payRecord.id="+payRecord.getId()+",payRecord.getTradeType="+payRecord.getTradeType()+",payRecord.getTradeStatus="+ payRecord.getTradeStatus());
				return JsonUtils.commonJsonReturn("0002", "状态或|类型错误");				
			}
			
			final FsCalendarOrder order = this.fsCalendarOrderDao.findById(payRecord.getOrderId());
			
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					Date now = new Date();
					int effectPayNum = fsPayRecordDao.updateForPayByResult(payRecord.getId(),bank_type,transaction_id ,paySucc, now);
					Assert.isTrue(effectPayNum ==1);
					FsCalendarOrder orderForUpdate = new FsCalendarOrder();
					orderForUpdate.setId(payRecord.getOrderId());
					if (paySucc) {
						orderForUpdate.setStatus(FsCalendarOrder.STATUS_PAY_SUCC);
					} else {
						orderForUpdate.setStatus(FsCalendarOrder.STATUS_PAY_FAIL);
					}
					effectPayNum = fsCalendarOrderDao.update(orderForUpdate);
					Assert.isTrue(effectPayNum ==1);
					FsCalendarUserinfo infoForUpdate = new FsCalendarUserinfo();
					if (paySucc) {
						infoForUpdate.setId(order.getInfoId());
						infoForUpdate.setStatus(FsCalendarUserinfo.STATUS_PAID);
						fsCalendarUserinfoDao.update(infoForUpdate);
					}
					
					return true;
				}
			});
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("out_trade_no:"+out_trade_no+", transaction_id:"+transaction_id+" ,bank_type:"+bank_type+", paySucc:"+paySucc  ,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	// only make business order succeed and change the relative userinfo status
	public JSONObject orderSucceed(String openId, Long orderId) {

		FsCalendarUser fsUser = fsCalendarUserDao.findByOpenId(openId);
		if (fsUser == null) {
			return JsonUtils.commonJsonReturn("0010", "参数错误：用户不存在");
		}
		
		final FsCalendarOrder fsOrder = fsCalendarOrderDao.findById(orderId);
		if (fsOrder == null) {
			return JsonUtils.commonJsonReturn("0011", "参数错误：订单不存在");
		} else if (!fsOrder.getUserId().equals(fsUser.getId())) {
			return JsonUtils.commonJsonReturn("0012", "参数错误：订单不属于该用户");
		}
		
		
		FsCalendarUserinfo fsInfo = fsCalendarUserinfoDao.findById(fsOrder.getInfoId());
		if (fsInfo == null) {
			return JsonUtils.commonJsonReturn("0013", "参数错误：用户信息不存在");
		}

		this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				Date now = new Date();
				FsCalendarOrder fsOrder2 = new FsCalendarOrder();
				fsOrder2.setId(fsOrder.getId());
				fsOrder2.setStatus(FsCalendarOrder.STATUS_PAY_SUCC);
				fsOrder2.setPayConfirmTime(now);
				fsOrder2.setUpdateTime(now);
				fsCalendarOrderDao.update(fsOrder2);
				
				FsCalendarUserinfo fsInfo = new FsCalendarUserinfo();
				fsInfo.setId(fsOrder.getInfoId());
				fsInfo.setStatus(FsCalendarUserinfo.STATUS_PAID);
				fsInfo.setUpdateTime(now);
				fsCalendarUserinfoDao.update(fsInfo);
				
				return true;
			}
		});
		return JsonUtils.commonJsonReturn();
	}
}
