package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsPayRecord  extends BaseObject {
  /** 公众号/服务窗 id */
  private  String  appId;
  /** 收款账号id,第三方支付机构申请:eg: 微信支付分配的商户号 */
  private  String  mchId;
  /** 支付者用户id */
  private  Long  usrId;
  /**  */
  private  String  openId;
  /** 风水订单号 */
  private  Long  orderId;
  /** 支付渠道 weixin;alipay */
  private  String  payChannel;
  /** 交易类型 unifiedorder;closeorder;refund 退款;transfers 转账 */
  private  String  tradeType;
  /** 交易状态 ing 交易(确认)中; succ 交易成功;fail 交易失败 */
  private  String  tradeStatus;
  /** 上送支付到订单号 */
  private  String  outTradeNo;
  /** 企业付款操作说明信息/交易说明,交易未企业转账是必填 */
  private  String  tradeDesc;
  /** NO_CHECK：不校验真实姓名;FORCE_CHECK：强校验真实姓名 */
  private  String  checkName;
  /** 收款用户真实姓名 */
  private  String  reUserName;
  /** 标价币种 */
  private  String  feeType;
  /** 订单总金额 单位分 当为退款交易时 这里是原交易金额 */
  private  Long  totalFee;
  /** 退款总金额 单位为分 */
  private  Long  refundFee;
  /** 商品简单描述 */
  private  String  body;
  /** 商品详情 */
  private  String  detail;
  /** APP和网页支付提交用户端ip */
  private  String  spbillCreateIp;
  /** 响应的支付渠道方交易编号 */
  private  String  respTradeNo;
  /** 微信响应码 */
  private  String  respCode;
  /** 微信响应msg */
  private  String  respMsg;
  /** 支付银行类别 */
  private  String  bankType;
  /** 交易确认时间 */
  private  Date  tradeConfirmTime;
  /** 附加数据 */
  private  String  attach;
  /** 商品标记 */
  private  String  goodsTag;
  /** 回调地址 */
  private  String  notifyUrl;
  /**  */
  private  Date  updateTime;
  /**  */
  private  String  remark;
  /** 公众号/服务窗 id */
	public String getAppId(){
		return this.appId;
	}
  /** 公众号/服务窗 id */
	public FsPayRecord setAppId(String appId){
		 this.appId=appId;
		 return this;
	}
  /** 收款账号id,第三方支付机构申请:eg: 微信支付分配的商户号 */
	public String getMchId(){
		return this.mchId;
	}
  /** 收款账号id,第三方支付机构申请:eg: 微信支付分配的商户号 */
	public FsPayRecord setMchId(String mchId){
		 this.mchId=mchId;
		 return this;
	}
  /** 支付者用户id */
	public Long getUsrId(){
		return this.usrId;
	}
  /** 支付者用户id */
	public FsPayRecord setUsrId(Long usrId){
		 this.usrId=usrId;
		 return this;
	}
  /**  */
	public String getOpenId(){
		return this.openId;
	}
  /**  */
	public FsPayRecord setOpenId(String openId){
		 this.openId=openId;
		 return this;
	}
  /** 风水订单号 */
	public Long getOrderId(){
		return this.orderId;
	}
  /** 风水订单号 */
	public FsPayRecord setOrderId(Long orderId){
		 this.orderId=orderId;
		 return this;
	}
  /** 支付渠道 weixin;alipay */
	public String getPayChannel(){
		return this.payChannel;
	}
  /** 支付渠道 weixin;alipay */
	public FsPayRecord setPayChannel(String payChannel){
		 this.payChannel=payChannel;
		 return this;
	}
  /** 交易类型 unifiedorder;closeorder;refund 退款;transfers 转账 */
	public String getTradeType(){
		return this.tradeType;
	}
  /** 交易类型 unifiedorder;closeorder;refund 退款;transfers 转账 */
	public FsPayRecord setTradeType(String tradeType){
		 this.tradeType=tradeType;
		 return this;
	}
  /** 交易状态 ing 交易(确认)中; succ 交易成功;fail 交易失败 */
	public String getTradeStatus(){
		return this.tradeStatus;
	}
  /** 交易状态 ing 交易(确认)中; succ 交易成功;fail 交易失败 */
	public FsPayRecord setTradeStatus(String tradeStatus){
		 this.tradeStatus=tradeStatus;
		 return this;
	}
  /** 上送支付到订单号 */
	public String getOutTradeNo(){
		return this.outTradeNo;
	}
  /** 上送支付到订单号 */
	public FsPayRecord setOutTradeNo(String outTradeNo){
		 this.outTradeNo=outTradeNo;
		 return this;
	}
  /** 企业付款操作说明信息/交易说明,交易未企业转账是必填 */
	public String getTradeDesc(){
		return this.tradeDesc;
	}
  /** 企业付款操作说明信息/交易说明,交易未企业转账是必填 */
	public FsPayRecord setTradeDesc(String tradeDesc){
		 this.tradeDesc=tradeDesc;
		 return this;
	}
  /** NO_CHECK：不校验真实姓名;FORCE_CHECK：强校验真实姓名 */
	public String getCheckName(){
		return this.checkName;
	}
  /** NO_CHECK：不校验真实姓名;FORCE_CHECK：强校验真实姓名 */
	public FsPayRecord setCheckName(String checkName){
		 this.checkName=checkName;
		 return this;
	}
  /** 收款用户真实姓名 */
	public String getReUserName(){
		return this.reUserName;
	}
  /** 收款用户真实姓名 */
	public FsPayRecord setReUserName(String reUserName){
		 this.reUserName=reUserName;
		 return this;
	}
  /** 标价币种 */
	public String getFeeType(){
		return this.feeType;
	}
  /** 标价币种 */
	public FsPayRecord setFeeType(String feeType){
		 this.feeType=feeType;
		 return this;
	}
  /** 订单总金额 单位分 当为退款交易时 这里是原交易金额 */
	public Long getTotalFee(){
		return this.totalFee;
	}
  /** 订单总金额 单位分 当为退款交易时 这里是原交易金额 */
	public FsPayRecord setTotalFee(Long totalFee){
		 this.totalFee=totalFee;
		 return this;
	}
  /** 退款总金额 单位为分 */
	public Long getRefundFee(){
		return this.refundFee;
	}
  /** 退款总金额 单位为分 */
	public FsPayRecord setRefundFee(Long refundFee){
		 this.refundFee=refundFee;
		 return this;
	}
  /** 商品简单描述 */
	public String getBody(){
		return this.body;
	}
  /** 商品简单描述 */
	public FsPayRecord setBody(String body){
		 this.body=body;
		 return this;
	}
  /** 商品详情 */
	public String getDetail(){
		return this.detail;
	}
  /** 商品详情 */
	public FsPayRecord setDetail(String detail){
		 this.detail=detail;
		 return this;
	}
  /** APP和网页支付提交用户端ip */
	public String getSpbillCreateIp(){
		return this.spbillCreateIp;
	}
  /** APP和网页支付提交用户端ip */
	public FsPayRecord setSpbillCreateIp(String spbillCreateIp){
		 this.spbillCreateIp=spbillCreateIp;
		 return this;
	}
  /** 响应的支付渠道方交易编号 */
	public String getRespTradeNo(){
		return this.respTradeNo;
	}
  /** 响应的支付渠道方交易编号 */
	public FsPayRecord setRespTradeNo(String respTradeNo){
		 this.respTradeNo=respTradeNo;
		 return this;
	}
  /** 微信响应码 */
	public String getRespCode(){
		return this.respCode;
	}
  /** 微信响应码 */
	public FsPayRecord setRespCode(String respCode){
		 this.respCode=respCode;
		 return this;
	}
  /** 微信响应msg */
	public String getRespMsg(){
		return this.respMsg;
	}
  /** 微信响应msg */
	public FsPayRecord setRespMsg(String respMsg){
		 this.respMsg=respMsg;
		 return this;
	}
  /** 支付银行类别 */
	public String getBankType(){
		return this.bankType;
	}
  /** 支付银行类别 */
	public FsPayRecord setBankType(String bankType){
		 this.bankType=bankType;
		 return this;
	}
  /** 交易确认时间 */
	public Date getTradeConfirmTime(){
		return this.tradeConfirmTime;
	}
  /** 交易确认时间 */
	public FsPayRecord setTradeConfirmTime(Date tradeConfirmTime){
		 this.tradeConfirmTime=tradeConfirmTime;
		 return this;
	}
  /** 附加数据 */
	public String getAttach(){
		return this.attach;
	}
  /** 附加数据 */
	public FsPayRecord setAttach(String attach){
		 this.attach=attach;
		 return this;
	}
  /** 商品标记 */
	public String getGoodsTag(){
		return this.goodsTag;
	}
  /** 商品标记 */
	public FsPayRecord setGoodsTag(String goodsTag){
		 this.goodsTag=goodsTag;
		 return this;
	}
  /** 回调地址 */
	public String getNotifyUrl(){
		return this.notifyUrl;
	}
  /** 回调地址 */
	public FsPayRecord setNotifyUrl(String notifyUrl){
		 this.notifyUrl=notifyUrl;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsPayRecord setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
  /**  */
	public String getRemark(){
		return this.remark;
	}
  /**  */
	public FsPayRecord setRemark(String remark){
		 this.remark=remark;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
