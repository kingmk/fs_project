package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsOrder  extends BaseObject {
  /** 下单/购买 人fs_usr.id */
  private  Long  buyUsrId;
  /** zxfw 咨询服务类; */
  private  String  orderType;
  /** 订单会话编号 */
  private  String  chatSessionNo;
  /** 目前关联 fs_master_service_cate.id */
  private  Long  goodsId;
  /** 目前关联表 fs_master_service_cate.name */
  private  String  goodsName;
  /** 交易描述 */
  private  String  transDesc;
  /** 目前关联表 fs_usr.id */
  private  Long  sellerUsrId;
  /** 关联表 fs_zx_cate.id */
  private  Long  zxCateId;
  /** 支付类型 weixinpay;alipay; */
  private  String  payType;
  /** 订单号 */
  private  String  orderNum;
  /** 购买数量 */
  private  Long  buyNum;
  /** 支付rmb总价 单位分 */
  private  Long  payRmbAmt;
  /** 折扣数(rmb) 单位分; 原价 = pay_rmb_amt + discount_rmb_amt, discount_rmb_amt=discount_amt_plat+discount_amt_master */
  private  Long  discountRmbAmt;
  /** 平台补贴的折扣数(rmb) 单位分; */
  private Long discountAmtPlat;
  /** 老师补贴的折扣数(rmb) 单位分; */
  private Long discountAmtMaster;
  /** 已退款总金额 单位分 */
  private  Long  refundRmbAmt;
  /** 使用了优惠券的id */
  private Long couponId;
  /** init 支付确认中;close 未支付|交易关闭;pay_succ 支付成功;pay_fail 支付失败;refund_applied 已申请退款待审批中; refunding 退款中(等待微信最终结果,一般可认为退款成功);refunded 已退款;refund_fail 退款失败;completed 已完成(已支付且24小时内有回复);settlementing 结算中(结算中打款给老师); settlemented 已结算（打款给老师）;settlement_fail 结算失败（打款给老师） */
  private  String  status;
  /** 订单单信息 */
  private  String  orderExtraInfo;
  /** 聊天开始时间 */
  private  Date  beginChatTime;
  /** 聊天结束时间 */
  private  Date  endChatTime;
  /** 最近/最后聊天时间 */
  private  Date  lastChatTime;
  /** 老师第一次回复时间 */
  private  Date  sellerFirstReplyTime;
  /** 与老师结算时间 */
  private  Date  settlementTime;
  /** 打给平台服务费 单位分 */
  private  Long  settlementPlatServiceFee;
  /** 转给老师的费用 单位分 */
  private  Long  settlementMasterServiceFee;
  /** 订单/服务/完成时间 */
  private  Date  completedTime;
  /** 用户评价时间 */
  private  Date  evaluateTime;
  /** 支付确认时间 支付成功就是聊天开始时间 */
  private  Date  payConfirmTime;
  /** 是否自动退款Y;N */
  private  String  isAutoRefund;
  /** 退款申请时间 */
  private  Date  refundApplyTime;
  /** 申请退款理由/原因/投诉语 */
  private  String  refundReason;
  /** 退款确认时间 */
  private  Date  refundConfirmTime;
  /** 退款审批语 */
  private  String  refundAuditWord;
  /** 退款审批时间 */
  private  Date  refundAuditTime;
  /**  */
  private  Date  updateTime;
  /** 备注 */
  private  String  remark;
  /** 是否被用户删除，NULL 或Y,N*/
  private String isUserDelete;
  
  /** 下单/购买 人fs_usr.id */
	public Long getBuyUsrId(){
		return this.buyUsrId;
	}
  /** 下单/购买 人fs_usr.id */
	public FsOrder setBuyUsrId(Long buyUsrId){
		 this.buyUsrId=buyUsrId;
		 return this;
	}
  /** zxfw 咨询服务类; */
	public String getOrderType(){
		return this.orderType;
	}
  /** zxfw 咨询服务类; */
	public FsOrder setOrderType(String orderType){
		 this.orderType=orderType;
		 return this;
	}
  /** 订单会话编号 */
	public String getChatSessionNo(){
		return this.chatSessionNo;
	}
  /** 订单会话编号 */
	public FsOrder setChatSessionNo(String chatSessionNo){
		 this.chatSessionNo=chatSessionNo;
		 return this;
	}
  /** 目前关联 fs_master_service_cate.id */
	public Long getGoodsId(){
		return this.goodsId;
	}
  /** 目前关联 fs_master_service_cate.id */
	public FsOrder setGoodsId(Long goodsId){
		 this.goodsId=goodsId;
		 return this;
	}
  /** 目前关联表 fs_master_service_cate.name */
	public String getGoodsName(){
		return this.goodsName;
	}
  /** 目前关联表 fs_master_service_cate.name */
	public FsOrder setGoodsName(String goodsName){
		 this.goodsName=goodsName;
		 return this;
	}
  /** 交易描述 */
	public String getTransDesc(){
		return this.transDesc;
	}
  /** 交易描述 */
	public FsOrder setTransDesc(String transDesc){
		 this.transDesc=transDesc;
		 return this;
	}
  /** 目前关联表 fs_usr.id */
	public Long getSellerUsrId(){
		return this.sellerUsrId;
	}
  /** 目前关联表 fs_usr.id */
	public FsOrder setSellerUsrId(Long sellerUsrId){
		 this.sellerUsrId=sellerUsrId;
		 return this;
	}
  /** 关联表 fs_zx_cate.id */
	public Long getZxCateId(){
		return this.zxCateId;
	}
  /** 关联表 fs_zx_cate.id */
	public FsOrder setZxCateId(Long zxCateId){
		 this.zxCateId=zxCateId;
		 return this;
	}
  /** 支付类型 weixinpay;alipay; */
	public String getPayType(){
		return this.payType;
	}
  /** 支付类型 weixinpay;alipay; */
	public FsOrder setPayType(String payType){
		 this.payType=payType;
		 return this;
	}
  /** 订单号 */
	public String getOrderNum(){
		return this.orderNum;
	}
  /** 订单号 */
	public FsOrder setOrderNum(String orderNum){
		 this.orderNum=orderNum;
		 return this;
	}
  /** 购买数量 */
	public Long getBuyNum(){
		return this.buyNum;
	}
  /** 购买数量 */
	public FsOrder setBuyNum(Long buyNum){
		 this.buyNum=buyNum;
		 return this;
	}
  /** 支付rmb总价 单位分 */
	public Long getPayRmbAmt(){
		return this.payRmbAmt;
	}
  /** 支付rmb总价 单位分 */
	public FsOrder setPayRmbAmt(Long payRmbAmt){
		 this.payRmbAmt=payRmbAmt;
		 return this;
	}
  /** 折扣数(rmb) 单位分; 原价 = pay_rmb_amt + discount_rmb_amt */
	public Long getDiscountRmbAmt(){
		return this.discountRmbAmt;
	}
  /** 折扣数(rmb) 单位分; 原价 = pay_rmb_amt + discount_rmb_amt */
	public FsOrder setDiscountRmbAmt(Long discountRmbAmt){
		 this.discountRmbAmt=discountRmbAmt;
		 return this;
	}
	
	public Long getDiscountAmtPlat() {
		return discountAmtPlat;
	}
	public FsOrder setDiscountAmtPlat(Long discountAmtPlat) {
		this.discountAmtPlat = discountAmtPlat;
		return this;
	}
	public Long getDiscountAmtMaster() {
		return discountAmtMaster;
	}
	public FsOrder setDiscountAmtMaster(Long discountAmtMaster) {
		this.discountAmtMaster = discountAmtMaster;
		return this;
	}
/** 已退款总金额 单位分 */
	public Long getRefundRmbAmt(){
		return this.refundRmbAmt;
	}
  /** 已退款总金额 单位分 */
	public FsOrder setRefundRmbAmt(Long refundRmbAmt){
		 this.refundRmbAmt=refundRmbAmt;
		 return this;
	}
	public Long getCouponId() {
		return couponId;
	}
	public FsOrder setCouponId(Long couponId) {
		this.couponId = couponId;
		return this;
	}
  /** init 支付确认中;close 未支付|交易关闭;pay_succ 支付成功;pay_fail 支付失败;refund_applied 已申请退款待审批中; refunding 退款中(等待微信最终结果,一般可认为退款成功);refunded 已退款;refund_fail 退款失败;completed 已完成(已支付且24小时内有回复);settlementing 结算中(结算中打款给老师); settlemented 已结算（打款给老师）;settlement_fail 结算失败（打款给老师） */
	public String getStatus(){
		return this.status;
	}
  /** init 支付确认中;close 未支付|交易关闭;pay_succ 支付成功;pay_fail 支付失败;refund_applied 已申请退款待审批中; refunding 退款中(等待微信最终结果,一般可认为退款成功);refunded 已退款;refund_fail 退款失败;completed 已完成(已支付且24小时内有回复);settlementing 结算中(结算中打款给老师); settlemented 已结算（打款给老师）;settlement_fail 结算失败（打款给老师） */
	public FsOrder setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** 订单单信息 */
	public String getOrderExtraInfo(){
		return this.orderExtraInfo;
	}
  /** 订单单信息 */
	public FsOrder setOrderExtraInfo(String orderExtraInfo){
		 this.orderExtraInfo=orderExtraInfo;
		 return this;
	}
  /** 聊天开始时间 */
	public Date getBeginChatTime(){
		return this.beginChatTime;
	}
  /** 聊天开始时间 */
	public FsOrder setBeginChatTime(Date beginChatTime){
		 this.beginChatTime=beginChatTime;
		 return this;
	}
  /** 聊天结束时间 */
	public Date getEndChatTime(){
		return this.endChatTime;
	}
  /** 聊天结束时间 */
	public FsOrder setEndChatTime(Date endChatTime){
		 this.endChatTime=endChatTime;
		 return this;
	}
  /** 最近/最后聊天时间 */
	public Date getLastChatTime(){
		return this.lastChatTime;
	}
  /** 最近/最后聊天时间 */
	public FsOrder setLastChatTime(Date lastChatTime){
		 this.lastChatTime=lastChatTime;
		 return this;
	}
  /** 老师第一次回复时间 */
	public Date getSellerFirstReplyTime(){
		return this.sellerFirstReplyTime;
	}
  /** 老师第一次回复时间 */
	public FsOrder setSellerFirstReplyTime(Date sellerFirstReplyTime){
		 this.sellerFirstReplyTime=sellerFirstReplyTime;
		 return this;
	}
  /** 与老师结算时间 */
	public Date getSettlementTime(){
		return this.settlementTime;
	}
  /** 与老师结算时间 */
	public FsOrder setSettlementTime(Date settlementTime){
		 this.settlementTime=settlementTime;
		 return this;
	}
  /** 打给平台服务费 单位分 */
	public Long getSettlementPlatServiceFee(){
		return this.settlementPlatServiceFee;
	}
  /** 打给平台服务费 单位分 */
	public FsOrder setSettlementPlatServiceFee(Long settlementPlatServiceFee){
		 this.settlementPlatServiceFee=settlementPlatServiceFee;
		 return this;
	}
  /** 转给老师的费用 单位分 */
	public Long getSettlementMasterServiceFee(){
		return this.settlementMasterServiceFee;
	}
  /** 转给老师的费用 单位分 */
	public FsOrder setSettlementMasterServiceFee(Long settlementMasterServiceFee){
		 this.settlementMasterServiceFee=settlementMasterServiceFee;
		 return this;
	}
  /** 订单/服务/完成时间 */
	public Date getCompletedTime(){
		return this.completedTime;
	}
  /** 订单/服务/完成时间 */
	public FsOrder setCompletedTime(Date completedTime){
		 this.completedTime=completedTime;
		 return this;
	}
  /** 用户评价时间 */
	public Date getEvaluateTime(){
		return this.evaluateTime;
	}
  /** 用户评价时间 */
	public FsOrder setEvaluateTime(Date evaluateTime){
		 this.evaluateTime=evaluateTime;
		 return this;
	}
  /** 支付确认时间 支付成功就是聊天开始时间 */
	public Date getPayConfirmTime(){
		return this.payConfirmTime;
	}
  /** 支付确认时间 支付成功就是聊天开始时间 */
	public FsOrder setPayConfirmTime(Date payConfirmTime){
		 this.payConfirmTime=payConfirmTime;
		 return this;
	}
  /** 是否自动退款Y;N */
	public String getIsAutoRefund(){
		return this.isAutoRefund;
	}
  /** 是否自动退款Y;N */
	public FsOrder setIsAutoRefund(String isAutoRefund){
		 this.isAutoRefund=isAutoRefund;
		 return this;
	}
  /** 退款申请时间 */
	public Date getRefundApplyTime(){
		return this.refundApplyTime;
	}
  /** 退款申请时间 */
	public FsOrder setRefundApplyTime(Date refundApplyTime){
		 this.refundApplyTime=refundApplyTime;
		 return this;
	}
  /** 申请退款理由/原因/投诉语 */
	public String getRefundReason(){
		return this.refundReason;
	}
  /** 申请退款理由/原因/投诉语 */
	public FsOrder setRefundReason(String refundReason){
		 this.refundReason=refundReason;
		 return this;
	}
  /** 退款确认时间 */
	public Date getRefundConfirmTime(){
		return this.refundConfirmTime;
	}
  /** 退款确认时间 */
	public FsOrder setRefundConfirmTime(Date refundConfirmTime){
		 this.refundConfirmTime=refundConfirmTime;
		 return this;
	}
  /** 退款审批语 */
	public String getRefundAuditWord(){
		return this.refundAuditWord;
	}
  /** 退款审批语 */
	public FsOrder setRefundAuditWord(String refundAuditWord){
		 this.refundAuditWord=refundAuditWord;
		 return this;
	}
  /** 退款审批时间 */
	public Date getRefundAuditTime(){
		return this.refundAuditTime;
	}
  /** 退款审批时间 */
	public FsOrder setRefundAuditTime(Date refundAuditTime){
		 this.refundAuditTime=refundAuditTime;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsOrder setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
  /** 备注 */
	public String getRemark(){
		return this.remark;
	}
  /** 备注 */
	public FsOrder setRemark(String remark){
		 this.remark=remark;
		 return this;
	}
	
	public String getIsUserDelete() {
		return isUserDelete;
	}
	public FsOrder setIsUserDelete(String isUserDelete) {
		this.isUserDelete = isUserDelete;
		return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
