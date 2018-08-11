package com.lmy.core.model;

import java.util.Date;

import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsCouponTemplate extends BaseObject {
	
	public static String STATUS_VALID = "VALID";
	
	public static String STATUS_INVALID = "INVALID";
	
	private String name;
	
	private Date fetchBeginTime;
	
	private Date fetchEndTime;
	
	private Integer useDays;
	
	private Date lastUseTime;
	
	private Long userLimit;
	
	private Long totalLimit;
	
	private Long countFetch;
	
	private String rules;
	
	private String type;
	
	private String categories;
	
	private Long payAmtMin;
	
	private Long discountAmt;
	
	private Long discountAmtPlat;
	
	private Long discountAmtMaster;
	
	private String status;
	
	private Date updateTime;

	public String getName() {
		return name;
	}

	public FsCouponTemplate setName(String name) {
		this.name = name;
		return this;
	}

	public Date getFetchBeginTime() {
		return fetchBeginTime;
	}

	public FsCouponTemplate setFetchBeginTime(Date fetchBeginTime) {
		this.fetchBeginTime = fetchBeginTime;
		return this;
	}

	public Date getFetchEndTime() {
		return fetchEndTime;
	}

	public FsCouponTemplate setFetchEndTime(Date fetchEndTime) {
		this.fetchEndTime = fetchEndTime;
		return this;
	}

	public Integer getUseDays() {
		return useDays;
	}

	public FsCouponTemplate setUseDays(Integer useDays) {
		this.useDays = useDays;
		return this;
	}

	public Date getLastUseTime() {
		return lastUseTime;
	}

	public FsCouponTemplate setLastUseTime(Date lastUseTime) {
		this.lastUseTime = lastUseTime;
		return this;
	}

	public Long getUserLimit() {
		return userLimit;
	}

	public FsCouponTemplate setUserLimit(Long userLimit) {
		this.userLimit = userLimit;
		return this;
	}

	public Long getTotalLimit() {
		return totalLimit;
	}

	public FsCouponTemplate setTotalLimit(Long totalLimit) {
		this.totalLimit = totalLimit;
		return this;
	}

	public Long getCountFetch() {
		return countFetch;
	}

	public FsCouponTemplate setCountFetch(Long countFetch) {
		this.countFetch = countFetch;
		return this;
	}

	public String getRules() {
		return rules;
	}

	public FsCouponTemplate setRules(String rules) {
		this.rules = rules;
		return this;
	}

	public String getType() {
		return type;
	}

	public FsCouponTemplate setType(String type) {
		this.type = type;
		return this;
	}

	public String getCategories() {
		return categories;
	}

	public FsCouponTemplate setCategories(String categories) {
		this.categories = categories;
		return this;
	}

	public Long getPayAmtMin() {
		return payAmtMin;
	}

	public FsCouponTemplate setPayAmtMin(Long payAmtMin) {
		this.payAmtMin = payAmtMin;
		return this;
	}

	public Long getDiscountAmt() {
		return discountAmt;
	}

	public FsCouponTemplate setDiscountAmt(Long discountAmt) {
		this.discountAmt = discountAmt;
		return this;
	}

	public Long getDiscountAmtPlat() {
		return discountAmtPlat;
	}

	public FsCouponTemplate setDiscountAmtPlat(Long discountAmtPlat) {
		this.discountAmtPlat = discountAmtPlat;
		return this;
	}

	public Long getDiscountAmtMaster() {
		return discountAmtMaster;
	}

	public FsCouponTemplate setDiscountAmtMaster(Long discountAmtMaster) {
		this.discountAmtMaster = discountAmtMaster;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public FsCouponTemplate setStatus(String status) {
		this.status = status;
		return this;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public FsCouponTemplate setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}
	
	
}
