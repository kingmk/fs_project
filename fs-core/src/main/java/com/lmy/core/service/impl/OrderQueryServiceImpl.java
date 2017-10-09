package com.lmy.core.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.dao.FsChatSessionDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderSettlementDao;
import com.lmy.core.dao.FsOrderSettlementRelaDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsOrderSettlement;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.dto.FsChatRecordDto;
import com.lmy.core.model.enums.OrderStatus;
@Service
public class OrderQueryServiceImpl {
	private static final Logger logger = Logger.getLogger(OrderQueryServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsChatSessionDao fsChatSessionDao;
	@Autowired
	private FsMasterInfoDao  fsMasterInfoDao;
	@Autowired 
	private FsChatRecordDao fsChatRecordDao;
	@Autowired
	private FsOrderSettlementDao fsOrderSettlementDao;
	@Autowired
	private FsOrderSettlementRelaDao fsOrderSettlementRelaDao;
	public FsOrder findByOrderIdAndBuyUsrId(long orderId , long buyUsrId){
		FsOrder bean = fsOrderDao.findById(orderId);
		if(bean !=null && bean.getBuyUsrId().equals(buyUsrId)){
			return bean;
		}else{
			return null;
		}
	}
	/**
	 * @param buyUsrId
	 * @param currentPage   当前页 从0开始
	 * @param perPageNum  每页显示条数
	 * @param orderBy          排序方式 0  最近聊天时间 ; def is 0
	 * @param statusList
	 * @return
	 */
	public JSONObject findCommonUsrOrderList(long buyUsrId , int currentPage,int perPageNum,int orderBy , List<String> statusList){
		if(CollectionUtils.isEmpty(statusList)){
			statusList =OrderAidUtil.getCommAllOrderStatus();
		}
		List<FsOrder> orderList = this.fsOrderDao.findOrder1(null,buyUsrId, null, currentPage, perPageNum, orderBy, statusList);
		if(CollectionUtils.isEmpty(orderList)){
			return JsonUtils.commonJsonReturn("1000", "查无数据");
		}
		Date now  =new Date();
		JSONObject groupResult =  getGroupList(orderList, now);
		List<Long> masterUsrIdList =  (List<Long>) groupResult.get("masterUsrIdList");
		List<String> waitQueryUnReadNumChatSessionNoList = (List<String>) groupResult.get("waitQueryUnReadNumChatSessionNoList");
		Map<String,Long>  chatSessionUnReadNumMap  =  this.fsChatRecordDao.statUnReadNum2(waitQueryUnReadNumChatSessionNoList, buyUsrId);
		Map<Long,FsMasterInfo> masterInfoIdMap =  findMasterInfoListAndConvert(masterUsrIdList);
		JSONArray dataList = new JSONArray();
		for(FsOrder bean :  orderList){
			JSONObject dataOne = new JSONObject(true);
			FsMasterInfo masterInfo = masterInfoIdMap.get( bean.getSellerUsrId() ) ;
			dataOne.put("masterNickName",  UsrAidUtil.getMasterNickName(masterInfo, null, ""));
			dataOne.put("masterHeadImgUrl",  	UsrAidUtil.getMasterHeadImg(masterInfo, null, "") );
			dataOne.put("goodsName", bean.getGoodsName());
			dataOne.put("payRmbAmt", bean.getPayRmbAmt()); //单位分
			dataOne.put("payRmbAmtDesc", CommonUtils.numberFormat(bean.getPayRmbAmt()/100d, "###,##0.00", "0.0")); //单位元
			dataOne.put("lastChatTime",  CommonUtils.dateToString(bean.getLastChatTime() ,CommonUtils.dateFormat2,"")  );
			dataOne.put("lastChatTimeStr",  OrderAidUtil.buildLastChatTimeStr(now, bean.getLastChatTime()) );  
			FsChatRecordDto  lastReplyInfo = this.fsChatRecordDao.findUsrReceLastReply(bean.getChatSessionNo(), null);
			if(lastReplyInfo !=null &&  "text".equals(lastReplyInfo.getMsgType()) ){
				lastReplyInfo.setContent( StringEscapeUtils.escapeHtml4(lastReplyInfo.getContent())  )	;
			}
			dataOne.put("lastReplyInfo",  lastReplyInfo ); 
			dataOne.put("lastReplyIsSelf",   (lastReplyInfo!=null && lastReplyInfo.getSentUsrId().equals( buyUsrId)) ?"Y":"N" ); //最后一句聊天是否为自己 add at 2017/05/31 12:57 by fidel 
			dataOne.put("unReadNum",  chatSessionUnReadNumMap.get( bean.getChatSessionNo() )  !=null ? chatSessionUnReadNumMap.get( bean.getChatSessionNo() ) : 0 ); 
			dataOne.put("createTime",  bean.getCreateTime() );
			dataOne.put("orderId",  bean.getId() );
			dataOne.put("chatSessionNo",  bean.getChatSessionNo() );
			dataOne.put("status",  bean.getStatus() );
			dataOne.put("isWaitSupplyOrderInfo",  (OrderAidUtil.getNeedSupplyOrderInfoZxCateIds().contains( bean.getZxCateId() ) 
																			&& OrderStatus.pay_succ.equals(bean.getStatus())  
																			&& bean.getOrderExtraInfo() == null )
																			? 'Y':'N' );  // 是否等待提交订单补充信息
			boolean isServiceEnd = now.after( bean.getEndChatTime() ) ;
			boolean isCanEvaluate = ( bean.getEvaluateTime() == null && OrderAidUtil.getCanEvaluateStatus().contains(  bean.getStatus()) ); 
			boolean hadEvaluate = bean.getEvaluateTime() !=null ;
			dataOne.put("isCanEvaluate", isCanEvaluate ? "Y":"N");  //是否可评价
			dataOne.put("isServiceEnd", isServiceEnd ? "Y":"N");  //聊天服务是否已过截止时间
			dataOne.put("hadRefund",  (bean.getRefundApplyTime()!=null  ) ? "Y":"N");  //是否发起过投诉
			dataOne.put("hadEvaluate", hadEvaluate ? "Y":"N");  //是否已评价
			dataOne.put("isWaitMasterService", ( OrderStatus.pay_succ.getStrValue().equals(bean.getStatus() )  && bean.getSellerFirstReplyTime()==null ) ? 'Y' :'N' );//是否等待老师服务
			dataList.add(dataOne);
		}
		JSONObject result = JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "data", dataList);
		return result;
	}
	/**
	 * @param sellerUsrId
	 * @param currentPage   当前页 从0开始
	 * @param perPageNum  每页显示条数
	 * @param orderBy          排序方式 0 最近聊天时间 ; def is 0
	 * @param statusList
	 * @return
	 */
	public JSONObject findMasterUsrOrderList(long sellerUsrId , int currentPage,int perPageNum,int orderBy , List<String> statusList){
		if(CollectionUtils.isEmpty(statusList)){
			statusList =OrderAidUtil.getMasterLeiJitatus();
		}
		List<FsOrder> orderList = this.fsOrderDao.findOrder1(null,null, sellerUsrId, currentPage, perPageNum, orderBy, statusList);
		if(CollectionUtils.isEmpty(orderList)){
			return JsonUtils.commonJsonReturn("1000", "查无数据");
		}
		Date now  =new Date();
		JSONObject groupResult =  getGroupList(orderList, now);
		List<Long> buyUsrIdList =  (List<Long>) groupResult.get("buyUsrIdList");
		List<String> waitQueryUnReadNumChatSessionNoList = (List<String>) groupResult.get("waitQueryUnReadNumChatSessionNoList");
		Map<String,Long>  chatSessionUnReadNumMap  =  this.fsChatRecordDao.statUnReadNum2(waitQueryUnReadNumChatSessionNoList, sellerUsrId);
		Map<Long, FsUsr> usrIdUsrMap = 	this.fsUsrDao.findShortInfo1ByUsrIds(buyUsrIdList);
		Map<Long,JSONObject> usrIdCacheWxUsrInfoMap = new HashMap<Long,JSONObject>();
		JSONArray dataList = new JSONArray();
		FsUsr _usr = null;
		for(FsOrder bean :  orderList){
			JSONObject dataOne = new JSONObject(true);
			_usr = usrIdUsrMap.get(  bean.getBuyUsrId() ) ;
			dataOne.put("buyUsrNickName",    _getBuyUsrNickName(_usr, usrIdCacheWxUsrInfoMap) );
			dataOne.put("buyUsrHeadImgUrl",   _getBuyUsrImgUrl(_usr, usrIdCacheWxUsrInfoMap));		
			dataOne.put("goodsName", bean.getGoodsName());
			dataOne.put("payRmbAmt", bean.getPayRmbAmt()); //单位分
			dataOne.put("payRmbAmt", bean.getPayRmbAmt()); //单位分
			dataOne.put("payRmbAmtDesc", CommonUtils.numberFormat(bean.getPayRmbAmt()/100d, "###,##0.00", "0.0")); //单位分
			dataOne.put("lastChatTime",  CommonUtils.dateToString(bean.getLastChatTime() ,CommonUtils.dateFormat2,"")  );
			dataOne.put("lastChatTimeStr",  OrderAidUtil.buildLastChatTimeStr(now, bean.getLastChatTime()) );  
			FsChatRecordDto  lastReplyInfo = this.fsChatRecordDao.findUsrReceLastReply(bean.getChatSessionNo(), null) ; 
			if(lastReplyInfo !=null &&  "text".equals(lastReplyInfo.getMsgType()) ){
				lastReplyInfo.setContent( StringEscapeUtils.escapeHtml4(lastReplyInfo.getContent())  )	;
			}
			dataOne.put("lastReplyInfo",  lastReplyInfo); 
			dataOne.put("lastReplyIsSelf",   (lastReplyInfo!=null && lastReplyInfo.getSentUsrId().equals( sellerUsrId)) ?"Y":"N" ); //最后一句聊天是否为自己 add at 2017/05/31 12:57 by fidel 
			dataOne.put("unReadNum",  chatSessionUnReadNumMap.get( bean.getChatSessionNo() )  !=null ? chatSessionUnReadNumMap.get( bean.getChatSessionNo() ) : 0 ); 
			dataOne.put("createTime",  bean.getCreateTime() );
			dataOne.put("orderId",  bean.getId() );
			dataOne.put("chatSessionNo",  bean.getChatSessionNo() );
			dataOne.put("status",  bean.getStatus() );
			boolean isServiceEnd = now.after( bean.getEndChatTime() ) ;
			boolean isCanEvaluate = ( bean.getEvaluateTime() == null && OrderAidUtil.getCanEvaluateStatus().contains(  bean.getStatus()) ); 
			dataOne.put("isCanEvaluate", isCanEvaluate ? "Y":"N");  //是否可评价
			dataOne.put("isServiceEnd", isServiceEnd ? "Y":"N");  //聊天服务是否已过截止时间
			dataOne.put("hadRefund",  (bean.getRefundApplyTime()!=null  ) ? "Y":"N");  //是否发起过投诉
			boolean hadEvaluate = bean.getEvaluateTime() !=null ;
			dataOne.put("hadEvaluate", hadEvaluate ? "Y":"N");  //是否已评价
			dataOne.put("orderId", bean.getId());  //是否已评价
			dataOne.put("isWaitMasterService", ( OrderStatus.pay_succ.getStrValue().equals(bean.getStatus() )  && bean.getSellerFirstReplyTime()==null ) ? 'Y' :'N' );//是否等待老师服务
			dataList.add(dataOne);
		}
		JSONObject result = JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "data", dataList);
		return result;
	}
	
	/**
	 * @param sellerUsrId
	 * @param currentPage
	 * @param perPageNum
	 * @since 2017/07/13
	 * @return
	 */
	public JSONObject findMasterUsrBillDetailList(long sellerUsrId ,Long orderSettleId,  int currentPage,int perPageNum ){
		if(orderSettleId!=null){
			return _findMasterUsrBillDetailList_had(sellerUsrId, orderSettleId, currentPage, perPageNum);
		}else{
			return _findMasterUsrBillDetailList_wait(sellerUsrId, currentPage, perPageNum);
		}
	}
	
	private Date getSettlementCycleBeginTime(Date now){
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
	
	
	private JSONObject _findMasterUsrBillDetailList_had(long sellerUsrId,long orderSettleId , int currentPage,int perPageNum){
		try{
			FsOrderSettlement settlementBean = this.fsOrderSettlementDao.findById(orderSettleId);
			 if(settlementBean == null){
				 return JsonUtils.commonJsonReturn("1000", "查无数据");
			 }
			 List<Long> _orderIds = fsOrderSettlementRelaDao.findOrderIds1(orderSettleId, sellerUsrId);
			 //加上退款的。 
			 List<Long> _refundOrderIds =  this.fsOrderDao.findOrderIds1(sellerUsrId, OrderAidUtil.getMasterRefundSatus(), settlementBean.getSettlementCycleBeginTime(), settlementBean.getSettlementCycleEndTime());
			 
			 List<Long> orderIds = _mergeOrdeIds(_orderIds, _refundOrderIds);
			 if(CollectionUtils.isEmpty(_orderIds) &&  CollectionUtils.isEmpty(_refundOrderIds)){
				 return JsonUtils.commonJsonReturn("1000", "查无数据");
			 }
			 if( currentPage * perPageNum >= orderIds.size() ){
				 return JsonUtils.commonJsonReturn("1000", "查无数据");
			 }
			 List<Long> orderSubIds = orderIds.subList( currentPage * perPageNum , Math.min(currentPage * perPageNum + perPageNum , orderIds.size() ));
			 List<FsOrder> hadSettleOrderList  =  this.fsOrderDao.findByOrderIds(orderSubIds);
			 if(CollectionUtils.isEmpty(hadSettleOrderList)){
				 return JsonUtils.commonJsonReturn("1000", "查无数据");
			 }
			 JSONObject result = JsonUtils.commonJsonReturn();
			 // 个税情况
			 JSONObject gsDetail = new JSONObject();
			 gsDetail.put("djgsDesc",    CommonUtils.numberFormat( settlementBean.getPersonalIncomeTaxRmbAmt() / 100d , "###,##0.00", "0.0")  );
			 gsDetail.put("jszqDesc",  CommonUtils.dateToString(  settlementBean.getCreateTime(),  CommonUtils.dateFormat1,"")  );
			 JSONArray eleList = new JSONArray();
			 for(FsOrder bean :  hadSettleOrderList ){
				 eleList.add( _findMasterUsrBillDetailList_one(bean) );
			 }
			 JsonUtils.setBody(result, "data", eleList);
			 JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(eleList) ? eleList.size():0);
			 JsonUtils.setBody(result, "gsDetail", gsDetail);  // 个税情况
			 return result;
		}catch(Exception e){
			logger.error("sellerUsrId:"+sellerUsrId+",currentPage:"+currentPage+",perPageNum:"+perPageNum, e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	
	private List<Long> _mergeOrdeIds(List<Long> _orderIds , List<Long> _refundOrderIds){
		List<Long> allOrderIdList = new ArrayList<Long>();
		if(CollectionUtils.isNotEmpty(_orderIds)){
			allOrderIdList.addAll(_orderIds);
		}
		if(CollectionUtils.isNotEmpty(_refundOrderIds)){
			allOrderIdList.addAll(_refundOrderIds);
		}
		return allOrderIdList;
	}
	
	private JSONObject _findMasterUsrBillDetailList_wait(long sellerUsrId,int currentPage,int perPageNum){
		try{
			 Date settlementCycleBeginTime = getSettlementCycleBeginTime(new Date());
			 List<FsOrder> waitSettleOrderList = 	this.fsOrderDao.findShortOrderInfoForSettlement2(sellerUsrId, OrderAidUtil.getMasterWaitIncometatus() ,OrderAidUtil.getMasterRefundSatus(), settlementCycleBeginTime,currentPage , perPageNum) ;
			 if(CollectionUtils.isEmpty(waitSettleOrderList)){
				 return JsonUtils.commonJsonReturn("1000", "查无数据");
			 }
			 JSONObject result = JsonUtils.commonJsonReturn();
			 // 个税情况
			 JSONObject gsDetail = new JSONObject();
			 gsDetail.put("djgsDesc",  "待计算");
			 gsDetail.put("jszqDesc",  CommonUtils.dateToString(  new Date(),  CommonUtils.dateFormat1,"")  );
			 JSONArray eleList = new JSONArray();
			 for(FsOrder bean :  waitSettleOrderList ){
				 eleList.add( _findMasterUsrBillDetailList_one(bean) );
			 }
			 JsonUtils.setBody(result, "data", eleList);
			 JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(eleList) ? eleList.size():0);
			 JsonUtils.setBody(result, "gsDetail", gsDetail);  // 个税情况
			 return result;
		}catch(Exception e){
			logger.error("sellerUsrId:"+sellerUsrId+",currentPage:"+currentPage+",perPageNum:"+perPageNum, e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}

	private JSONObject _findMasterUsrBillDetailList_one(FsOrder bean ){
		JSONObject dataOne = new JSONObject(true);
		Long sqsr = OrderAidUtil.mul(bean.getPayRmbAmt(), (1-OrderAidUtil.getPlatCommissionRate())).longValue();
		dataOne.put("ddje", bean.getPayRmbAmt() ); //订单金额 单位分
		dataOne.put("ddjeDesc",  CommonUtils.numberFormat(bean.getPayRmbAmt() /100d, "###,##0.00", "0.0")    ); //订单金额 格式化数据 单位元
		dataOne.put("sqsr", sqsr ); //税前收入  单位分
		dataOne.put("sqsrDesc",   CommonUtils.numberFormat(sqsr/100d, "###,##0.00", "0.0")     ); //税前收入格式化数据  单位元
		dataOne.put("ptyj", bean.getPayRmbAmt()  - sqsr ); //平台佣金  单位分
		dataOne.put("ptyjDesc",    CommonUtils.numberFormat( (bean.getPayRmbAmt()  - sqsr) /100d, "###,##0.00", "0.0")  ); //平台佣金 格式化数据 单位元
		dataOne.put("goodsName", bean.getGoodsName());
		dataOne.put("status", bean.getStatus());
		dataOne.put("statusDesc",  OrderAidUtil.masterIncomeDetailGetStatusDesc(bean.getStatus()) );
		dataOne.put("timeDesc",  CommonUtils.dateToString(bean.getCreateTime(),  CommonUtils.dateFormat1,"") );
		dataOne.put("simpleOrderExtraInfoAndCateName",   masterIncomeDetailGetSimpleOrderExtraInfoDesc(bean.getZxCateId(), bean.getOrderExtraInfo() , bean.getGoodsName())); 							
		return dataOne;
	}
	
	/**
	 * @param sellerUsrId
	 * @param currentPage
	 * @param perPageNum
	 * @since 2017/07/13
	 * @return
	 */
	public JSONObject findMasterUsrBillList(long sellerUsrId , int currentPage,int perPageNum){
		JSONObject result = JsonUtils.commonJsonReturn();
		try{
			JSONArray eleList = new JSONArray();
			if(currentPage ==0){
				//未结算的
				 List<FsOrder> waitSettleOrderList = this.fsOrderDao.findShortOrderInfoForSettlement1(sellerUsrId, OrderAidUtil.getMasterWaitIncometatus());
				 if(CollectionUtils.isNotEmpty(waitSettleOrderList)){
					 eleList.add(  _buildWaitSellteBill(waitSettleOrderList) );
				 }				
			}
			 //分页查询已结算的
			List<FsOrderSettlement>  settlementList =  fsOrderSettlementDao.find1(sellerUsrId, Arrays.asList("succ") , currentPage,perPageNum);
			if(CollectionUtils.isNotEmpty(settlementList)){
				for( FsOrderSettlement bean  : settlementList){
					eleList.add( _buildHaDSellteBill(bean)  );
				}
			}
			JsonUtils.setBody(result, "data", eleList);
			JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(eleList) ? eleList.size():0);
			return result;
		}catch(Exception e){
			logger.error("sellerUsrId:"+sellerUsrId+",currentPage:"+currentPage+",perPageNum:"+perPageNum, e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	
	private JSONObject _buildHaDSellteBill(FsOrderSettlement bean ){
		JSONObject result = new JSONObject(true);
		result.put("sqsr", bean.getOrderTotalPayRmbAmt() - bean.getPlatCommissionRmbAmt());  	//税前收入  单位分
		result.put("sqsrDesc",  	CommonUtils.numberFormat( 	 (bean.getOrderTotalPayRmbAmt() - bean.getPlatCommissionRmbAmt())/100d , "###,##0.00", "0.0")			);  	//税前收入格式化数据
		result.put("zdje", bean.getOrderTotalPayRmbAmt());  			//账单金额  单位分
		result.put("zdjeDesc",   	CommonUtils.numberFormat( 	bean.getOrderTotalPayRmbAmt() / 100d , "###,##0.00", "0.0")			);  			//账单金额格式化数据
		result.put("ptyj",  bean.getPlatCommissionRmbAmt());  		//平台佣金  单位分
		result.put("ptyjDesc",  	CommonUtils.numberFormat(bean.getPlatCommissionRmbAmt() / 100d , "###,##0.00", "0.0")				 );  		//平台佣金格式化数据
		result.put("djgs", bean.getPersonalIncomeTaxRmbAmt());  	//待缴个税  单位分
		result.put("djgsDesc", "¥" +CommonUtils.numberFormat(bean.getPersonalIncomeTaxRmbAmt()/100d, "###,##0.00", "0.0")  );  	//待缴个税  格式化数据 元
		result.put("hadSettle", "Y");  //是否已结算
		result.put("orderSettlementId", bean.getId());  
		result.put("year", bean.getSettlementCycle().substring(0, 4));  
		result.put("month",  bean.getSettlementCycle().substring(4));  
		result.put("sellerUsrId", bean.getSellerUsrId());  
		return  result;
	}
	
	
	private JSONObject _buildWaitSellteBill( List<FsOrder> waitSettleOrderList  ){
		Date now = new Date();
		JSONObject analysisListResult = _analysisList(waitSettleOrderList);
		//订单总金额 C端用户支付总金额
		Long orderTotalPayRmbAmt = (Long)JsonUtils.getBodyValue(analysisListResult, "orderTotalPayRmbAmt");
		//List<Long> ids =(List<Long>) JsonUtils.getBodyValue(analysisListResult, "ids");
		// 平台佣金 单位分           
		Long platCommissionRmbAmt = OrderAidUtil.mul(orderTotalPayRmbAmt, OrderAidUtil.getPlatCommissionRate()).longValue();
		//税前总收入
		Long beforeTaxIncomeRmbAmt = orderTotalPayRmbAmt - platCommissionRmbAmt;
		//应出个税 单位分
		Long personalIncomeTaxRmbAmt = OrderAidUtil.calPersonalIncomeTaxRmbAmt(beforeTaxIncomeRmbAmt);
		//实际到(转)账金额 单位分
		//long realArrivalAmt =  beforeTaxIncomeRmbAmt - personalIncomeTaxRmbAmt ;
		JSONObject result = new JSONObject(true);
		result.put("sqsr", beforeTaxIncomeRmbAmt);  	//税前收入  单位分
		result.put("sqsrDesc", CommonUtils.numberFormat(beforeTaxIncomeRmbAmt / 100d , "###,##0.00", "0.0") );  	//税前收入格式化数据
		result.put("zdje", orderTotalPayRmbAmt);  		//账单金额  单位分
		result.put("zdjeDesc", CommonUtils.numberFormat( orderTotalPayRmbAmt/100d, "###,##0.00", "0.0"));  		//账单金额格式化数据
		result.put("ptyj", platCommissionRmbAmt);  		//平台佣金  单位分
		result.put("ptyjDesc", CommonUtils.numberFormat(platCommissionRmbAmt/100d , "###,##0.00", "0.0") );  		//平台佣金格式数据
		result.put("djgs", personalIncomeTaxRmbAmt);  //待缴个税  单位分
		//result.put("djgs", CommonUtils.numberFormat(personalIncomeTaxRmbAmt/100d , "###,##0.00", "0.0") );  //待缴个税格式化数据
		result.put("djgsDesc",  "待计算"  );  	//待缴个税  格式化数据 元
		result.put("hadSettle", "N");  //是否已结算
		result.put("year", CommonUtils.dateToString(now, "yyyy", ""));  
		result.put("month",  CommonUtils.dateToString(now, "MM", ""));  
		return result;
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
	
	private String _getBuyUsrImgUrl(FsUsr usr,Map<Long,JSONObject> usrIdCacheWxInfoMap){
		JSONObject  cacheWxInfo =usrIdCacheWxInfoMap.get(usr.getId());
		if(cacheWxInfo == null && !usrIdCacheWxInfoMap.containsKey(usr.getId()) ){
			cacheWxInfo = UsrAidUtil.getCacheWeiXinInfo(usr);
			usrIdCacheWxInfoMap.put(usr.getId(), cacheWxInfo);
		}
		return UsrAidUtil.getUsrHeadImgUrl2(usr, cacheWxInfo, null);
	}
	private String _getBuyUsrNickName(FsUsr usr,Map<Long,JSONObject> usrIdCacheWxInfoMap){
		JSONObject  cacheWxInfo =usrIdCacheWxInfoMap.get(usr.getId());
		if(cacheWxInfo == null && !usrIdCacheWxInfoMap.containsKey(usr.getId()) ){
			cacheWxInfo = UsrAidUtil.getCacheWeiXinInfo(usr);
			usrIdCacheWxInfoMap.put(usr.getId(), cacheWxInfo);
		}
		return UsrAidUtil.getNickName2(usr, cacheWxInfo, "");
	}
	
	/** 
	 * 不会返回null ，  if ${masterUsrIdList} is empty return new HashMap<Long,FsMasterInfo>();
	 */
	private Map<Long,FsMasterInfo> findMasterInfoListAndConvert( List<Long> masterUsrIdList ){
		 Map<Long,FsMasterInfo> mapResult = new HashMap<Long,FsMasterInfo>();
		if(CollectionUtils.isEmpty(masterUsrIdList)){
			return mapResult;
		}
		List<FsMasterInfo> list =   fsMasterInfoDao.findByUsrIds2(masterUsrIdList, "approved", null) ; //this.fsMasterInfoDao.findByUsrIdsAndAuditStatusNotIng(masterUsrIdList);
		if(CollectionUtils.isEmpty(list)){
			return mapResult;
		}
		for(FsMasterInfo bean :  list){
			mapResult.put( bean.getUsrId() , bean);
		}
		return mapResult;
	}
	
	
	/**  key masterUsrIdList ,waitQueryUnReadNumChatSessionNoList   not return null **/
	private JSONObject getGroupList(List<FsOrder> orderList , Date now){
		List<String> waitQueryUnReadNumChatSessionNoList = new ArrayList<String>();
		List<Long> masterUsrIdList = new ArrayList<Long>();
		List<Long> buyUsrIdList = new ArrayList<Long>();
		for(FsOrder bean : orderList){
			if( OrderAidUtil.getCommAllOrderStatus().contains( bean.getStatus()  ) ){
				waitQueryUnReadNumChatSessionNoList.add(bean.getChatSessionNo() );
			}
			if(!masterUsrIdList.contains(  bean.getSellerUsrId()   )){
				masterUsrIdList.add(  bean.getSellerUsrId() );
			}
			if(!buyUsrIdList.contains(  bean.getSellerUsrId()   )){
				buyUsrIdList.add( bean.getBuyUsrId() );
			}
		}
		JSONObject result = new JSONObject();
		result.put("waitQueryUnReadNumChatSessionNoList"	, waitQueryUnReadNumChatSessionNoList);
		result.put("masterUsrIdList"	, masterUsrIdList);
		result.put("buyUsrIdList"	, buyUsrIdList);
		return result;
	}

	public JSONObject statMasterIncome(long masterUsrId){
		try{
			FsOrderSettlement statSettlement = this.fsOrderSettlementDao.stat1(masterUsrId, Arrays.asList("succ"));
			Map<String, Long> statusTotalFeeMap =  this.fsOrderDao.statAmtBySellerIdAndLastTimeAndStatusListWithGroupByStatus(masterUsrId,null,null);
			Long waitInComeAmt =  OrderAidUtil.getSumAmtFromStatusAmtMap(statusTotalFeeMap,  OrderAidUtil.getMasterWaitIncometatus()) ;
			//已退款 = 退款确认成功+ 退款确认中
			//退款申请中 = 退款申请成功+待审批
			Long refundedComeAmt =  OrderAidUtil.getSumAmtFromStatusAmtMap(statusTotalFeeMap, OrderAidUtil.getMasterRefundSatus());
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "allAmt", statSettlement==null ?  0  :  (statSettlement==null ?  0 : statSettlement.getRealArrivalAmt()  ) );
			JsonUtils.setBody(result, "waitInComeAmt",   OrderAidUtil.mul(waitInComeAmt, (1-OrderAidUtil.getPlatCommissionRate())));
			JsonUtils.setBody(result, "refundedComeAmt", OrderAidUtil.mul(refundedComeAmt, (1-OrderAidUtil.getPlatCommissionRate()))  );
			return result;
		}catch(Exception e){
			logger.error("masterUsrId:"+masterUsrId, e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	private String masterIncomeDetailGetTimeDesc(String status , Long amt ,Map map){
		if(OrderStatus.pay_succ.getStrValue().equals(status)  ||   OrderStatus.completed.getStrValue().equals(status) ||
				OrderStatus.settlementing.getStrValue().equals(status)  || OrderStatus.settlement_fail.getStrValue().equals(status) ){
			return "预计"+CommonUtils.dateToString((Date)map.get("settlement_time"), "MM.dd", "")+"到账";
		}
		if( OrderStatus.settlemented.getStrValue().equals(status)){
			return CommonUtils.dateToString((Date)map.get("settlement_time"), "yyyy-MM-dd HH:mm", "");
		}
		else if(OrderStatus.refunding.getStrValue().equals(status)  || OrderStatus.refund_fail.getStrValue().equals(status) ){
			return CommonUtils.dateToString((Date)map.get("refund_apply_time"), "yyyy-MM-dd HH:mm", "");
		}
		else if(OrderStatus.refunded.getStrValue().equals(status)){
			return CommonUtils.dateToString((Date)map.get("refund_confirm_time"),"yyyy-MM-dd HH:mm","");
		}else{
			return "--";
		}
	}
	
	private String masterIncomeDetailGetSimpleOrderExtraInfoDesc(Long cateId,String orderExtraInfo,String cateName){
		try{
			if(StringUtils.isEmpty(orderExtraInfo) || !OrderAidUtil.getNeedSupplyOrderInfoZxCateIds().contains(cateId)){
				return cateName;
			}
			
			JSONArray list = JSON.parseArray(orderExtraInfo);
			if(CollectionUtils.isEmpty(list)){
				return "";
			}
			JSONObject one = list.getJSONObject(0);
			String str = one.getString("realName");
			if(StringUtils.isEmpty(str)){
				str = one.getString("comName");
			}
			if(StringUtils.isEmpty(str)){
				str = one.getString("curUsrName");
			}
			return StringUtils.isNotEmpty(str) ? str+" | "+cateName : cateName;
		}catch(Exception e){
			logger.error("cateId:"+cateId+", orderExtraInfo:"+orderExtraInfo, e);
			return "";
		}
	}
	
	
	public JSONObject masterIncomeDetail(long masterUsrId , int currentPage,int perPageNum,String orderBy){
		try{
			List<Map> list = this.fsOrderDao.findMyInComeList(masterUsrId, currentPage, perPageNum, OrderAidUtil.getMasterLeiJitatus()	, null);
			if(CollectionUtils.isEmpty(list)){
				return JsonUtils.commonJsonReturn("1000", "查无数据");
			}
			JSONArray dataList = new JSONArray();
			String _status = null;
			Long _amt = null;
			for(Map map : list){
				JSONObject dataOne = new JSONObject(true);
				_status =  (String) map.get("status");
				_amt = Long.valueOf( map.get("settlement_master_service_fee").toString() );
				dataOne.put("id", map.get("id"));
				dataOne.put("status", _status);
				dataOne.put("statusDesc",  OrderAidUtil.masterIncomeDetailGetStatusDesc(_status) );
				dataOne.put("timeDesc",  masterIncomeDetailGetTimeDesc(_status, _amt, map) );
				dataOne.put("amt", _amt );
				dataOne.put("amtDesc",  CommonUtils.numberFormat( _amt /100d, "###,##0.00", "--") );
				dataOne.put("cateName",  (String)map.get("goods_name") );
				dataOne.put("simpleOrderExtraInfoAndCateName",   masterIncomeDetailGetSimpleOrderExtraInfoDesc(Long.valueOf(map.get("zx_cate_id").toString()), 
						(String)map.get("order_extra_info") , (String)map.get("goods_name")) 
						); 
				dataList.add(dataOne);
			}
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "data", dataList);
			JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(dataList) ? dataList.size():0);
			return result;
		}catch(Exception e){
			logger.error("masterUsrId:"+masterUsrId+", currentPage:"+currentPage+", perPageNum:"+perPageNum+", orderBy:"+orderBy, e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
}
