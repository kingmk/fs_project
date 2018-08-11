package com.lmy.core.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.core.beanstalkd.job.QueueNameConstant;
import com.lmy.core.dao.FsCouponInstanceDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsPayRecordDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsCouponInstance;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.service.impl.OrderAidUtil;
import com.lmy.core.utils.FsEnvUtil;
import com.lmy.core.utils.FsExecutorUtil;
import com.lmy.core.utils.UniqueNoUtil;
/**
 * @author fidel
 * @since 2017/05/09
 */
@Service
public class OrderRefundServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(OrderRefundServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao ;
	@Autowired
	private FsMasterInfoDao  fsMasterInfoDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsPayRecordDao fsPayRecordDao;
	@Autowired
	private FsCouponInstanceDao fsCouponInstanceDao;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	@Autowired
	private OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	
	public JSONObject commonUsrRefundNav(long buyUsrId, long orderId){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null || !order.getBuyUsrId().equals(buyUsrId)){
				logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			List<FsMasterInfo> masterInfolist  =  this.fsMasterInfoDao.findByUsrIds2(Arrays.asList(order.getSellerUsrId()), "approved"	, null);
			if(CollectionUtils.isEmpty(masterInfolist)){
				logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误 masterInfolist.size="+CommonUtils.getListSize(masterInfolist));
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			FsMasterInfo masterInfo = masterInfolist.get(0);
			if(masterInfo == null){
				logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			Date now = new Date();
			boolean isCanRefund = OrderAidUtil.isOrderCanApplyRefund(order, now);
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "orderId", orderId);
			JsonUtils.setBody(result, "chatSessionNo", order.getChatSessionNo());
			JsonUtils.setBody(result, "masterName",   UsrAidUtil.getMasterNickName(masterInfo, null, "") );
			JsonUtils.setBody(result, "masterHeadImgUrl",  UsrAidUtil.getMasterHeadImg(masterInfo, null, "") );
			JsonUtils.setBody(result, "isCanRefund", isCanRefund ? 'Y':'N');  //是否可以退款
			JsonUtils.setBody(result, "orderStatus", order.getStatus());  
			return result;
		}catch(Exception e){
			logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",系统错误",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	/** **/
	public JSONObject commonUsrRefundApply(long buyUsrId, long orderId,String refundReason){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null || !order.getBuyUsrId().equals(buyUsrId)){
				logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			Date now = new Date();
			boolean isCanRefund = OrderAidUtil.isOrderCanApplyRefund(order, now);
			if(!isCanRefund){
				logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",当前状态不可申请退款");
				return JsonUtils.commonJsonReturn("0002", "当前状态不可申请退款");
			}
			int effecNum = this.fsOrderDao.updateForRefundApplied(order.getId(), "N", refundReason, order.getPayRmbAmt(), now);
			Assert.isTrue( effecNum ==1 );
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.warn("buyUsrId:"+buyUsrId+",orderId:"+orderId+",refundReason:"+refundReason,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	/**  拿到微信退款申请(不是退款最终结果)结果的处理程序 **/
	public  JSONObject doRefundAfterWeiXinApplyResult(long id ,  Long refundBeanId, boolean isWxRefundApplySucc,String wxRespXml){
		try{
			if(!isWxRefundApplySucc){
				logger.info("微信 退款申请失败 orderId:"+id+" ,处理开始");
				doRefundAfterWxApplyFail(id, refundBeanId,wxRespXml);
				logger.info("微信 退款申请失败 orderId:"+id+" ,处理完成");
			}else{
				logger.info("微信 退款申请成功 orderId:"+id+" ,无需处理");
				return JsonUtils.commonJsonReturn();
			}			
		}catch(Exception e){
			logger.error("orderId:{} , refundBeanId:{} ,isWxRefundApplySucc:{}", id, refundBeanId ,  isWxRefundApplySucc , e);
		}
		return null;
	}
	
	private void doRefundAfterWxApplyFail(final long id , final  Long refundBeanId , final String wxRespXml){
		final FsOrder order = fsOrderDao.findById(id);
		this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				Date now  = new Date();
				int effectNum = fsOrderDao.updateRefundAfterWxRefundApplyResult(id, false, now);
				Assert.isTrue( effectNum ==1 );
				if (order.getCouponId() != null) {
					fsCouponInstanceDao.updateStatus(order.getCouponId(), FsCouponInstance.STATUS_UNUSED);
				}
				
				if(refundBeanId == null){
					FsPayRecord payBeanForUpdate =  new FsPayRecord();
					payBeanForUpdate.setId( refundBeanId  );
					payBeanForUpdate.setTradeConfirmTime(now).setTradeStatus("fail").setUpdateTime(now);
					payBeanForUpdate.setRespMsg(wxRespXml);
					fsPayRecordDao.update(payBeanForUpdate);
				}
				return true;
			}
		});
	}
	
	
	/** 退款审批: 同意退款  系统退款直接调用该method  会发起call 微信退款交易 <br>
	 *   如果是自动退款 且微信退款交易 受理成功 -->will call OrderEvaluateServiceImpl#commentOrder2 自动评价 。 退款自动评价都是 2星 add by fidel at 2017/05/31 13:43<br>
	 * **/
	private JSONObject doRefundAuditApproved(final FsOrder order,final boolean isAutoRefund, final String refundReason,final String refundAuditWord){
		try{
			//退款中的可以再一次发起退款
			String [] expectStatus = new String [] {OrderStatus.refund_applied.getStrValue()};
			if(order == null || !ArrayUtils.contains(expectStatus, order.getStatus())   ){
				logger.warn("orderId:"+(order!=null ? order.getId():null)+",状态错误 当前状态:"+order.getStatus()+",期待状态:refund_applied");
				return JsonUtils.commonJsonReturn("0001", "数据/状态错误");
			}
			List<FsPayRecord> payRecordList =   this.fsPayRecordDao.findByOrderIdAndTradeType(order.getId(),null);
			JSONObject analysisPayRecordResult =   autoRefund_orgPayMatchCondition(payRecordList,order.getId());
			if(!JsonUtils.equalDefSuccCode(analysisPayRecordResult)){
				return analysisPayRecordResult;
			}
			int paySuccNum =(Integer) JsonUtils.getBodyValue(analysisPayRecordResult, "paySuccNum");
			int refundedNum = (Integer) JsonUtils.getBodyValue(analysisPayRecordResult, "refundedNum");
			int refundingNum = (Integer) JsonUtils.getBodyValue(analysisPayRecordResult, "refundingNum");
			final FsPayRecord paySuccBean = (FsPayRecord) JsonUtils.getBodyValue(analysisPayRecordResult, "paySuccBean");
			if(paySuccNum<1 || paySuccBean ==null ){
				logger.warn("orderId:"+order.getId() +",paySuccNum:"+paySuccNum+",没有支付成功记录,不允许退款");
				return JsonUtils.commonJsonReturn("0002", "没有支付成功记录,不允许退款");
			}
			if(refundedNum>0){
				logger.warn("orderId:"+order.getId() +",refundedNum:"+refundedNum+",存在退款成功记录,不符合退款条件");
				return JsonUtils.commonJsonReturn("0003", "存在退款成功记录,不符合退款条件");
			}
			if(refundingNum>0){
				logger.warn("orderId:"+order.getId() +",refundingNum:"+refundingNum+",存在退款中记录,不符合退款条件");
				return JsonUtils.commonJsonReturn("0004", "存在退款中记录,不符合退款条件");
			}
			final Date now = new Date();
			final FsPayRecord payRecordForInsert = autoRefundBuildPayRecord(paySuccBean, now);
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					int affectNum =fsOrderDao.updateForRefundAudit(order.getId(), true, refundReason, refundAuditWord, null, now);
					Assert.isTrue(  affectNum== 1  );
					if (order.getCouponId() != null) {
						fsCouponInstanceDao.updateStatus(order.getCouponId(), FsCouponInstance.STATUS_UNUSED);
					}
					fsPayRecordDao.insert(payRecordForInsert);
					return null;
				}
			});
			//call 微信 退款接口
			asynCallWxRefundAndPushWxMsgAfterRefundAuditApproved(payRecordForInsert, paySuccBean, order, isAutoRefund, refundReason, refundAuditWord);
			JSONObject result =  JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "refundBean", payRecordForInsert);
			JsonUtils.setBody(result, "paySuccBean", paySuccBean);
			return result;
		}catch(Exception e){
			logger.error("orderId:"+(order!=null ? order.getId():null)+",系统错误",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	private void asynCallWxRefundAndPushWxMsgAfterRefundAuditApproved(final FsPayRecord refundBean ,final  FsPayRecord orgPaySuccBean,final FsOrder order ,
			final boolean isAutoRefund,final  String refundReason,final String refundAuditWord){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				boolean wxRefundSucc = callWeiXinRefundAndHandWxResp(refundBean, orgPaySuccBean,order, isAutoRefund,refundReason);
				if(wxRefundSucc){
					_pushWxMsgAfterRefundAuditApproved(order.getId(), refundReason, refundAuditWord);	
				}
			}
		};
		FsExecutorUtil.getExecutor().execute(r);
	}
	
	private void _pushRefundConfirmWxRefundMsgInQueue(long payRecordId , boolean isCft){
		JSONObject waitConfrimRefundData = new JSONObject();
		waitConfrimRefundData.put("payRecordId", payRecordId);
		if(isCft){
			BeanstalkClient.put(QueueNameConstant.QUEUE_WXPAY_CONFIRM, null, 3, null, waitConfrimRefundData);	
		}else{
			BeanstalkClient.put(QueueNameConstant.QUEUE_WXPAY_CONFIRM, null, 3, null, waitConfrimRefundData);
		}
		
	}
	
	public boolean callWeiXinRefundAndHandWxResp(FsPayRecord refundBean , FsPayRecord orgPaySuccBean,FsOrder order , final boolean isAutoRefund,String refundReason){
		boolean wxRefundSucc = true;
		try{
			//call 微信退款交易接口
			JSONObject weixinRefundResult = WeiXinInterServiceImpl.refund1(orgPaySuccBean, refundBean);
			logger.info("weixinRefundResult:"+weixinRefundResult.toJSONString());
			if(!FsEnvUtil.isPro() && !JsonUtils.equalDefSuccCode(weixinRefundResult)){
				logger.warn("非生产环境 模拟微信退款.....orderId:{}", order.getId());
				JsonUtils.setHead(weixinRefundResult, "0000", "模拟成功...");
			}
			//需要确认
			if(JsonUtils.codeEqual(weixinRefundResult, "1000")  ){
				logger.info("发起微信退款 遇到未知结果需要确认:");
				_pushRefundConfirmWxRefundMsgInQueue(refundBean.getId(), "CFT".equals(orgPaySuccBean.getBankType()));
			}
			//退款 申请  成功
			else if(JsonUtils.equalDefSuccCode(weixinRefundResult)){
				//系统自动退款 需要自动评价， 推送微信消息
				if(isAutoRefund){
					this.orderEvaluateServiceImpl.evaluateOrder(order.getBuyUsrId(), order.getSellerUsrId(), order.getId(), order.getGoodsId(), 1l, 1l,1l, refundReason, "N", 1);
				}
				_pushRefundConfirmWxRefundMsgInQueue(refundBean.getId(), "CFT".equals(orgPaySuccBean.getBankType()));
			}
			//退款申请失败
			else{
				doRefundAfterWeiXinApplyResult( order.getId() ,  refundBean!=null ? refundBean.getId():null , false  ,(String)JsonUtils.getBodyValue(weixinRefundResult, "respMsg"));
				wxRefundSucc = false;
			}
		}catch(Exception e){
			wxRefundSucc = false;
			logger.error("orderId:{}", order.getId());
			logger.error("微信退款发生错误", e);
		}
		return wxRefundSucc;
	}
	
	
	private void _pushWxMsgAfterRefundAuditApproved(long orderId , String refundReason,String refundAuditWord){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			Map<Long ,FsUsr> idUsrMap = fsUsrDao.findByUsrIdsAndConvert( Arrays.asList(order.getBuyUsrId() , order.getSellerUsrId()) );
			String buyUsrName =UsrAidUtil.getNickName2(idUsrMap.get( order.getBuyUsrId() ), "匿名");
			wxNoticeManagerImpl.orderRefundMsgMasterWxMsg(orderId, order.getChatSessionNo(), order.getSellerUsrId(), 
					idUsrMap.get( order.getSellerUsrId() ).getWxOpenId(), order.getGoodsName(), order.getRefundRmbAmt(), buyUsrName, refundAuditWord);    //refundReason-->refundAuditWord  modify  by fidel at  2017/06/05 12:24
			
			wxNoticeManagerImpl.orderRefundMsgUserWxMsg(orderId, order.getGoodsName(), order.getBuyUsrId(), 
					idUsrMap.get( order.getBuyUsrId() ).getWxOpenId(), order.getChatSessionNo(), true, refundAuditWord);
		}catch(Exception e){
			logger.error("orderId:{},refundReason:{},refundAuditWord{}", orderId ,refundReason ,refundAuditWord );
			logger.error("退款审批通过 推送微信消息错误", e );
		}
	}
	
	
	private JSONObject doRefundAuditRefuse(final FsOrder order,boolean isAutoRefund, final String refundReason,final String refundAuditWord){
		try{
			String [] expectStatus = new String [] {  OrderStatus.refund_applied.getStrValue() };
			if(order == null || !ArrayUtils.contains(expectStatus, order.getStatus())   ){
				logger.warn("orderId:"+(order!=null ? order.getId():null)+",状态错误");
				return JsonUtils.commonJsonReturn("0001", "数据/状态错误");
			}
			this.fsOrderDao.updateForRefundAudit(order.getId(), false, refundReason, refundAuditWord, null, new Date());
			_pushWxRefundAuditRefuseMsg(order, refundAuditWord);
			return 	JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("退款审批 拒绝 发生系统错误 orderId:"+(order!=null ? order.getId():null)+",系统错误",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	private void _pushWxRefundAuditRefuseMsg(final FsOrder order, String refundAuditWord){
		try{
			logger.info("退款审批拒接开始推送微信消息  orderId:{},refundAuditWord:{}", order.getId() ,refundAuditWord);
			Map<Long,FsUsr> idUsrMap = this.fsUsrDao.findByUsrIdsAndConvert( Arrays.asList( order.getSellerUsrId(),order.getBuyUsrId() ) );
			wxNoticeManagerImpl.orderRefundMsgUserWxMsg(order.getId(), order.getGoodsName(), order.getBuyUsrId(), 
					idUsrMap.get( order.getBuyUsrId() ).getWxOpenId(), order.getChatSessionNo(), false, refundAuditWord);
		}catch(Exception e){
			logger.error("orderId:{},refundAuditWord:{}", order.getId() ,refundAuditWord);
			logger.error("拒绝给老师推送消息发生错误", e );
		}
	}
	
	/** 可以发起多次退款申请 但是只能成功一笔 --->退款交易表中 至多同时只存在 一条 ing|succ 的退款记录 <br>
	 * 同意退款， 同时会发起 call 微信 退款接口 . 退款成功会会发送微信通知到 master and buyUsr。会push beanstalk msg for confirm wx refund trans  这些都是异步执行 
	 * **/
	public JSONObject refundAudit(final FsOrder order, boolean isAgree,boolean isAutoRefund, final String refundReason,final String refundAuditWord){
		JSONObject result = null;
		if(isAgree){
			result = this.doRefundAuditApproved(order, isAutoRefund, refundReason, refundAuditWord);
		}else{
			result = this.doRefundAuditRefuse(order, isAutoRefund, refundReason, refundAuditWord);
		}
		return result;
	}
	
	private JSONObject autoRefund_orgPayMatchCondition(List<FsPayRecord> payRecordList,Long orderId){
		if(CollectionUtils.isEmpty(payRecordList)){
			logger.warn("退款, 数据错误,未能查询到任何 支付记录orderId:{}", orderId);
			return JsonUtils.commonJsonReturn("0001", "数据错误");
		}
		//支付成功的记录数(已退款+退款中+支付成功)
		int paySuccNum = 0;
		//退款成功记录数
		int refundedNum = 0;
		//退款中的记录数
		int refundingNum = 0;
		FsPayRecord paySuccBean = null;
		for(FsPayRecord bean :  payRecordList){
			//
			if("unifiedorder".equals(bean.getTradeType())){
				if("succ".equals( bean.getTradeStatus() )){
					paySuccNum = paySuccNum + 1;
					 paySuccBean = bean;
				}
			}
			if("refund".equals(bean.getTradeType())){
				if("succ".equals( bean.getTradeStatus() ) ){
					paySuccNum = paySuccNum + 1;
					refundedNum = refundedNum + 1;
				}
				if("ing".equals( bean.getTradeStatus() )){
					refundingNum = refundingNum + 1;
					paySuccNum = paySuccNum + 1;
				}
			}
		}
		JSONObject result = JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "paySuccNum", paySuccNum);
		JsonUtils.setBody(result, "refundedNum", refundedNum);
		JsonUtils.setBody(result, "refundingNum", refundingNum);
		JsonUtils.setBody(result, "paySuccBean", paySuccBean);
		return result;
	}
	
	private FsPayRecord autoRefundBuildPayRecord(FsPayRecord paySuccBean,Date now){
		FsPayRecord newBean = new FsPayRecord();
		BeanUtils.copyProperties(paySuccBean, newBean);
		newBean.setId(null);
		newBean.setUpdateTime(now).setCreateTime(now);
		newBean.setTradeType("refund").setTradeStatus("ing").setOutTradeNo( UniqueNoUtil.getSimpleUniqueNo() ).setRefundFee( paySuccBean.getTotalFee() );
		newBean.setRespTradeNo(null).setTradeConfirmTime(null).setNotifyUrl(null).setRemark(null).setRespTradeNo(null);
		return newBean;
	}
	
}
