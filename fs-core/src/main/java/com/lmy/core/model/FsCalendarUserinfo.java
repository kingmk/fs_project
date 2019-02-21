package com.lmy.core.model;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;

/**
 * @author yuxinjin
 *
 */
@SuppressWarnings("serial")
public class FsCalendarUserinfo extends BaseObject {
	
	public static final String STATUS_UNPAID = "00";
	public static final String STATUS_PAID = "01";
	
	public static final String TYPE_MAIN = "MAIN";
	public static final String TYPE_SUB = "SUB";

	private Long id;
	
	private Long userId;
	
	private String name;
	
	private String type;
	
	private String status;
	
	private String gender;
	
	private Date birthTime;
	
	private Date birthRealTime;
	
	private String birthProvince;
	
	private String birthCity;
	
	private String birthArea;
	
	private Integer birthLongitude;
	
	private Integer birthLatitude;
	
	private String mobile;
	
	private Date createTime;
	
	private Date updateTime;
	
	private String yearHeaven;
	
	private String yearEarth;
	
	private String monthHeaven;
	
	private String monthEarth;
	
	private String dayHeaven;
	
	private String dayEarth;
	
	private String hourHeaven;
	
	private String hourEarth;
	
	private String taiHeaven;
	
	private String taiEarth;
	
	private String lifeHeaven;
	
	private String lifeEarth;
	
	private Integer countUpdate;

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

	public String getName() {
		return name;
	}

	public FsCalendarUserinfo setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public FsCalendarUserinfo setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public FsCalendarUserinfo setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public Date getBirthTime() {
		return birthTime;
	}

	public FsCalendarUserinfo setBirthTime(Date birthTime) {
		this.birthTime = birthTime;
		return this;
	}

	public Date getBirthRealTime() {
		return birthRealTime;
	}

	public FsCalendarUserinfo setBirthRealTime(Date birthRealTime) {
		this.birthRealTime = birthRealTime;
		return this;
	}

	public String getBirthProvince() {
		return birthProvince;
	}

	public FsCalendarUserinfo setBirthProvince(String birthProvince) {
		this.birthProvince = birthProvince;
		return this;
	}

	public String getBirthCity() {
		return birthCity;
	}

	public FsCalendarUserinfo setBirthCity(String birthCity) {
		this.birthCity = birthCity;
		return this;
	}

	public String getBirthArea() {
		return birthArea;
	}

	public FsCalendarUserinfo setBirthArea(String birthArea) {
		this.birthArea = birthArea;
		return this;
	}

	public Integer getBirthLongitude() {
		return birthLongitude;
	}

	public FsCalendarUserinfo setBirthLongitude(Integer birthLongitude) {
		this.birthLongitude = birthLongitude;
		return this;
	}

	public Integer getBirthLatitude() {
		return birthLatitude;
	}

	public FsCalendarUserinfo setBirthLatitude(Integer birthLatitude) {
		this.birthLatitude = birthLatitude;
		return this;
	}

	public String getMobile() {
		return mobile;
	}

	public FsCalendarUserinfo setMobile(String mobile) {
		this.mobile = mobile;
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

	public FsCalendarUserinfo setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	public String getYearHeaven() {
		return yearHeaven;
	}

	public FsCalendarUserinfo setYearHeaven(String yearHeaven) {
		this.yearHeaven = yearHeaven;
		return this;
	}

	public String getYearEarth() {
		return yearEarth;
	}

	public FsCalendarUserinfo setYearEarth(String yearEarth) {
		this.yearEarth = yearEarth;
		return this;
	}

	public String getMonthHeaven() {
		return monthHeaven;
	}

	public FsCalendarUserinfo setMonthHeaven(String monthHeaven) {
		this.monthHeaven = monthHeaven;
		return this;
	}

	public String getMonthEarth() {
		return monthEarth;
	}

	public FsCalendarUserinfo setMonthEarth(String monthEarth) {
		this.monthEarth = monthEarth;
		return this;
	}

	public String getDayHeaven() {
		return dayHeaven;
	}

	public FsCalendarUserinfo setDayHeaven(String dayHeaven) {
		this.dayHeaven = dayHeaven;
		return this;
	}

	public String getDayEarth() {
		return dayEarth;
	}

	public FsCalendarUserinfo setDayEarth(String dayEarth) {
		this.dayEarth = dayEarth;
		return this;
	}

	public String getHourHeaven() {
		return hourHeaven;
	}

	public FsCalendarUserinfo setHourHeaven(String hourHeaven) {
		this.hourHeaven = hourHeaven;
		return this;
	}

	public String getHourEarth() {
		return hourEarth;
	}

	public FsCalendarUserinfo setHourEarth(String hourEarth) {
		this.hourEarth = hourEarth;
		return this;
	}

	public String getTaiHeaven() {
		return taiHeaven;
	}

	public FsCalendarUserinfo setTaiHeaven(String taiHeaven) {
		this.taiHeaven = taiHeaven;
		return this;
	}

	public String getTaiEarth() {
		return taiEarth;
	}

	public FsCalendarUserinfo setTaiEarth(String taiEarth) {
		this.taiEarth = taiEarth;
		return this;
	}

	public String getLifeHeaven() {
		return lifeHeaven;
	}

	public FsCalendarUserinfo setLifeHeaven(String lifeHeaven) {
		this.lifeHeaven = lifeHeaven;
		return this;
	}

	public String getLifeEarth() {
		return lifeEarth;
	}

	public FsCalendarUserinfo setLifeEarth(String lifeEarth) {
		this.lifeEarth = lifeEarth;
		return this;
	}
	
	public Integer getCountUpdate() {
		return countUpdate;
	}

	public FsCalendarUserinfo setCountUpdate(Integer countUpdate) {
		this.countUpdate = countUpdate;
		return this;
	}

	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
