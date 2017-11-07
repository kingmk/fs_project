package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsReserve extends BaseObject {
	public static final String RESERVE_STATUS_VALID = "VALID";
	public static final String RESERVE_STATUS_EXPIRED = "EXPIRED";
	
	private Long id;
	
	private Long reserveUsrId;
	
	private Long masterUsrId;
	
	private String mobile;
	
	private Date createTime;
	
	private Date noticeTime;
	
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getReserveUsrId() {
		return reserveUsrId;
	}

	public void setReserveUsrId(Long reserveUsrId) {
		this.reserveUsrId = reserveUsrId;
	}

	public Long getMasterUsrId() {
		return masterUsrId;
	}

	public void setMasterUsrId(Long masterUsrId) {
		this.masterUsrId = masterUsrId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
}
