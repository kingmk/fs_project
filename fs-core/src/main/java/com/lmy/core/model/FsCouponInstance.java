package com.lmy.core.model;

import java.util.Date;

import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsCouponInstance extends BaseObject {
	
	public static String STATUS_UNUSED = "UNUSED";

	public static String STATUS_USED = "USED";

	private String name;
	
	private Long usrId;
	
	private Long templateId;
	
	private Date lastUseTime;
	
	private String type;
	
	private String categories;
	
	private Long payAmtMin;
	
	private Long discountAmt;
	
	private String status;
	
	private Date usedTime;

	public String getName() {
		return name;
	}

	public FsCouponInstance setName(String name) {
		this.name = name;
		return this;
	}

	public Long getUsrId() {
		return usrId;
	}

	public FsCouponInstance setUsrId(Long usrId) {
		this.usrId = usrId;
		return this;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public FsCouponInstance setTemplateId(Long templateId) {
		this.templateId = templateId;
		return this;
	}

	public Date getLastUseTime() {
		return lastUseTime;
	}

	public FsCouponInstance setLastUseTime(Date lastUseTime) {
		this.lastUseTime = lastUseTime;
		return this;
	}

	public String getType() {
		return type;
	}

	public FsCouponInstance setType(String type) {
		this.type = type;
		return this;
	}

	public String getCategories() {
		return categories;
	}

	public FsCouponInstance setCategories(String categories) {
		this.categories = categories;
		return this;
	}

	public Long getPayAmtMin() {
		return payAmtMin;
	}

	public FsCouponInstance setPayAmtMin(Long payAmtMin) {
		this.payAmtMin = payAmtMin;
		return this;
	}

	public Long getDiscountAmt() {
		return discountAmt;
	}

	public FsCouponInstance setDiscountAmt(Long discountAmt) {
		this.discountAmt = discountAmt;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public FsCouponInstance setStatus(String status) {
		this.status = status;
		return this;
	}

	public Date getUsedTime() {
		return usedTime;
	}

	public FsCouponInstance setUsedTime(Date usedTime) {
		this.usedTime = usedTime;
		return this;
	}
	
	
}
