package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsOrderSettlement  extends BaseObject {
  /** 结算周期单位 year,month,day,week */
  private  String  settlementCycleUnit;
  /** 结算周期 eg:201707 */
  private  String  settlementCycle;
  /** 结算周期 开始时间 */
  private  Date  settlementCycleBeginTime;
  /** 结算周期 结束时间 */
  private  Date  settlementCycleEndTime;
  /** 目前关联表 fs_usr.id */
  private  Long  sellerUsrId;
  /** 微信opend_id */
  private  String  sellerOpenId;
  /** 订单数量 */
  private  Long  orderTotalNum;
  /** 支付rmb总价 单位分 */
  private  Long  orderTotalPayRmbAmt;
  /** 平台佣金 单位分 */
  private  Long  platCommissionRmbAmt;
  /** 应出个税 单位分 */
  private  Long  personalIncomeTaxRmbAmt;
  /** 实际到账金额 单位分 */
  private  Long  realArrivalAmt;
  /** succ,ing,fail */
  private  String  status;
  /** 打款确认时间 */
  private  Date  confrimTime;
  /** 微信打款交易号 */
  private  String  wxOutTradeNo;
  /**  */
  private  Date  updateTime;
  /** 备注 */
  private  String  remark;
  /** 结算周期单位 year,month,day,week */
	public String getSettlementCycleUnit(){
		return this.settlementCycleUnit;
	}
  /** 结算周期单位 year,month,day,week */
	public FsOrderSettlement setSettlementCycleUnit(String settlementCycleUnit){
		 this.settlementCycleUnit=settlementCycleUnit;
		 return this;
	}
  /** 结算周期 eg:201707 */
	public String getSettlementCycle(){
		return this.settlementCycle;
	}
  /** 结算周期 eg:201707 */
	public FsOrderSettlement setSettlementCycle(String settlementCycle){
		 this.settlementCycle=settlementCycle;
		 return this;
	}
  /** 结算周期 开始时间 */
	public Date getSettlementCycleBeginTime(){
		return this.settlementCycleBeginTime;
	}
  /** 结算周期 开始时间 */
	public FsOrderSettlement setSettlementCycleBeginTime(Date settlementCycleBeginTime){
		 this.settlementCycleBeginTime=settlementCycleBeginTime;
		 return this;
	}
  /** 结算周期 结束时间 */
	public Date getSettlementCycleEndTime(){
		return this.settlementCycleEndTime;
	}
  /** 结算周期 结束时间 */
	public FsOrderSettlement setSettlementCycleEndTime(Date settlementCycleEndTime){
		 this.settlementCycleEndTime=settlementCycleEndTime;
		 return this;
	}
  /** 目前关联表 fs_usr.id */
	public Long getSellerUsrId(){
		return this.sellerUsrId;
	}
  /** 目前关联表 fs_usr.id */
	public FsOrderSettlement setSellerUsrId(Long sellerUsrId){
		 this.sellerUsrId=sellerUsrId;
		 return this;
	}
  /** 微信opend_id */
	public String getSellerOpenId(){
		return this.sellerOpenId;
	}
  /** 微信opend_id */
	public FsOrderSettlement setSellerOpenId(String sellerOpenId){
		 this.sellerOpenId=sellerOpenId;
		 return this;
	}
  /** 订单数量 */
	public Long getOrderTotalNum(){
		return this.orderTotalNum;
	}
  /** 订单数量 */
	public FsOrderSettlement setOrderTotalNum(Long orderTotalNum){
		 this.orderTotalNum=orderTotalNum;
		 return this;
	}
  /** 支付rmb总价 单位分 */
	public Long getOrderTotalPayRmbAmt(){
		return this.orderTotalPayRmbAmt;
	}
  /** 支付rmb总价 单位分 */
	public FsOrderSettlement setOrderTotalPayRmbAmt(Long orderTotalPayRmbAmt){
		 this.orderTotalPayRmbAmt=orderTotalPayRmbAmt;
		 return this;
	}
  /** 平台佣金 单位分 */
	public Long getPlatCommissionRmbAmt(){
		return this.platCommissionRmbAmt;
	}
  /** 平台佣金 单位分 */
	public FsOrderSettlement setPlatCommissionRmbAmt(Long platCommissionRmbAmt){
		 this.platCommissionRmbAmt=platCommissionRmbAmt;
		 return this;
	}
  /** 应出个税 单位分 */
	public Long getPersonalIncomeTaxRmbAmt(){
		return this.personalIncomeTaxRmbAmt;
	}
  /** 应出个税 单位分 */
	public FsOrderSettlement setPersonalIncomeTaxRmbAmt(Long personalIncomeTaxRmbAmt){
		 this.personalIncomeTaxRmbAmt=personalIncomeTaxRmbAmt;
		 return this;
	}
  /** 实际到账金额 单位分 */
	public Long getRealArrivalAmt(){
		return this.realArrivalAmt;
	}
  /** 实际到账金额 单位分 */
	public FsOrderSettlement setRealArrivalAmt(Long realArrivalAmt){
		 this.realArrivalAmt=realArrivalAmt;
		 return this;
	}
	/** succ,ing,fail */
	public String getStatus(){
		return this.status;
	}
	/** succ,ing,fail */
	public FsOrderSettlement setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** 打款确认时间 */
	public Date getConfrimTime(){
		return this.confrimTime;
	}
  /** 打款确认时间 */
	public FsOrderSettlement setConfrimTime(Date confrimTime){
		 this.confrimTime=confrimTime;
		 return this;
	}
  /** 微信打款交易号 */
	public String getWxOutTradeNo(){
		return this.wxOutTradeNo;
	}
  /** 微信打款交易号 */
	public FsOrderSettlement setWxOutTradeNo(String wxOutTradeNo){
		 this.wxOutTradeNo=wxOutTradeNo;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsOrderSettlement setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
  /** 备注 */
	public String getRemark(){
		return this.remark;
	}
  /** 备注 */
	public FsOrderSettlement setRemark(String remark){
		 this.remark=remark;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
