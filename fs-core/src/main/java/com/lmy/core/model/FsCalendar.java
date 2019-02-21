package com.lmy.core.model;

import com.lmy.common.model.base.BaseObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
@SuppressWarnings("serial")
public class FsCalendar extends BaseObject {
	private Long id;
	
	private Long userId;
	
	private Integer type;
	
	private Date date;
	
	private Date lunarDate;
	
	private String lunarStr;
	
	private String yearHeaven;
	
	private String yearEarth;
	
	private String monthHeaven;
	
	private String monthEarth;
	
	private String dayHeaven;
	
	private String dayEarth;
	
	private String constel;
	
	private String guard;
	
	private String luck;
	
	private Integer luckV1;
	
	private Integer luckV2;
	
	private String evil;
	
	private String solar;
	
	private String holiday;
	
	private Long eventsValue;
	
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getLunarDate() {
		return lunarDate;
	}

	public void setLunarDate(Date lunarDate) {
		this.lunarDate = lunarDate;
	}

	public String getLunarStr() {
		return lunarStr;
	}

	public void setLunarStr(String lunarStr) {
		this.lunarStr = lunarStr;
	}

	public String getYearHeaven() {
		return yearHeaven;
	}

	public void setYearHeaven(String yearHeaven) {
		this.yearHeaven = yearHeaven;
	}

	public String getYearEarth() {
		return yearEarth;
	}

	public void setYearEarth(String yearEarth) {
		this.yearEarth = yearEarth;
	}

	public String getMonthHeaven() {
		return monthHeaven;
	}

	public void setMonthHeaven(String monthHeaven) {
		this.monthHeaven = monthHeaven;
	}

	public String getMonthEarth() {
		return monthEarth;
	}

	public void setMonthEarth(String monthEarth) {
		this.monthEarth = monthEarth;
	}

	public String getDayHeaven() {
		return dayHeaven;
	}

	public void setDayHeaven(String dayHeaven) {
		this.dayHeaven = dayHeaven;
	}

	public String getDayEarth() {
		return dayEarth;
	}

	public void setDayEarth(String dayEarth) {
		this.dayEarth = dayEarth;
	}

	public String getConstel() {
		return constel;
	}

	public void setConstel(String constel) {
		this.constel = constel;
	}

	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	public String getLuck() {
		return luck;
	}

	public void setLuck(String luck) {
		this.luck = luck;
	}

	public Integer getLuckV1() {
		return luckV1;
	}

	public void setLuckV1(Integer luckV1) {
		this.luckV1 = luckV1;
	}

	public Integer getLuckV2() {
		return luckV2;
	}

	public void setLuckV2(Integer luckV2) {
		this.luckV2 = luckV2;
	}

	public String getEvil() {
		return evil;
	}

	public void setEvil(String evil) {
		this.evil = evil;
	}

	public String getSolar() {
		return solar;
	}

	public void setSolar(String solar) {
		this.solar = solar;
	}

	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}

	public Long getEventsValue() {
		return eventsValue;
	}

	public void setEventsValue(Long eventsValue) {
		this.eventsValue = eventsValue;
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
