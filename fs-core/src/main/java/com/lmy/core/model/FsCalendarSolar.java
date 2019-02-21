package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

public class FsCalendarSolar extends BaseObject {

	private Long id;
	
	private String name;
	
	private Date time;
	
	private Date showDate;
	
	private String yearHeaven;
	
	private String yearEarth;
	
	private String monthHeaven;
	
	private String monthEarth;
	
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public FsCalendarSolar setName(String name) {
		this.name = name;
		return this;
	}

	public Date getTime() {
		return time;
	}

	public FsCalendarSolar setTime(Date time) {
		this.time = time;
		return this;
	}

	public Date getShowDate() {
		return showDate;
	}

	public FsCalendarSolar setShowDate(Date showDate) {
		this.showDate = showDate;
		return this;
	}

	public String getYearHeaven() {
		return yearHeaven;
	}

	public FsCalendarSolar setYearHeaven(String yearHeaven) {
		this.yearHeaven = yearHeaven;
		return this;
	}

	public String getYearEarth() {
		return yearEarth;
	}

	public FsCalendarSolar setYearEarth(String yearEarth) {
		this.yearEarth = yearEarth;
		return this;
	}

	public String getMonthHeaven() {
		return monthHeaven;
	}

	public FsCalendarSolar setMonthHeaven(String monthHeaven) {
		this.monthHeaven = monthHeaven;
		return this;
	}

	public String getMonthEarth() {
		return monthEarth;
	}

	public FsCalendarSolar setMonthEarth(String monthEarth) {
		this.monthEarth = monthEarth;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
}
