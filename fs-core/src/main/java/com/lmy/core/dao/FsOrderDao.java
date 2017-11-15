package com.lmy.core.dao ;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.enums.OrderStatus;
@Component
public class FsOrderDao extends GenericDAOImpl<FsOrder> {
	@Override
	public String getNameSpace() {
		return "fs_order";
	}
	/** 根据支付结果 update order  **/
	public int updateForPayByResult1(long id , String  status ,  Date beginChatTime , Date endChatTime,  Date lastChatTime, Date completedTime ,  Date settlementTime,  Date updateTime){
		if(updateTime == null ){
			updateTime = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("id", id);
		map.put("status", status);
		map.put("beginChatTime", beginChatTime);
		map.put("endChatTime", endChatTime);
		map.put("lastChatTime", lastChatTime);
		map.put("completedTime", completedTime);
		map.put("settlementTime", settlementTime);
		map.put("payConfirmTime"	, updateTime);
		map.put("updateTime"	, updateTime);
		return this.getSqlSession().update(this.getNameSpace()+".updateByIdWithStatus", map);
	}
	
	
	public int updateForRefundApplied(long id  , String isAutoRefund ,String refundReason,Long refundRmbAmt , Date now){
		if(now == null ){
			now = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("id", id);
		map.put("status",  OrderStatus.refund_applied.getStrValue());
		if(StringUtils.isNotBlank(isAutoRefund)){
			map.put("isAutoRefund"	, isAutoRefund);	
		}
		if(StringUtils.isNotBlank(refundReason)){
			map.put("refundReason"	, refundReason);	
		}
		map.put("refundRmbAmt"	, refundRmbAmt);
		map.put("refundApplyTime"	, now);
		map.put("updateTime"	, now);
		return this.getSqlSession().update(this.getNameSpace()+".updateByIdWithStatus", map);
	}
	public int updateForAfterRefundResult(long id  ,boolean refundSucc , String remark , Date now){
		if(now == null ){
			now = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("id", id);
		if(refundSucc){
			map.put("status",  OrderStatus.refunded.getStrValue());
			map.put("refundConfirmTime"	, now);
		}else{
			map.put("status", 	OrderStatus.refund_fail.getStrValue());
			map.put("refundRmbAmt", 0l);
			map.put("refundConfirmTime"	, now);
		}
		map.put("updateTime"	, now);
		if(StringUtils.isEmpty(remark)){
			map.put("remark", remark);
		}
		return this.getSqlSession().update(this.getNameSpace()+".updateByIdWithStatus", map);
	}
	
	public int updateForRefundAudit(long id  ,boolean isAgree , final String refundReason,final String refundAuditWord,String remark , Date now){
		if(now == null ){
			now = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("id", id);
		if(isAgree){
			map.put("status",  OrderStatus.refunding.getStrValue());
		}else{
			map.put("status", 	OrderStatus.refund_fail.getStrValue());
			map.put("refundRmbAmt", 0l);
			map.put("refundConfirmTime"	, now);
		}
		map.put("updateTime"	, now);
		if(!StringUtils.isEmpty(remark)){
			map.put("remark", remark);
		}
		if(!StringUtils.isEmpty(refundReason)){
			map.put("refundReason", refundReason);
		}
		if(!StringUtils.isEmpty(refundAuditWord)){
			map.put("refundAuditWord", refundAuditWord);
		}
		map.put("refundAuditTime", now);
		return this.getSqlSession().update(this.getNameSpace()+".updateByIdWithStatus", map);
	}
	
	public int updateRefundAfterWxRefundApplyResult(long id  ,  boolean isWxRefundApplySucc, Date now){
		if(now == null ){
			now = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("id", id);
		if(isWxRefundApplySucc){
			map.put("status",  OrderStatus.refunding.getStrValue());
		}else{
			map.put("status", 	OrderStatus.refund_fail.getStrValue());
			map.put("refundRmbAmt", 0l);
			map.put("refundConfirmTime"	, now);
		}
		map.put("updateTime"	, now);
		return this.getSqlSession().update(this.getNameSpace()+".updateByIdWithStatus", map);
	}
	
	
	public int updateLastChatTime(long id){
		JSONObject map = new JSONObject();
		map.put("id", id);
		return this.getSqlSession().update(this.getNameSpace()+".updateLastChatTime", map);
	}
	/**
	 * @param gtId 需要大于的id
	 * @param buyUsrId
	 * @param sellerUsrId
	 * @param currentPage
	 * @param perPageNum
	 * @param orderBy  排序方式 0  最近聊天时间 ; def is 0
	 * @param statusList
	 * @return
	 */
	public List<FsOrder>  findOrder1 (Long gtId,  Long buyUsrId , Long sellerUsrId, int currentPage,int perPageNum,int orderBy , List<String> statusList){
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		map.put("buyUsrId", buyUsrId);
		map.put("gtId", gtId);
		if(CollectionUtils.isNotEmpty(statusList  )){
			map.put("statusList", statusList);
		}
		map.put("orderBy", orderBy);
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findOrder1", map);
	}
	
	/**
	 * method never return null if not found return new HashMap<Long,Long>();
	 * @param sellerUsrId
	 * @param goodsIds if is empty 查询某个大师的所有曾经提供的服务
	 * @return key: cateId , value 成功服务次数 ()
	 */
	public Map<Long,Long> statSuccNumBySellerUsrIdWithGroupByCateIds(long  sellerUsrId, List<Long> cateIds){
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		if(CollectionUtils.isNotEmpty(cateIds)){
			map.put("cateIds", cateIds);	
		}
		List<Map> list =  this.getSqlSession().selectList(this.getNameSpace()+".statSuccNumBySellerUsrIdWithGroupByCateIds", map);
		Map<Long,Long> idNumMap = new HashMap<Long,Long>();
		if(CollectionUtils.isNotEmpty(list)){
			for(Map _map :  list){
				idNumMap.put( Long.valueOf( _map.get("cateId").toString()) , (Long)_map.get("num"));
			}
		}
		return idNumMap;
	}
	/**
	 * @param sellerUsrId 统计某个大师的的收入
	 * @param lastTime if is null 统计的时间
	 * @return key status; value 所有费用
	 */
	public Map<String,Long> statAmtBySellerIdAndLastTimeAndStatusListWithGroupByStatus(long  sellerUsrId , Date lastTime,List<String> statusList){
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		map.put("lastTime", lastTime);
		if(CollectionUtils.isNotEmpty(statusList)){
			map.put("statusList",statusList);
		}
		List<Map> list =  this.getSqlSession().selectList(this.getNameSpace()+".statAmtBySellerIdAndLastTimeAndStatusListWithGroupByStatus", map);
		Map<String,Long> statusTotalFeeMap = new HashMap<String,Long>();
		if(CollectionUtils.isNotEmpty(list)){
			for(Map _map :  list){
				statusTotalFeeMap.put((String) _map.get("status"), Long.valueOf(_map.get("totalFee").toString() ));
			}
		}
		return statusTotalFeeMap;
	}
	/**
	 * @param masterUsrId
	 * @param currentPage
	 * @param perPageNum
	 * @param orderBy  暂时没用到
	 * @return
	 */
	public List<Map> findMyInComeList(long sellerUsrId , int currentPage,int perPageNum,List<String> statusList,  String orderBy){
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		if(CollectionUtils.isNotEmpty(statusList)){
			map.put("statusList", statusList);
		}
		map.put("orderBy", orderBy);
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findMyInComeList", map);
	}
	public Long statOrderNum(Long sellerUsrId , Long buyUsrId , List<String> statusList){
		Assert.isTrue(  !( sellerUsrId == null && buyUsrId == null ));
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		map.put("buyUsrId", buyUsrId);
		if(CollectionUtils.isNotEmpty(statusList)){
			map.put("statusList", statusList);
		}
		return this.getSqlSession().selectOne(this.getNameSpace()+".statOrderNum", map);
	}

	public List<FsOrder> findByOrderIds(List<Long> orderIds){
		Assert.isTrue( CollectionUtils.isNotEmpty(orderIds) );
		JSONObject parameter = new JSONObject();
		parameter.put("orderIds", orderIds);
		return this.getSqlSession().selectList(this.getNameSpace()+".findByOrderIds", parameter);
	}
	
	public List<Map> statAllSellerUsrOrderNum1(String isPlatRecomm,Long zxCateId){
		JSONObject parameter = new JSONObject();
		if(StringUtils.isNotBlank(isPlatRecomm)){
			parameter.put("isPlatRecomm", isPlatRecomm);			
		}
		parameter.put("zxCateId", zxCateId);
		return this.getSqlSession().selectList(this.getNameSpace()+".statAllSellerUsrOrderNum1", parameter);
	}
	

	public List<Long> findOrderIds1(long sellerUsrId, List<String> statusList,  Date settlementCycleBeginTime , Date settlementCycleEndTime){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);
		if(CollectionUtils.isNotEmpty(statusList)){
			parameter.put("statusList", statusList);
		}
		parameter.put("settlementCycleBeginTime", settlementCycleBeginTime);
		parameter.put("settlementCycleEndTime", settlementCycleEndTime);
		return this.getSqlSession().selectList(this.getNameSpace()+".findOrderIds1", parameter);
	}
	
	/**
	 * @since 2017/06/05
	 * @param gtId
	 * @param currentPage
	 * @param perPageNum
	 * @return
	 */
	public List<Long> findOrderIdsForCompleted(Long gtId,  int currentPage,int perPageNum){
		JSONObject map = new JSONObject();
		map.put("gtId", gtId);
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findOrderIdsForCompleted", map);
	}
	/**
	 * @param settlementCycleBeginTime
	 * @param settlementCycleEndTime
	 * @since 2017/07/12
	 * @return
	 */
	public List<Long> findSellerUsrIdsByForSettlement(Date settlementCycleBeginTime, Date settlementCycleEndTime){
		JSONObject parameter = new JSONObject();
		if(settlementCycleBeginTime!=null){
			parameter.put("settlementCycleBeginTime", settlementCycleBeginTime);	
		}
		if(settlementCycleEndTime != null){
			parameter.put("settlementCycleEndTime", settlementCycleEndTime);	
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findSellerUsrIdsByForSettlement", parameter);
	}
	public List<FsOrder> findShortOrderInfoForSettlement(long sellerUsrId ,Date settlementCycleBeginTime, Date settlementCycleEndTime){
		JSONObject parameter = new JSONObject();
		if(settlementCycleBeginTime!=null){
			parameter.put("settlementCycleBeginTime", settlementCycleBeginTime);	
		}
		if(settlementCycleEndTime != null){
			parameter.put("settlementCycleEndTime", settlementCycleEndTime);	
		}
		parameter.put("sellerUsrId", sellerUsrId);	
		return this.getSqlSession().selectList(this.getNameSpace()+".findShortOrderInfoForSettlement", parameter);
	}
	public List<FsOrder> findShortOrderInfoForSettlement1(long sellerUsrId,List<String> statusList){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		if(CollectionUtils.isNotEmpty(statusList)){
			parameter.put("statusList", statusList);	
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findShortOrderInfoForSettlement1", parameter);
	}
	
	public List<FsOrder> findShortOrderInfoForSettlement2(long sellerUsrId,List<String> waitStatusList  , List<String> curPeriodsRefundStatusList ,   Date settlementCycleBeginTime,int currentPage,int perPageNum){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		if(CollectionUtils.isNotEmpty(waitStatusList)){
			parameter.put("waitStatusList", waitStatusList);	
		}
		if(CollectionUtils.isNotEmpty(curPeriodsRefundStatusList)){
			parameter.put("curPeriodsRefundStatusList", curPeriodsRefundStatusList);	
		}
		parameter.put("settlementCycleBeginTime", settlementCycleBeginTime);
		
		parameter.put("limitBegin", perPageNum * currentPage);
		parameter.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findShortOrderInfoForSettlement2", parameter);
	}
	
	public int updateForSettlementBeforCallWeiXin(long sellerUsrId, List<Long>orderIds ){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		parameter.put("orderIds", orderIds);	
		return this.getSqlSession().update(this.getNameSpace()+".updateForSettlementBeforCallWeiXin", parameter);
	}
	
	public int updateForSettlementAfterCallWeiXin(String status,long sellerUsrId, List<Long> orderIds ){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		parameter.put("orderIds", orderIds);	
		parameter.put("status", status);	
		return this.getSqlSession().update(this.getNameSpace()+".updateForSettlementAfterCallWeiXin", parameter);
	}
	
	public long countContactOrders(long sellerUsrId, long buyUsrId) {
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);
		parameter.put("buyUsrId", buyUsrId);
		
		Map<String, Long> rlt = this.getSqlSession().selectOne(this.getNameSpace()+".countContactOrders", parameter);
		long count = rlt.get("count");
		return count;
	}
	
	public List<FsOrder> findContactOrders(Long sellerUsrId, Long buyUsrId, Long excludeOrderId, int currentPage,int perPageNum) {
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		map.put("buyUsrId", buyUsrId);
		map.put("excludeOrderId", excludeOrderId);
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findContactOrders", map);
	}
	
	public List<Map<String, Object>> statOrdersByMasterCate() {
		return this.getSqlSession().selectList(this.getNameSpace()+".statOrdersByMasterCate");
	}
}
