package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsMasterServiceCate  extends BaseObject {
  /** 关联表fs_usr.id */
  private  Long  usrId;
  /** 关联表fs_master_info.id */
  private  Long  fsMasterInfoId;
  /** 关联表zx_cate.id */
  private  Long  fsZxCateId;
  /** 类目名称 */
  private  String  name;
  /** 是否平台推荐 Y;N */
  private  String  isPlatRecomm;
  /** 平台推荐时间 */
  private  String  platRecommTime;
  /** 当前服务状态 ON 服务中;OFF 非服务状态 */
  private  String  status;
  /** 人民币价格 单位分 */
  private  Long  amt;
  /**  */
  private  Date  updateTime;
  /** 关联表fs_usr.id */
	public Long getUsrId(){
		return this.usrId;
	}
  /** 关联表fs_usr.id */
	public FsMasterServiceCate setUsrId(Long usrId){
		 this.usrId=usrId;
		 return this;
	}
  /** 关联表fs_master_info.id */
	public Long getFsMasterInfoId(){
		return this.fsMasterInfoId;
	}
  /** 关联表fs_master_info.id */
	public FsMasterServiceCate setFsMasterInfoId(Long fsMasterInfoId){
		 this.fsMasterInfoId=fsMasterInfoId;
		 return this;
	}
  /** 关联表zx_cate.id */
	public Long getFsZxCateId(){
		return this.fsZxCateId;
	}
  /** 关联表zx_cate.id */
	public FsMasterServiceCate setFsZxCateId(Long fsZxCateId){
		 this.fsZxCateId=fsZxCateId;
		 return this;
	}
  /** 类目名称 */
	public String getName(){
		return this.name;
	}
  /** 类目名称 */
	public FsMasterServiceCate setName(String name){
		 this.name=name;
		 return this;
	}
  /** 是否平台推荐 Y;N */
	public String getIsPlatRecomm(){
		return this.isPlatRecomm;
	}
  /** 是否平台推荐 Y;N */
	public FsMasterServiceCate setIsPlatRecomm(String isPlatRecomm){
		 this.isPlatRecomm=isPlatRecomm;
		 return this;
	}
  /** 平台推荐时间 */
	public String getPlatRecommTime(){
		return this.platRecommTime;
	}
  /** 平台推荐时间 */
	public FsMasterServiceCate setPlatRecommTime(String platRecommTime){
		 this.platRecommTime=platRecommTime;
		 return this;
	}
  /** 当前服务状态 ON 服务中;OFF 非服务状态 */
	public String getStatus(){
		return this.status;
	}
  /** 当前服务状态 ON 服务中;OFF 非服务状态 */
	public FsMasterServiceCate setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** 人民币价格 单位分 */
	public Long getAmt(){
		return this.amt;
	}
  /** 人民币价格 单位分 */
	public FsMasterServiceCate setAmt(Long amt){
		 this.amt=amt;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsMasterServiceCate setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
