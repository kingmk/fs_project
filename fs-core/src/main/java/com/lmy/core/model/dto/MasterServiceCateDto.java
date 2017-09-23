package com.lmy.core.model.dto;
import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

@SuppressWarnings("serial")
public class MasterServiceCateDto implements Serializable {
	 private Long id ;
	  /** 关联表fs_usr.id */
	  private  Long  usrId;
	  /** 关联表fs_master_info.id */
	  private  Long  fsMasterInfoId;
	  /** 关联表zx_cate.id */
	  private  Long  fsZxCateId;
	  /** 类目名称 */
	  private  String  name;
	  private  Long  fsZxCateParentId;
	  private  String  fsZxCateParentName;
	  /** 是否平台推荐 Y;N */
	  private  String  isPlatRecomm;
	  /** 平台推荐时间 */
	  private  String  platRecommTime;
	  /** 当前服务状态 ON 服务中;OFF 非服务状态 */
	  private  String  status;
	  /** 人民币价格 单位分 */
	  private  Long  amt;
	  /** 建议人民币价格 单位分 **/
	  private Long suggestAmt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUsrId() {
		return usrId;
	}
	public void setUsrId(Long usrId) {
		this.usrId = usrId;
	}
	public Long getFsMasterInfoId() {
		return fsMasterInfoId;
	}
	public void setFsMasterInfoId(Long fsMasterInfoId) {
		this.fsMasterInfoId = fsMasterInfoId;
	}
	public Long getFsZxCateId() {
		return fsZxCateId;
	}
	public void setFsZxCateId(Long fsZxCateId) {
		this.fsZxCateId = fsZxCateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getFsZxCateParentId() {
		return fsZxCateParentId;
	}
	public void setFsZxCateParentId(Long fsZxCateParentId) {
		this.fsZxCateParentId = fsZxCateParentId;
	}
	public String getFsZxCateParentName() {
		return fsZxCateParentName;
	}
	public void setFsZxCateParentName(String fsZxCateParentName) {
		this.fsZxCateParentName = fsZxCateParentName;
	}
	public String getIsPlatRecomm() {
		return isPlatRecomm;
	}
	public void setIsPlatRecomm(String isPlatRecomm) {
		this.isPlatRecomm = isPlatRecomm;
	}
	public String getPlatRecommTime() {
		return platRecommTime;
	}
	public void setPlatRecommTime(String platRecommTime) {
		this.platRecommTime = platRecommTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getAmt() {
		return amt;
	}
	public void setAmt(Long amt) {
		this.amt = amt;
	}
	public Long getSuggestAmt() {
		return suggestAmt;
	}
	public void setSuggestAmt(Long suggestAmt) {
		this.suggestAmt = suggestAmt;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}	
