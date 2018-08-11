package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

@SuppressWarnings("serial")
public class FsBanner extends BaseObject {
	
	private String imgurl;
	
	private String filepath;
	
	private String description;
	
	private String url;
	
	private Integer sort;
	
	private String status;
	
	private Date updateTime;

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
}
