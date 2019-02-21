package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsCalendarOrder extends BaseObject {
	
	public final static String STATUS_INIT = "init";
	public final static String STATUS_CLOSE = "close";
	public final static String STATUS_PAY_SUCC = "pay_succ";
	public final static String STATUS_PAY_FAIL = "pay_fail";
	
	public final static String TYPE_MEMBER_FEE = "member_fee";

	private Long id;
	
	private Long userId;
	
	private Long infoId;
	
	private String orderNum;
	
	private String orderType;
	
	private String name;
	
	private String detail;
	
	private Long payRmbAmt;
	
	private Long discountRmbAmt;
	
	private String status;
	
	private Date createTime;
	
	private Date payConfirmTime;
	
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public FsCalendarOrder setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Long getInfoId() {
		return infoId;
	}

	public FsCalendarOrder setInfoId(Long infoId) {
		this.infoId = infoId;
		return this;
	}

	public String getOrderNum() {
		return orderNum;
	}

	public FsCalendarOrder setOrderNum(String orderNum) {
		this.orderNum = orderNum;
		return this;
	}

	public String getOrderType() {
		return orderType;
	}

	public FsCalendarOrder setOrderType(String orderType) {
		this.orderType = orderType;
		return this;
	}

	public String getName() {
		return name;
	}

	public FsCalendarOrder setName(String name) {
		this.name = name;
		return this;
	}

	public String getDetail() {
		return detail;
	}

	public FsCalendarOrder setDetail(String detail) {
		this.detail = detail;
		return this;
	}

	public Long getPayRmbAmt() {
		return payRmbAmt;
	}

	public FsCalendarOrder setPayRmbAmt(Long payRmbAmt) {
		this.payRmbAmt = payRmbAmt;
		return this;
	}

	public Long getDiscountRmbAmt() {
		return discountRmbAmt;
	}

	public FsCalendarOrder setDiscountRmbAmt(Long discountRmbAmt) {
		this.discountRmbAmt = discountRmbAmt;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public FsCalendarOrder setStatus(String status) {
		this.status = status;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getPayConfirmTime() {
		return payConfirmTime;
	}

	public FsCalendarOrder setPayConfirmTime(Date payConfirmTime) {
		this.payConfirmTime = payConfirmTime;
		return this;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public FsCalendarOrder setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
