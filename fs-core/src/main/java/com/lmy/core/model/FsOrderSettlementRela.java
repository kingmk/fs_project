package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsOrderSettlementRela  extends BaseObject {
  /**  */
  private  Long  orderSettlementId;
  /**  */
  private  Long  sellerUsrId;
  /**  */
  private  Long  orderId;
  /**  */
	public Long getOrderSettlementId(){
		return this.orderSettlementId;
	}
  /**  */
	public FsOrderSettlementRela setOrderSettlementId(Long orderSettlementId){
		 this.orderSettlementId=orderSettlementId;
		 return this;
	}
  /**  */
	public Long getSellerUsrId(){
		return this.sellerUsrId;
	}
  /**  */
	public FsOrderSettlementRela setSellerUsrId(Long sellerUsrId){
		 this.sellerUsrId=sellerUsrId;
		 return this;
	}
  /**  */
	public Long getOrderId(){
		return this.orderId;
	}
  /**  */
	public FsOrderSettlementRela setOrderId(Long orderId){
		 this.orderId=orderId;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
