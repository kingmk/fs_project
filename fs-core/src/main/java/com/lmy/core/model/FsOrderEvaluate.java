package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsOrderEvaluate  extends BaseObject {
  /**  */
  private  Long  orderId;
  /** fs_master_service_cate.id */
  private  Long  goodsId;
  /**  */
  private  Long  sellerUsrId;
  /**  */
  private  Long  buyUsrId;
  /** 是否系统自动评价 */
  private  String  isAutoEvaluate;
  /** effect;del */
  private  String  status;
  /** 响应速度 */
  private  Long  respSpeed;
  /** 专业水平 */
  private  Long  majorLevel;
  /** 服务态度 */
  private  Long  serviceAttitude;
  /** 评论语 */
  private  String  evaluateWord;
  /**  */
	public Long getOrderId(){
		return this.orderId;
	}
  /**  */
	public FsOrderEvaluate setOrderId(Long orderId){
		 this.orderId=orderId;
		 return this;
	}
  /** fs_master_service_cate.id */
	public Long getGoodsId(){
		return this.goodsId;
	}
  /** fs_master_service_cate.id */
	public FsOrderEvaluate setGoodsId(Long goodsId){
		 this.goodsId=goodsId;
		 return this;
	}
  /**  */
	public Long getSellerUsrId(){
		return this.sellerUsrId;
	}
  /**  */
	public FsOrderEvaluate setSellerUsrId(Long sellerUsrId){
		 this.sellerUsrId=sellerUsrId;
		 return this;
	}
  /**  */
	public Long getBuyUsrId(){
		return this.buyUsrId;
	}
  /**  */
	public FsOrderEvaluate setBuyUsrId(Long buyUsrId){
		 this.buyUsrId=buyUsrId;
		 return this;
	}
  /** 是否系统自动评价 */
	public String getIsAutoEvaluate(){
		return this.isAutoEvaluate;
	}
  /** 是否系统自动评价 */
	public FsOrderEvaluate setIsAutoEvaluate(String isAutoEvaluate){
		 this.isAutoEvaluate=isAutoEvaluate;
		 return this;
	}
  /** effect;del */
	public String getStatus(){
		return this.status;
	}
  /** effect;del */
	public FsOrderEvaluate setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** 响应速度 */
	public Long getRespSpeed(){
		return this.respSpeed;
	}
  /** 响应速度 */
	public FsOrderEvaluate setRespSpeed(Long respSpeed){
		 this.respSpeed=respSpeed;
		 return this;
	}
  /** 专业水平 */
	public Long getMajorLevel(){
		return this.majorLevel;
	}
  /** 专业水平 */
	public FsOrderEvaluate setMajorLevel(Long majorLevel){
		 this.majorLevel=majorLevel;
		 return this;
	}
  /** 服务态度 */
	public Long getServiceAttitude(){
		return this.serviceAttitude;
	}
  /** 服务态度 */
	public FsOrderEvaluate setServiceAttitude(Long serviceAttitude){
		 this.serviceAttitude=serviceAttitude;
		 return this;
	}
  /** 评论语 */
	public String getEvaluateWord(){
		return this.evaluateWord;
	}
  /** 评论语 */
	public FsOrderEvaluate setEvaluateWord(String evaluateWord){
		 this.evaluateWord=evaluateWord;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
