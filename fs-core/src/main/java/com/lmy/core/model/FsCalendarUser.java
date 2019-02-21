package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsCalendarUser extends BaseObject {

	private Long id;
	
	private String openId;
	
	private String wxName;
	
	private String avatarUrl;
	
	private Date createTime;
	
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOpenId() {
		return openId;
	}

	public FsCalendarUser setOpenId(String openId) {
		this.openId = openId;
		return this;
	}

	public String getWxName() {
		return wxName;
	}

	public FsCalendarUser setWxName(String wxName) {
		this.wxName = wxName;
		return this;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public FsCalendarUser setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public FsCalendarUser setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
}
