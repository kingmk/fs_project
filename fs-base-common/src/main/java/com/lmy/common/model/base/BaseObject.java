package com.lmy.common.model.base;

import java.util.Date;

@SuppressWarnings("serial")
public class BaseObject implements java.io.Serializable {
	private Long id;
	private Date createTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
