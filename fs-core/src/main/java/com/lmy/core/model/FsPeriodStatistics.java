package com.lmy.core.model;

import java.util.Date;

import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsPeriodStatistics extends BaseObject {
	public static final String TYPE_YEAR = "YEAR";
	public static final String TYPE_MONTH = "MONTH";
	public static final String TYPE_WEEK = "WEEK";
	public static final String TYPE_CUSTOM = "CUSTOM";
	
	private Long id;
	
	private String periodName;
	
	private Long sellerUsrId;
	
	private Long countOrder;
	
	private Long countBuyer;
	
	private Long avgRespTime;
	
	private String type;
	
	private Date startTime;
	
	private Date endTime;
	
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPeriodName() {
		return periodName;
	}

	public FsPeriodStatistics setPeriodName(String periodName) {
		this.periodName = periodName;
		return this;
	}

	public Long getSellerUsrId() {
		return sellerUsrId;
	}

	public FsPeriodStatistics setSellerUsrId(Long sellerUsrId) {
		this.sellerUsrId = sellerUsrId;
		return this;
	}

	public Long getCountOrder() {
		return countOrder;
	}

	public FsPeriodStatistics setCountOrder(Long countOrder) {
		this.countOrder = countOrder;
		return this;
	}

	public Long getCountBuyer() {
		return countBuyer;
	}

	public FsPeriodStatistics setCountBuyer(Long countBuyer) {
		this.countBuyer = countBuyer;
		return this;
	}

	public Long getAvgRespTime() {
		return avgRespTime;
	}

	public FsPeriodStatistics setAvgRespTime(Long avgRespTime) {
		this.avgRespTime = avgRespTime;
		return this;
	}

	public String getType() {
		return type;
	}

	public FsPeriodStatistics setType(String type) {
		this.type = type;
		return this;
	}

	public Date getStartTime() {
		return startTime;
	}

	public FsPeriodStatistics setStartTime(Date startTime) {
		this.startTime = startTime;
		return this;
	}

	public Date getEndTime() {
		return endTime;
	}

	public FsPeriodStatistics setEndTime(Date endTime) {
		this.endTime = endTime;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("periodName: "+ this.getPeriodName()+";");
		sb.append("sellerUsrId: "+ this.getSellerUsrId()+";");
		sb.append("countOrder: "+ this.getCountOrder()+";");
		sb.append("countBuyer: "+ this.getCountBuyer()+";");
		sb.append("avgRespTime: "+ this.getAvgRespTime()+";");
		sb.append("type: "+ this.getType()+";");
		sb.append("startTime: "+ this.getStartTime()+";");
		sb.append("endTime: "+ this.getEndTime()+";");
		sb.append("createTime: "+ this.getCreateTime()+";");
		return sb.toString();
	}
	
}
