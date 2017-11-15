package com.lmy.core.service.impl;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.common.redis.RedisClient;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.beanstalkd.job.QueueNameConstant;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderSettlementDao;
import com.lmy.core.dao.FsOrderSettlementRelaDao;
import com.lmy.core.dao.FsPayRecordDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsOrderSettlement;
import com.lmy.core.model.FsOrderSettlementRela;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.utils.FsEnvUtil;
import com.lmy.core.utils.UniqueNoUtil;
/**
 * @author fidel
 * @since 2017/07/12
 */
@Service
public class OrderSettlementServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(OrderSettlementServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsPayRecordDao fsPayRecordDao;
	@Autowired
	private FsOrderSettlementDao fsOrderSettlementDao;
	@Autowired
	private FsOrderSettlementRelaDao fsOrderSettlementRelaDao;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	

	/**
	 * @see 2017/06/05 19:03 防止 部分订单到点没有执行 pay_succ -->completed , 这里进行补救一次
	 */
	public void autoPaySuccToCompleted(Date now){
		String redisLockSuffixKey = this.getClass()+"#"+Thread.currentThread().getStackTrace()[1].getMethodName();
		try{
			boolean hadGetlock = getLock(redisLockSuffixKey, RedisClient.oneHourSec *3);
			if(!hadGetlock){
				logger.info("获取所失败,本次操作 忽略");
				return ;
			}
			int perPageNum = 1;
			List<Long> waittoCompletedOrderIds = null;
			boolean loopFlag = true;
			Long gtId = null;
			while( loopFlag ){
				waittoCompletedOrderIds =this.fsOrderDao.findOrderIdsForCompleted(gtId, 0, perPageNum);
				_autoPaySuccToCompletedOne(waittoCompletedOrderIds);
				if(CollectionUtils.isEmpty(waittoCompletedOrderIds) || waittoCompletedOrderIds.size() < perPageNum ){
					loopFlag = false;
				}else{
					gtId = waittoCompletedOrderIds.get( waittoCompletedOrderIds.size()-1 );
				}
			}
		}catch(Exception e){
			logger.error("autoPaySuccToCompleted 系统错误 now:{}", now);
			logger.error("autoPaySuccToCompleted 系统错误",e);
		}finally{
			RedisClient.delete(CacheConstant.AUTO_JOB +"_" +redisLockSuffixKey);
		}
	}
	
	private void _autoPaySuccToCompletedOne(List<Long> waittoCompletedOrderIds ){
		if(CollectionUtils.isEmpty(waittoCompletedOrderIds)){
			return ;
		}
		for(Long orderId  : waittoCompletedOrderIds) {
			JSONObject data = new JSONObject();
			data.put("orderId", orderId);			
			BeanstalkClient.put(QueueNameConstant.masterNoReply24HoursAutoRefund, null, 5, 2, data);
		}
	}
	
	public void autoSettlement(Date now){
		String redisLockSuffixKey = this.getClass()+"#"+Thread.currentThread().getStackTrace()[1].getMethodName();
		try{
			boolean hadGetlock = getLock(redisLockSuffixKey, RedisClient.oneHourSec *3);
			if(!hadGetlock){
				if(FsEnvUtil.isDev()){
					RedisClient.delete(CacheConstant.AUTO_JOB +"_" +redisLockSuffixKey);
				}
				logger.info("获取所失败,本次操作 忽略");
				return ;
			}
			Date settlementCycleBeginTime = getSettlementCycleBeginTime(now);
			Date settlementCycleEndTime = getSettlementCycleEndTime(now);
			logger.info("开始结算 settlementCycleBeginTime:"+CommonUtils.dateToString(settlementCycleBeginTime, CommonUtils.dateFormat2, "")+"  ,settlementCycleEndTime"+CommonUtils.dateToString(settlementCycleEndTime, CommonUtils.dateFormat2, ""));
			List<Long> waitSettleSellerUsrIds = this.fsOrderDao.findSellerUsrIdsByForSettlement(settlementCycleBeginTime, settlementCycleEndTime);
			if(CollectionUtils.isEmpty(waitSettleSellerUsrIds)){
				logger.info("结算结束 ， 本月没有待结算订单");
				return ;
			}
			for(Long waitSettleSellerUsrId :   waitSettleSellerUsrIds){
				settlementOne(waitSettleSellerUsrId, settlementCycleBeginTime, settlementCycleEndTime);
			}
//			RedisClient.delete(CacheConstant.AUTO_JOB +"_" +redisLockSuffixKey);
		}catch(Exception e){
			logger.error("autoPaySuccToCompleted 系统错误 now:{}", now);
			logger.error("autoPaySuccToCompleted 系统错误",e);
		}
	}
	@SuppressWarnings("unchecked")
	public void settlementOne(final Long waitSettleSellerUsrId ,  final Date settlementCycleBeginTime , final Date settlementCycleEndTime){
		JSONObject wxTransResult = null;
		try{
			logger.info("开始结算 waitSettleSellerUsrId:"+waitSettleSellerUsrId+",settlementCycleBeginTime:"+CommonUtils.dateToString(settlementCycleBeginTime, CommonUtils.dateFormat2, "")+"  ,settlementCycleEndTime"+CommonUtils.dateToString(settlementCycleEndTime, CommonUtils.dateFormat2, ""));
			List<FsOrder> waitSettleOrderList =   this.fsOrderDao.findShortOrderInfoForSettlement(waitSettleSellerUsrId, settlementCycleBeginTime, settlementCycleEndTime);
			if(CollectionUtils.isEmpty(waitSettleOrderList)){
				logger.info("没有待结算的订单waitSettleSellerUsrId:"+waitSettleSellerUsrId+",settlementCycleBeginTime:"+CommonUtils.dateToString(settlementCycleBeginTime, CommonUtils.dateFormat2, "")+"  ,settlementCycleEndTime"+CommonUtils.dateToString(settlementCycleEndTime, CommonUtils.dateFormat2, ""));
				return ;
			}
			Date now = new Date();
			FsUsr sellerUsr = this.fsUsrDao.findById(waitSettleSellerUsrId);
			int orderTotalNum = waitSettleOrderList.size();
			JSONObject analysisListResult = _analysisList(waitSettleOrderList);
			//订单总金额 C端用户支付总金额
			Long orderTotalPayRmbAmt = (Long)JsonUtils.getBodyValue(analysisListResult, "orderTotalPayRmbAmt");
			List<Long> ids =(List<Long>) JsonUtils.getBodyValue(analysisListResult, "ids");
			// 平台佣金 单位分
			Long platCommissionRmbAmt = OrderAidUtil.mul(orderTotalPayRmbAmt, OrderAidUtil.getPlatCommissionRate()).longValue();
			//税前总收入
			Long beforeTaxIncomeRmbAmt = orderTotalPayRmbAmt - platCommissionRmbAmt;
			//应出个税 单位分
			Long personalIncomeTaxRmbAmt = OrderAidUtil.calPersonalIncomeTaxRmbAmt(beforeTaxIncomeRmbAmt);
			//实际到(转)账金额 单位分
			long realArrivalAmt =  beforeTaxIncomeRmbAmt - personalIncomeTaxRmbAmt ;
			FsOrderSettlement orderSettleForInsert = new FsOrderSettlement();
			orderSettleForInsert.setCreateTime(now);
			orderSettleForInsert.setOrderTotalNum(Long.valueOf(orderTotalNum)).setOrderTotalPayRmbAmt(orderTotalPayRmbAmt).setPersonalIncomeTaxRmbAmt(personalIncomeTaxRmbAmt).setPlatCommissionRmbAmt(platCommissionRmbAmt)
			.setRealArrivalAmt(realArrivalAmt);
			orderSettleForInsert.setSellerOpenId(sellerUsr.getWxOpenId()).setSellerUsrId(waitSettleSellerUsrId);
			orderSettleForInsert.setSettlementCycle( CommonUtils.dateToString(settlementCycleBeginTime, "yyyyMM", "") ).setSettlementCycleBeginTime(settlementCycleBeginTime).setSettlementCycleEndTime(settlementCycleEndTime).setSettlementCycleUnit("month");
			orderSettleForInsert.setStatus("ing").setUpdateTime(now);
			FsPayRecord beanForInsert = buildTransfers1(sellerUsr, now, realArrivalAmt);
			settlementPersistenceDbBeforCallWeiXin(orderSettleForInsert, beanForInsert, waitSettleSellerUsrId, ids);
			//发起微信退款交易  method 不会抛异常
			wxTransResult = WeiXinInterServiceImpl.transfers1(beanForInsert);	
			//处理call 微信 结果
			handWxTransfers1(waitSettleSellerUsrId, ids, orderSettleForInsert.getId(),beanForInsert.getId(), beanForInsert.getOutTradeNo(),wxTransResult);
		}catch(Exception e){
			logger.error("结算错误waitSettleSellerUsrId:"+ waitSettleSellerUsrId, e);
		}
	}
	
	/** method never throw Exception **/
	private void handWxTransfers1(final Long waitSettleSellerUsrId,List<Long> orderIds, final Long orderSettleId , long payReCordId,String wxOutTradeNo,JSONObject wxTransResult){
		//0000 succ; 0001 fail;1000 需确认 ; key respMsg
		String remark = null;
		if(!FsEnvUtil.isPro() && !JsonUtils.equalDefSuccCode(wxTransResult) ){
			//logger.info("结算 模拟成功，当前环境:{} 非生产环境, 微信转账接口非成功状态,{} payReCordId:{}",FsEnvUtil.getEnv(), wxTransResult,payReCordId);
			//remark = "挡板程序模拟成功";
			//JsonUtils.setHead(wxTransResult, "0000", "succ");
		}
		if(JsonUtils.equalDefSuccCode(wxTransResult)){
			_handWxTransfers1(waitSettleSellerUsrId,orderIds,orderSettleId, true,payReCordId, wxOutTradeNo, (String)JsonUtils.getBodyValue(wxTransResult, "respMsg") , remark);
		}
		else if(JsonUtils.codeEqual(wxTransResult, "0001")){
			_handWxTransfers1(waitSettleSellerUsrId,orderIds,orderSettleId,false,payReCordId, wxOutTradeNo,(String)JsonUtils.getBodyValue(wxTransResult, "respMsg") , remark);
		}
		else if(JsonUtils.codeEqual(wxTransResult, "1000")){
			//TODO 自动确认, 自动重新 转账 压队列
		}
	}
	
	public JSONObject settlementOneAgain( long waitSettleSellerUsrId ,  long orderSettleId ){
		FsOrderSettlement orderSelltement = this.fsOrderSettlementDao.findById(orderSettleId);
		if(orderSelltement == null){
			logger.warn("orderSettleId:"+orderSettleId+",查无数据");
			return JsonUtils.commonJsonReturn("0001", "查无数据");
		}
		if( StringUtils.equals("succ", orderSelltement.getStatus()) ){
			logger.info("orderSettleId:"+orderSettleId+",status:"+orderSelltement.getStatus()+",不满足再一次条件");
			return JsonUtils.commonJsonReturn("0002", "不满足再一次条件");
		}
		else if( StringUtils.equals("fail", orderSelltement.getStatus()) ){
			logger.info("orderSettleId:"+orderSettleId+",status:"+orderSelltement.getStatus()+",不满足再一次条件");
			_settlementOneAgain_fail(waitSettleSellerUsrId, orderSettleId);
			return JsonUtils.commonJsonReturn("0002", "不满足再一次条件");
		}
		else if( StringUtils.equals("ing", orderSelltement.getStatus()) ){
			logger.info("orderSettleId:"+orderSettleId+",status:"+orderSelltement.getStatus()+",不满足再一次条件-->发起确认流程");
			_settlementOneAgain_ing(waitSettleSellerUsrId, orderSettleId, orderSelltement.getWxOutTradeNo());
			return JsonUtils.commonJsonReturn("0002", "不满足再一次条件-->发起确认流程");
		}
		else{
			logger.info("orderSettleId:"+orderSettleId+",status:"+orderSelltement.getStatus()+",不满足再一次条件,进入未知分支");
			return JsonUtils.commonJsonReturn("0002", "不满足再一次条件");
		}
	}
	
	private void _settlementOneAgain_fail(long waitSettleSellerUsrId ,  long orderSettleId){
		JSONObject wxTransResult = null;
		try{
			List<Long> orderIds = fsOrderSettlementRelaDao.findOrderIds1(orderSettleId, waitSettleSellerUsrId);
			List<FsOrder>	waitSettleOrderList = this.fsOrderDao.findByOrderIds(orderIds);
			if(CollectionUtils.isEmpty(orderIds)){
				logger.warn("orderSettleId:"+orderSettleId+",waitSettleSellerUsrId:"+waitSettleSellerUsrId+" -->orderIds is empty");
				return ;
			}
			FsUsr sellerUsr = this.fsUsrDao.findById(waitSettleSellerUsrId);
			JSONObject analysisListResult = _analysisList(waitSettleOrderList);
			//订单总金额 C端用户支付总金额
			Long orderTotalPayRmbAmt = (Long)JsonUtils.getBodyValue(analysisListResult, "orderTotalPayRmbAmt");
			// 平台佣金 单位分
			Long platCommissionRmbAmt = OrderAidUtil.mul(orderTotalPayRmbAmt, OrderAidUtil.getPlatCommissionRate()).longValue();
			//税前总收入
			Long beforeTaxIncomeRmbAmt = orderTotalPayRmbAmt - platCommissionRmbAmt;
			//应出个税 单位分
			Long personalIncomeTaxRmbAmt = OrderAidUtil.calPersonalIncomeTaxRmbAmt(beforeTaxIncomeRmbAmt);
			//实际到(转)账金额 单位分
			long realArrivalAmt =  beforeTaxIncomeRmbAmt - personalIncomeTaxRmbAmt ;
			final FsPayRecord payRecordForInsert = this.buildTransfers1(sellerUsr, new Date(), realArrivalAmt);			
			payRecordForInsert.setOrderId(orderSettleId);
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					fsPayRecordDao.insert(payRecordForInsert);
					return true;
				}
			});
			wxTransResult = WeiXinInterServiceImpl.transfers1(payRecordForInsert);	
			if(!FsEnvUtil.isPro() && !JsonUtils.equalDefSuccCode(wxTransResult) ){
				logger.info("结算 模拟成功，当前环境:{} 非生产环境, 微信转账接口非成功状态,{} payReCordId:{}",FsEnvUtil.getEnv(), wxTransResult,payRecordForInsert.getId());
				JsonUtils.setHead(wxTransResult, "0000", "succ");
			}
			//处理call 微信 结果
			handWxTransfers1(waitSettleSellerUsrId, orderIds, orderSettleId,payRecordForInsert.getId(), payRecordForInsert.getOutTradeNo(),wxTransResult);
		}catch(Exception e){
			logger.warn("orderSettleId:"+orderSettleId+",waitSettleSellerUsrId:"+waitSettleSellerUsrId,e);
			return ;
		}
		return ;
	}
	
	
	private void _settlementOneAgain_ing( long waitSettleSellerUsrId ,  long orderSettleId , String wxOutTradeNo){
		List<Long> orderIds = fsOrderSettlementRelaDao.findOrderIds1(orderSettleId, waitSettleSellerUsrId);
		if(CollectionUtils.isEmpty(orderIds)){
			logger.warn("orderSettleId:"+orderSettleId+",waitSettleSellerUsrId:"+waitSettleSellerUsrId+" -->orderIds is empty");
			return ;
		}
		FsPayRecord payRecord = this.fsPayRecordDao.findByOutTradeNo(wxOutTradeNo);
		if(payRecord == null){
			logger.warn("orderSettleId:"+orderSettleId+",waitSettleSellerUsrId:"+waitSettleSellerUsrId+" -->payRecord is empty");
			return ;
		}
		if(!StringUtils.equals("ing", payRecord.getTradeStatus())){
			logger.warn("orderSettleId:"+orderSettleId+",waitSettleSellerUsrId:"+waitSettleSellerUsrId+" -->payRecord.getTradeStatus:"+payRecord.getTradeStatus()+" not ing");
			return ;
		}
		autoConfirmOrderSettle(waitSettleSellerUsrId, orderIds, orderSettleId, payRecord.getId(), wxOutTradeNo);
	}
	
	public JSONObject autoConfirmOrderSettle(Long waitSettleSellerUsrId ,  List<Long> orderIds ,   Long orderSettleId ,  Long payReCordId,String wxOutTradeNo){
		try{
			FsOrderSettlement orderSelltement = this.fsOrderSettlementDao.findById(orderSettleId);
			if(orderSelltement == null){
				logger.warn("orderSettleId:"+orderSettleId+",查无数据");
				return JsonUtils.commonJsonReturn("0001", "查无数据");
			}
			if( !StringUtils.equals("ing", orderSelltement.getStatus()) ){
				logger.info("orderSettleId:"+orderSettleId+",status:"+orderSelltement.getStatus()+",终态 无需确认");
				return JsonUtils.commonJsonReturn("0002", "终态 无需确认");
			}
			JSONObject transResult = WeiXinInterServiceImpl.gettransferinfo(wxOutTradeNo);
			//明确转账失败
			if(JsonUtils.codeEqual(transResult, "0001") ){
				_handWxTransfers1(waitSettleSellerUsrId,orderIds,orderSettleId, false,payReCordId, wxOutTradeNo, null,"后台转账查询确认");
			}
			//明确转账成功
			else if(JsonUtils.codeEqual(transResult, "0000") ){
				_handWxTransfers1(waitSettleSellerUsrId,orderIds,orderSettleId, true,payReCordId, wxOutTradeNo, null,"后台转账查询确认");
			}
			else if(JsonUtils.codeEqual(transResult, "1000") ){
				//TODO 自动确认, 自动重新 转账 压队列
			}else{
				logger.warn("orderSettleId:"+orderSettleId+",进入未知分支 transResult:"+transResult);
			}
			return transResult;
		}catch(Exception e){
			logger.error("orderSettleId:"+orderSettleId+",进入未知分支 wxOutTradeNo:"+wxOutTradeNo,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	/** method never throw Exception **/
	private void _handWxTransfers1(final Long waitSettleSellerUsrId,final List<Long> orderIds, final Long orderSettleId, final boolean isSucc,final long payReCordId,final String wxOutTradeNo,  final String wxRespMsg,final String remark){
		try{
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				Date now = new Date();
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					int effetcNum = fsOrderDao.updateForSettlementAfterCallWeiXin(isSucc? OrderStatus.settlemented.getStrValue(): OrderStatus.settlement_fail.getStrValue(), waitSettleSellerUsrId, orderIds);
					int effetcNum2 = fsPayRecordDao.updateForPayByResult(payReCordId, null, null, null, wxRespMsg, remark, isSucc, now);
					Assert.isTrue( effetcNum >=0 && effetcNum2 ==1 );
					FsOrderSettlement orderSettleBeanForUpdate = new FsOrderSettlement();
					orderSettleBeanForUpdate.setConfrimTime(now);
					orderSettleBeanForUpdate.setId(orderSettleId);
					orderSettleBeanForUpdate.setUpdateTime(now);
					orderSettleBeanForUpdate.setStatus(isSucc?"succ":"fail");
					orderSettleBeanForUpdate.setRemark(wxRespMsg);
					orderSettleBeanForUpdate.setWxOutTradeNo(wxOutTradeNo); 
					fsOrderSettlementDao.update(orderSettleBeanForUpdate);
					return  true ;
				}
			});
		}catch(Exception e){
			logger.error("payReCordId:{},微信转账结果:{},微信响应:{},remark:{}",payReCordId, isSucc,wxRespMsg,remark);
			logger.error("拿到微信转账结果系统处理异常", e);
		}
	}
	private void settlementPersistenceDbBeforCallWeiXin(final FsOrderSettlement orderSettleForInsert ,final FsPayRecord beanForInsert, final long sellerUsrId,  final List<Long> orderIds){
		this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				orderSettleForInsert.setWxOutTradeNo(beanForInsert.getOutTradeNo());
				long orderSettlementId = fsOrderSettlementDao.insert(orderSettleForInsert);
				beanForInsert.setOrderId(orderSettlementId);
				fsPayRecordDao.insert(beanForInsert);
				int effectNum = fsOrderDao.updateForSettlementBeforCallWeiXin(sellerUsrId, orderIds);
				Assert.isTrue( effectNum == orderSettleForInsert.getOrderTotalNum().intValue() );
				List<FsOrderSettlementRela> list = _buildFsOrderSettlementRelaList(orderSettlementId, sellerUsrId,orderIds);
				fsOrderSettlementRelaDao.batchInsert(list);
				return true;
			}
		});
	}
	private  List<FsOrderSettlementRela> _buildFsOrderSettlementRelaList(Long orderSettlementId, Long sellerUsrId, List<Long> orderIds){
		List<FsOrderSettlementRela> list = new ArrayList<FsOrderSettlementRela>();
		Date now = new Date();
		for(Long orderId : orderIds){
			FsOrderSettlementRela bean = new FsOrderSettlementRela();
			bean.setCreateTime(now);
			bean.setSellerUsrId(sellerUsrId);
			bean.setOrderId(orderId);
			bean.setOrderSettlementId(orderSettlementId);
			list.add(bean);
		}
		return list;
	}
	
	private FsPayRecord buildTransfers1(FsUsr sellerUsr, Date now,long realArrivalAmt) throws UnknownHostException{
		FsPayRecord beanForInsert =new  FsPayRecord();
		beanForInsert.setAppId( ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId") ) ; 
		beanForInsert.setAttach(null).setMchId( ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.merId") )  ;
		beanForInsert.setBody("月结");
		beanForInsert.setDetail("月结");
		beanForInsert.setOpenId(sellerUsr.getWxOpenId()).setCheckName("NO_CHECK").setUpdateTime(now)  .setCreateTime(now);
		beanForInsert.setFeeType("CNY").setTotalFee(realArrivalAmt). setOrderId(null).setUsrId( sellerUsr.getId() )  .setOutTradeNo( UniqueNoUtil.getSimpleUniqueNo()   ).setPayChannel("weixin").setRefundFee(0l)
		.setReUserName(null).setTradeConfirmTime(null).setTradeDesc("月结").setTradeStatus("ing").setTradeType("transfers").setSpbillCreateIp(InetAddress.getLocalHost().getHostAddress());
		return beanForInsert;
	}
	
	

	
	private JSONObject _analysisList(List<FsOrder> waitSettleOrderList ){
		long d = 0;
		List<Long> ids = new ArrayList<Long>();
		for(FsOrder order : waitSettleOrderList){
			d = order.getPayRmbAmt() +d;
			ids.add(order.getId());
		}
		JSONObject result = JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "orderTotalPayRmbAmt", d);
		JsonUtils.setBody(result, "ids", ids);
		return result;
	}
	
	
	public Date getSettlementCycleBeginTime(Date now){
		if(now == null){
			now = new Date();
		}
		Calendar cal  = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 1);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
	public  Date getSettlementCycleEndTime(Date now){
		if(now == null){
			now = new Date();
		}
		Calendar cal  = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.DAY_OF_MONTH, 7);
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
/*	private void _autoPaySuccToCompletedOne(List<Long> waittoCompletedOrderIds ){
		if(CollectionUtils.isEmpty(waittoCompletedOrderIds)){
			return ;
		}
		for(Long orderId  : waittoCompletedOrderIds) {
			JSONObject data = new JSONObject();
			data.put("orderId", orderId);			
			BeanstalkClient.put(QueueNameConstant.masterNoReply24HoursAutoRefund, null, 5, 2, data);
		}
	}*/
	
	
	
	 /**
	  * 获取锁   分布式 执行多长问题 2016/12/12 add by zhouzhaohua 
	  * @param methd
	  * @param timeoutSec
	  * @return
	  */
	 private boolean getLock(String classNameAndMethodName , int timeoutSec){
		 redis.clients.jedis.Jedis jedis = null;
		 redis.clients.jedis.Transaction trans = null;
		 String key = null;
		 try{
			 Assert.isTrue(  StringUtils.isNotEmpty( classNameAndMethodName));
			 jedis = RedisClient.getJedis();
			 key = CacheConstant.AUTO_JOB +"_" + classNameAndMethodName;
			boolean keyExists = jedis.exists(key) ;
			if( ! keyExists  ){
					jedis.watch(key);// make sure below operation is particle
					trans = jedis.multi();
					trans.set(key, key+"_"+ CommonUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss:SSS", ""));
					trans.expire(key, timeoutSec);
					List<Object> list = trans.exec();
					trans = null;
					Assert.isTrue( list !=null);
					return true;
			}else{
				 	logger.info("获取锁失败,key="+key+"已经存在,其他线程已在跑,本次丢弃value="+jedis.get(key)+"  classNameAndMethodName="+classNameAndMethodName+",timeoutSec="+timeoutSec);
					return false;
			}
		 }catch(Exception e){
			 logger.error("method="+classNameAndMethodName+",timeoutSec="+timeoutSec,e);
			 return false;
		 }finally{
			 RedisClient.closeJedis(jedis);
		 }
	 }
}
