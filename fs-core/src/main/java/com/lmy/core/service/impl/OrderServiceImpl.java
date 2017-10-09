package com.lmy.core.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.beanstalkd.job.QueueNameConstant;
import com.lmy.core.dao.FsChatSessionDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsMasterServiceCateDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsPayRecordDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsChatSession;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsMasterServiceCate;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.utils.FsExecutorUtil;
import com.lmy.core.utils.UniqueNoUtil;
/**
 * 操作本地库 | 与微信的交换 @see WeiXinServiceImpl
 * @author fidel
 */
@Service
public class OrderServiceImpl {
	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsMasterServiceCateDao fsMasterServiceCateDao;
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired 
	private FsPayRecordDao fsPayRecordDao;
	@Autowired 
	private FsChatSessionDao fsChatSessionDao;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	/**
	 * 				JsonUtils.setBody(result, "order", orderBeanForInsert);<br>
					JsonUtils.setBody(result, "payRecord", payRecordForInsert);
	 */
	public JSONObject unifiedorderWeixin(long buyUsrId, final String buyUsrOpenId ,String registerMobile, final String buyUsrIp,  long masterInfoId , long masterServiceCateId){
		FsMasterServiceCate masterServiceCate = 	fsMasterServiceCateDao.findById(masterServiceCateId);
		FsMasterInfo masterInfo =  this.fsMasterInfoDao.findById(masterInfoId);
		if(masterServiceCate == null ||  masterServiceCate.getFsMasterInfoId() ==null ||masterInfo == null){
			logger.warn("buyUsrId="+buyUsrId+",masterInfoId="+masterInfoId+",masterServiceCateId="+masterServiceCateId+" 参数错误");
			return JsonUtils.commonJsonReturn("0001", "参数错误");
		}
		if(!masterServiceCate.getFsMasterInfoId().equals( masterInfo.getId() )){
			logger.warn("buyUsrId="+buyUsrId+",masterInfoId="+masterInfoId+",masterServiceCateId="+masterServiceCateId+" 参数错误");
			return JsonUtils.commonJsonReturn("0001", "参数错误");
		}
		
		if(!"ING".equals(masterInfo.getServiceStatus())){
			logger.warn("buyUsrId="+buyUsrId+",masterInfoId="+masterInfoId+",masterServiceCateId="+masterServiceCateId+" 不在服务状态 masterInfo.getServiceStatus():"+masterInfo.getServiceStatus());
			return JsonUtils.commonJsonReturn("0003", "不在服务状态");
		}
		if(masterInfo.getUsrId() == null || masterInfo.getUsrId().equals(buyUsrId)){
			logger.warn("buyUsrId="+buyUsrId+",masterInfoId="+masterInfoId+",masterUsrId="+masterInfo.getUsrId()+",自己买自己产品/服务");
			return JsonUtils.commonJsonReturn("0002", "不能刷单");
		}
		if( !"ON".equals( masterServiceCate.getStatus()) ){
			logger.warn("buyUsrId="+buyUsrId+",masterInfoId="+masterInfoId+",masterServiceCateId="+masterServiceCateId+" 不在服务状态  masterServiceCate.getStatus():"+ masterServiceCate.getStatus());
			return JsonUtils.commonJsonReturn("0003", "不在服务状态");
		}
		String chatSessionNo = UniqueNoUtil.getSimpleUniqueNo();
		//final List<FsChatSession> listChatSession = 	unifiedorderWeixin_buildFsChatSessionForInsert(buyUsrId, masterInfo.getUsrId() , chatSessionNo);
		final FsOrder orderBeanForInsert = unifiedorderWeixin_buildOrderBeanForInsert(buyUsrId, registerMobile,masterInfo, masterServiceCate,chatSessionNo);
		try{
			return 
			fsTransactionTemplate.execute(new TransactionCallback<JSONObject>() {
				@Override
				public JSONObject doInTransaction(TransactionStatus status) {
					//fsChatSessionDao.batchInsert(listChatSession);
					fsOrderDao.insert(orderBeanForInsert);
					final FsPayRecord  payRecordForInsert=   unifiedorderWeixin_buildPayRecordBeanForInsert(orderBeanForInsert,buyUsrOpenId,buyUsrIp) ;
					fsPayRecordDao.insert(payRecordForInsert);
					JSONObject result = JsonUtils.commonJsonReturn();
					JsonUtils.setBody(result, "order", orderBeanForInsert);
					JsonUtils.setBody(result, "payRecord", payRecordForInsert);
					return result;
				}
			});
		}catch(Exception e){
			logger.error("buyUsrId="+buyUsrId+",masterInfoId="+masterInfoId+",masterServiceCateId="+masterServiceCateId,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	private FsOrder unifiedorderWeixin_buildOrderBeanForInsert(long buyUsrId , String registerMobile,FsMasterInfo masterInfo , FsMasterServiceCate masterServiceCate ,String chatSessionNo ){
		Date now = new Date();
		FsOrder bean = new  FsOrder();
		//平台佣金调整为30% modify by fidel at  2017/08/03 15:50 
		Long settlementMasterServiceFee =  OrderAidUtil.mul(masterServiceCate.getAmt(), (1-OrderAidUtil.getPlatCommissionRate())).longValue();
		if(settlementMasterServiceFee.equals(0l)){
			settlementMasterServiceFee = masterServiceCate.getAmt();
		}
		bean.setBuyNum(1l).setBuyUsrId(buyUsrId).setDiscountRmbAmt(0l).setGoodsId(masterServiceCate.getId()).setGoodsName(masterServiceCate.getName())
		.setOrderNum(UniqueNoUtil.getSimpleUniqueNo()).setChatSessionNo(chatSessionNo);
		bean.setOrderType("zxfw").setPayConfirmTime(null).setPayRmbAmt(masterServiceCate.getAmt()).setPayType("weixinpay").setRefundRmbAmt(0l)
		.setSellerUsrId(masterInfo.getUsrId()).setStatus( "init") .setTransDesc(masterInfo.getName()+"的订单-"+masterServiceCate.getName()).setZxCateId(masterServiceCate.getFsZxCateId());
		bean.setSettlementMasterServiceFee(settlementMasterServiceFee) .setSettlementPlatServiceFee( masterServiceCate.getAmt() -settlementMasterServiceFee  );
		bean.setUpdateTime(now) .setCreateTime(now);
		//办公风水 住宅风水
		//办公风水和住宅风水无需填写什么信息，但需要将该用户的注册手机号带上保存下来 add by fidel at 2017/05/31 10:56
		if(masterServiceCate.getFsZxCateId().equals(100011l) || masterServiceCate.getFsZxCateId().equals(100012l)  ){
			if(StringUtils.isNotEmpty(registerMobile)){
				JSONArray orderExtraInfoList = new JSONArray();
				JSONObject orderExtraInfo = new JSONObject();
				orderExtraInfo.put("mobile", registerMobile);
				orderExtraInfoList.add(orderExtraInfo);
				bean.setOrderExtraInfo(orderExtraInfoList.toJSONString());
			}
		}
		return bean;
	}
	
	private  FsPayRecord unifiedorderWeixin_buildPayRecordBeanForInsert(FsOrder orderBeanAfterInsert , String buyUsrOpenId,String buyUsrIp){
		FsPayRecord beanForInsert = new FsPayRecord();
		beanForInsert.setAppId( ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId") ) ; 
		beanForInsert.setAttach(null) ; 
		beanForInsert.setMchId( ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.merId") )  ;
		beanForInsert.setNotifyUrl(  ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.service.basehost")  +  ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.pay.notify.relative.url") )  ;
		beanForInsert.setBody(orderBeanAfterInsert.getGoodsName()).setCreateTime(orderBeanAfterInsert.getCreateTime());
		beanForInsert.setDetail(orderBeanAfterInsert.getTransDesc()).setFeeType("CNY").setGoodsTag(null) ;
		beanForInsert.setOpenId(buyUsrOpenId).setOrderId(orderBeanAfterInsert.getId()).setOutTradeNo(UniqueNoUtil.getSimpleUniqueNo()  ) .setPayChannel("weixin")
		.setRefundFee(0l).setRemark(null).setSpbillCreateIp(buyUsrIp).setTotalFee(orderBeanAfterInsert.getPayRmbAmt()).setTradeStatus("ing")
		.setTradeType("unifiedorder").setUsrId(orderBeanAfterInsert.getBuyUsrId()).setUpdateTime(orderBeanAfterInsert.getCreateTime()).setCreateTime(  orderBeanAfterInsert.getCreateTime());
		return beanForInsert;
	}
	
	private List<FsChatSession> buildFsChatSessionForInsert(long buyUsrId,long masterUsrId ,String chatSessionNo , Date now  , Date ExpirtyDate){
		FsChatSession beanForInsert1 = new FsChatSession();
		beanForInsert1.setSessionNo(chatSessionNo).setUsrId(buyUsrId).setStatus("effect").setIsServiceProvider("N").setExpiryDate(ExpirtyDate)   .setCreateTime(now);
		
		FsChatSession beanForInsert2 = new FsChatSession();
		beanForInsert2.setSessionNo(chatSessionNo).setUsrId(masterUsrId).setStatus("effect").setIsServiceProvider("Y").setExpiryDate(ExpirtyDate).setCreateTime(now);
		List<FsChatSession> list = new ArrayList<FsChatSession>();
		list.add(beanForInsert1);
		list.add(beanForInsert2);
		return list;
	}
	
	public JSONObject supplyOrderZXUsrInfo(long orderId , String chatSessionNo,final long buyUsrId ,final JSONArray dataList){
		try{
			final FsOrder order =   this.fsOrderDao.findById(orderId);
			if( order == null ||  !order.getBuyUsrId().equals(buyUsrId ) && !order.getChatSessionNo().equals( chatSessionNo ) ){
				return JsonUtils.commonJsonReturn("0001","数据错误");
			}
			if(! OrderAidUtil.supplyOrderInfoParamsCheck(order.getZxCateId(), dataList) ){
				return JsonUtils.commonJsonReturn("0002","还有信息没有填写");
			}
			//作为第一条聊天记录  
			final FsOrder beanForUpdate = new FsOrder();
			beanForUpdate.setId(orderId);
			Date now = new Date();
			Date completedTime =   DateUtils.addDays(now, 1)  ;
			Date settlementTime =   DateUtils.addDays(completedTime, 7) ;
			//在订单完成后的第7个自然日22点从服务号转账给老师的微信钱包
			settlementTime = DateUtils.setHours(settlementTime, 22);
			settlementTime = DateUtils.setMinutes(settlementTime, 0);
			settlementTime = DateUtils.setSeconds(settlementTime, 0);
			settlementTime = DateUtils.setMilliseconds(settlementTime, 0);
			beanForUpdate.setOrderExtraInfo(dataList.toJSONString()) .setSettlementTime(settlementTime);
			beanForUpdate.setBuyUsrId(buyUsrId).setBeginChatTime(now).setEndChatTime(completedTime).setUpdateTime(now) .setCompletedTime(completedTime);
			FsUsr usrForUpdate=	this.fsTransactionTemplate.execute(new TransactionCallback<FsUsr>() {
				@Override
				public FsUsr doInTransaction(TransactionStatus status) {
					if(order.getSellerFirstReplyTime()!=null){
						logger.info("orderId:"+order.getId()+" 补充咨询信息,老师已经有过答复getSellerFirstReplyTime: "+CommonUtils.dateToString(order.getSellerFirstReplyTime(), CommonUtils.dateFormat2, "")+",不再更新 开始、结束会话时间,服务完成时间，与老师结算时间，只更新最近一次聊天时间");
						fsOrderDao.updateLastChatTime(order.getId());
					}
					fsOrderDao.update(beanForUpdate);				
					return   _infoUpdteToUsr(buyUsrId, dataList);
				}
			});
			JSONObject result =  JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "usrForUpdate", usrForUpdate);
			return result;
		}catch(Exception e){
			logger.error("orderId:"+orderId+",chatSessionNo:"+chatSessionNo, e);
			return JsonUtils.commonJsonReturn("0000","系统错误");
		}
	}
	
	private FsUsr _infoUpdteToUsr(long usrId,  JSONArray dataList){
		if(CollectionUtils.isEmpty(dataList)){
			return null;
		}
		boolean needUpdate = false;
		FsUsr usrForUpdate = new FsUsr();
		for(Object obj :  dataList){
			JSONObject dataOne = (JSONObject) obj;
			if( StringUtils.equals(  dataOne.getString("isSelf") , "Y") ){
				if(StringUtils.isNotEmpty(dataOne.getString("realName"))){
					usrForUpdate.setRealName(dataOne.getString("realName"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("englishName"))){
					usrForUpdate.setEnglishName(dataOne.getString("englishName"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("sex"))){
					usrForUpdate.setSex(dataOne.getString("sex"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("birthTimeType"))){
					usrForUpdate.setBirthTimeType(dataOne.getString("birthTimeType"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("birthTime"))){
					usrForUpdate.setBirthTime(dataOne.getString("birthTime"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("birthDate"))){
					Date birthDate = CommonUtils.stringToDate(dataOne.getString("birthDate"), CommonUtils.dateFormat1);
					Calendar cal = Calendar.getInstance();
					cal.setTime(birthDate);
					usrForUpdate.setBirthYear(cal.get(Calendar.YEAR));
					usrForUpdate.setBirthDate(CommonUtils.dateToString(birthDate, "MMdd", ""));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("birthAddress"))){
					usrForUpdate.setBirthAddress(dataOne.getString("birthAddress"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("familyRank"))){
					usrForUpdate.setFamilyRank(dataOne.getString("familyRank"));
					needUpdate = true;
				}
				if(StringUtils.isNotEmpty(dataOne.getString("marriageStatus"))){
					usrForUpdate.setMarriageStatus(dataOne.getString("marriageStatus"));
					needUpdate = true;
				}
			}
		}
		if(needUpdate){
			logger.info("buyUsrId:"+usrId+",提交订单补充资料,本人部分 回写 usr表");
			usrForUpdate.setId(usrId);
			usrForUpdate.setUpdateTime(new Date());
			this.fsUsrDao.update(usrForUpdate);
		}
		return usrForUpdate;
	}
	
	private void doAsynPutBeanstalkMsgAndWxNotifyForNewOrder(final FsOrder order ){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				_putAllMsgForNewOrder(order);
			}
		};
		FsExecutorUtil.getExecutor().execute(r);
	}
	private void _putAllMsgForNewOrder(FsOrder order ){
		try{
			logger.info("========prepare body for new order msgs========");
			// build beanstalk msg for 24 hours auto refund
			JSONObject beanstalkMsg24 = new JSONObject();
			beanstalkMsg24.put("orderId", order.getId());
			BeanstalkClient.put(QueueNameConstant.masterNoReply24HoursAutoRefund, null, 3600 * 24 + 1, null, beanstalkMsg24);
			
			// build beanstalk msg for 10 minutes user info check
			JSONObject beanstalkMsgInfo = new JSONObject();
			beanstalkMsgInfo.put("orderId", order.getId());
			beanstalkMsgInfo.put("msgType", QueueNameConstant.MSG_ORDER_INFO_CHECK);
			BeanstalkClient.put(QueueNameConstant.QUEUE_ORDER, null, 600 + 1, null, beanstalkMsgInfo);

			// build beanstalk msg for 10 minutes master begin check
			JSONObject beanstalkMsgBegin = new JSONObject();
			beanstalkMsgBegin.put("orderId", order.getId());
			beanstalkMsgBegin.put("msgType", QueueNameConstant.MSG_ORDER_BEGIN_CHECK);
			BeanstalkClient.put(QueueNameConstant.QUEUE_ORDER, null, 600 + 1, null, beanstalkMsgBegin);

			// immediately send wx msg to master
			Map<Long,FsUsr> usrIdMap =  this.fsUsrDao.findByUsrIdsAndConvert( Arrays.asList( order.getSellerUsrId(),order.getBuyUsrId()  )  );
			String sellerUsrOpenId = usrIdMap.get( order.getSellerUsrId() ) .getWxOpenId();
			String buyUsrName = UsrAidUtil.getNickName2(usrIdMap.get( order.getBuyUsrId() ), "匿名") ;
			wxNoticeManagerImpl.masterNewOrderWxMsg(order, sellerUsrOpenId, buyUsrName);
		}catch(Exception e){
			logger.error("orderId:"+order.getId(), e);
		}
	}
	
	public JSONObject zxOrderHandWeiXinNotify(String out_trade_no , final String bank_type ,final String transaction_id  , final boolean paySucc){
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
			final FsOrder order = this.fsOrderDao.findById( payRecord.getOrderId()  );
			if(order == null  || !"init".equals(order.getStatus())){
				logger.warn("out_trade_no:"+out_trade_no+", transaction_id:"+transaction_id+" ,bank_type:"+bank_type+", paySucc:"+paySucc +"  订单 未nul或状态错误 "
						+",payRecord.id="+payRecord.getId()+",payRecord.getTradeType="+payRecord.getTradeType()+",payRecord.getTradeStatus="+ payRecord.getTradeStatus()+",orderId="+(order!=null?order.getId():null)+",orderStatus:"+(order!=null?order.getStatus():null));
				return JsonUtils.commonJsonReturn("0002", "数据错误");				
			}
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					Date now = new Date();
					Date completedTime =   DateUtils.addDays(now, 1)  ;
					Date settlementTime =   DateUtils.addDays(completedTime, 7) ;
					//在订单完成后的第7个自然日22点为结算时间
					settlementTime = DateUtils.setHours(settlementTime, 22);
					settlementTime = DateUtils.setMinutes(settlementTime, 0);
					settlementTime = DateUtils.setSeconds(settlementTime, 0);
					settlementTime = DateUtils.setMilliseconds(settlementTime, 0);
					List<FsChatSession> listForInsert = buildFsChatSessionForInsert(order.getBuyUsrId(), order.getSellerUsrId(), order.getChatSessionNo() ,now ,completedTime);
					fsChatSessionDao.batchInsert(listForInsert);
					int effectOrderNum = fsOrderDao.updateForPayByResult1(order.getId(),     
						(paySucc ) ? OrderStatus.pay_succ.getStrValue() :  OrderStatus.pay_fail.getStrValue(),   
						now, completedTime, now, completedTime, settlementTime, now);
					Assert.isTrue(effectOrderNum ==1 );
					int effectPayNum = fsPayRecordDao.updateForPayByResult(payRecord.getId(),bank_type,transaction_id ,paySucc, now);
					Assert.isTrue(effectPayNum ==1 );
					return true;
				}
			});
			if(paySucc){
				doAsynPutBeanstalkMsgAndWxNotifyForNewOrder(order);	
			}
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("out_trade_no:"+out_trade_no+", transaction_id:"+transaction_id+" ,bank_type:"+bank_type+", paySucc:"+paySucc  ,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}

	public JSONObject handWeiXinRefundConfirm(final FsPayRecord payRecord ,final boolean refundSucc,final String respTradeNo, final String respCode, final String respMsg ,final String remark){
		try{
			final FsOrder order = fsOrderDao.findById(payRecord.getOrderId());
			if(!OrderStatus.refunding.getStrValue() .equals(order.getStatus())){
				return JsonUtils.commonJsonReturn("9999","状态错误");
			}
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				Date now = new Date();
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					int effectNum = fsOrderDao.updateForAfterRefundResult(payRecord.getOrderId(), refundSucc,remark,now);
					int effectNum2 = fsPayRecordDao.updateForPayByResult(payRecord.getId(), respTradeNo, null, respCode, respMsg, remark, refundSucc, now);
					Assert.isTrue( effectNum2 == 1 && effectNum == 1);
					return null;
				}
			});
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("payRecord.id:"+payRecord.getId(),e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
}
