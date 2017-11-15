package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsMasterStatistics extends BaseObject {
	public static final String STATISTICS_STATUS_VALID = "VALID"; 
	public static final String STATISTICS_STATUS_INVALID = "INVALID";
	
	private Long masterInfoId;
	
	private Long masterUsrId;
	
	private Long cateId;
	
	private Long cateParentId;
	
	private String cateName;
	
	private Long countOrder;
	
	private Long countEvaluate;
	
	private Long sumRespSpeed;
	
	private Long sumMajorLevel;
	
	private Long sumServiceAttitude;
	
	private Long minPrice;
	
	private String isPlatRecomm;
	
	private String status;
	
	private Date updateTime;

	public Long getMasterInfoId() {
		return masterInfoId;
	}

	public FsMasterStatistics setMasterInfoId(Long masterInfoId) {
		this.masterInfoId = masterInfoId;
		return this;
	}

	public Long getMasterUsrId() {
		return masterUsrId;
	}

	public FsMasterStatistics setMasterUsrId(Long masterUsrId) {
		this.masterUsrId = masterUsrId;
		return this;
	}

	public long getCateId() {
		return cateId;
	}

	public FsMasterStatistics setCateId(Long cateId) {
		this.cateId = cateId;
		return this;
	}

	public Long getCateParentId() {
		return cateParentId;
	}

	public FsMasterStatistics setCateParentId(Long cateParentId) {
		this.cateParentId = cateParentId;
		return this;
	}

	public String getCateName() {
		return cateName;
	}

	public FsMasterStatistics setCateName(String cateName) {
		this.cateName = cateName;
		return this;
	}

	public Long getCountOrder() {
		return countOrder;
	}

	public FsMasterStatistics setCountOrder(Long countOrder) {
		this.countOrder = countOrder;
		return this;
	}

	public Long getCountEvaluate() {
		return countEvaluate;
	}

	public FsMasterStatistics setCountEvaluate(Long countEvaluate) {
		this.countEvaluate = countEvaluate;
		return this;
	}

	public Long getSumRespSpeed() {
		return sumRespSpeed;
	}

	public FsMasterStatistics setSumRespSpeed(Long sumRespSpeed) {
		this.sumRespSpeed = sumRespSpeed;
		return this;
	}

	public Long getSumMajorLevel() {
		return sumMajorLevel;
	}

	public FsMasterStatistics setSumMajorLevel(Long sumMajorLevel) {
		this.sumMajorLevel = sumMajorLevel;
		return this;
	}

	public Long getSumServiceAttitude() {
		return sumServiceAttitude;
	}

	public FsMasterStatistics setSumServiceAttitude(Long sumServiceAttitude) {
		this.sumServiceAttitude = sumServiceAttitude;
		return this;
	}

	public Long getMinPrice() {
		return minPrice;
	}

	public FsMasterStatistics setMinPrice(Long minPrice) {
		this.minPrice = minPrice;
		return this;
	}

	public String getIsPlatRecomm() {
		return isPlatRecomm;
	}

	public FsMasterStatistics setIsPlatRecomm(String isPlatRecomm) {
		this.isPlatRecomm = isPlatRecomm;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public FsMasterStatistics setStatus(String status) {
		this.status = status;
		return this;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public FsMasterStatistics setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	
	public void defaultValue() {
		if (this.countOrder == null) {
			this.countOrder = 0L;
		}
		if (this.countEvaluate == null) {
			this.countEvaluate = 0L;
		}
		if (this.sumRespSpeed == null) {
			this.sumRespSpeed = 0L;
		}
		if (this.sumMajorLevel == null) {
			this.sumMajorLevel = 0L;
		}
		if (this.sumServiceAttitude == null) {
			this.sumServiceAttitude = 0L;
		}
		if (this.minPrice == null) {
			this.minPrice = 0L;
		}
		if (this.isPlatRecomm == null) {
			this.isPlatRecomm = "N";
		}
		if (this.status == null) {
			this.status = "OFF";
		}
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
}
