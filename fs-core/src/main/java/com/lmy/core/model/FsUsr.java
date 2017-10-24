package com.lmy.core.model ;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsUsr  extends BaseObject {
  /** 微信opend_id */
  private String  wxOpenId;
  /** 注册手机号码 */
  private String  registerMobile;
  /** 注册手机时间/手机验证码通过时间 */
  private Date  registerTime;
  /** 注册来源 */
  private String registerSrc;
  /** 真实姓名 */
  private String  realName;
  /** 用户昵称 */
  private String  nickName;
  /** 英文名 */
  private String  englishName;
  /** rank 区间; min 精确到分钟 */
  private String  birthTimeType;
  /** 09:30~11:20; 00:00~23:59 未填写; 21:30 精确到分 */
  private  String  birthTime;
  /** 阳历出生日期(样例生日) :eg:0112  一月12号 */
  private String  birthDate;
  /** 出生年份 eg: 1986  1986年出生 */
  private Integer  birthYear;
  /** 出生地址 */
  private String  birthAddress;
  /** 现居住地 */
  private String  liveAddress;
  /** 性别 M男人;F女人;O其他 */
  private String  sex;
  /** single 单身;celibate 独身;married 已婚;divorce 离异;widowed 丧偶;remarriage 在婚 */
  private String  marriageStatus;
  /** 家中排行 ：eg 老大,长子;次女 */
  private String  familyRank;
  /** 用户类别 common 普通;master 风水师 */
  private String  usrCate;
  /**  */
  private Date  updateTime;
  /** 用户自定义头像 */
  private String  usrHeadImgUrl;
  /** normal, black */
  private String status;
  
  /** 微信opend_id */
	public String getWxOpenId(){
		return this.wxOpenId;
	}
  /** 微信opend_id */
	public FsUsr setWxOpenId(String wxOpenId){
		 this.wxOpenId=wxOpenId;
		 return this;
	}
  /** 注册手机号码 */
	public String getRegisterMobile(){
		return this.registerMobile;
	}
  /** 注册手机号码 */
	public FsUsr setRegisterMobile(String registerMobile){
		 this.registerMobile=registerMobile;
		 return this;
	}
  /** 注册手机时间/手机验证码通过时间 */
	public Date getRegisterTime(){
		return this.registerTime;
	}
  /** 注册手机时间/手机验证码通过时间 */
	public FsUsr setRegisterTime(Date registerTime){
		 this.registerTime=registerTime;
		 return this;
	}
	public String getRegisterSrc() {
		return registerSrc;
	}
	public FsUsr setRegisterSrc(String registerSrc) {
		this.registerSrc = registerSrc;
		return this;
	}
	public String getRealName(){
		return this.realName;
	}
	public FsUsr setRealName(String realName){
		 this.realName=realName;
		 return this;
	}
	public String getNickName(){
		return this.nickName;
	}
	public FsUsr setNickName(String nickName){
		 this.nickName=nickName;
		 return this;
	}
	public String getEnglishName(){
		return this.englishName;
	}
	public FsUsr setEnglishName(String englishName){
		 this.englishName=englishName;
		 return this;
	}
	public String getBirthTimeType(){
		return this.birthTimeType;
	}
  /** rank 区间; min 精确到分钟 */
	public FsUsr setBirthTimeType(String birthTimeType){
		 this.birthTimeType=birthTimeType;
		 return this;
	}
  /** 09:30~11:20; 00:00~23:59 未填写; 21:30 精确到分 */
	public String getBirthTime(){
		return this.birthTime;
	}
  /** 09:30~11:20; 00:00~23:59 未填写; 21:30 精确到分 */
	public FsUsr setBirthTime(String birthTime){
		 this.birthTime=birthTime;
		 return this;
	}
  /** 阳历出生日期(样例生日) :eg:0112  一月12号 */
	public String getBirthDate(){
		return this.birthDate;
	}
  /** 阳历出生日期(样例生日) :eg:0112  一月12号 */
	public FsUsr setBirthDate(String birthDate){
		 this.birthDate=birthDate;
		 return this;
	}
  /** 出生年份 eg: 1986  1986年出生 */
	public Integer getBirthYear(){
		return this.birthYear;
	}
  /** 出生年份 eg: 1986  1986年出生 */
	public FsUsr setBirthYear(Integer birthYear){
		 this.birthYear=birthYear;
		 return this;
	}
	public String getBirthAddress(){
		return this.birthAddress;
	}
	public FsUsr setBirthAddress(String birthAddress){
		 this.birthAddress=birthAddress;
		 return this;
	}
	public String getLiveAddress(){
		return this.liveAddress;
	}
	public FsUsr setLiveAddress(String liveAddress){
		 this.liveAddress=liveAddress;
		 return this;
	}
  /** 性别 M男人;F女人;O其他 */
	public String getSex(){
		return this.sex;
	}
  /** 性别 M男人;F女人;O其他 */
	public FsUsr setSex(String sex){
		 this.sex=sex;
		 return this;
	}
  /** single 单身;celibate 独身;married 已婚;divorce 离异;widowed 丧偶;remarriage 在婚 */
	public String getMarriageStatus(){
		return this.marriageStatus;
	}
  /** single 单身;celibate 独身;married 已婚;divorce 离异;widowed 丧偶;remarriage 在婚 */
	public FsUsr setMarriageStatus(String marriageStatus){
		 this.marriageStatus=marriageStatus;
		 return this;
	}
  /** 家中排行 ：eg 老大,长子;次女 */
	public String getFamilyRank(){
		return this.familyRank;
	}
  /** 家中排行 ：eg 老大,长子;次女 */
	public FsUsr setFamilyRank(String familyRank){
		 this.familyRank=familyRank;
		 return this;
	}
  /** 用户类别 common 普通;master 风水师 */
	public String getUsrCate(){
		return this.usrCate;
	}
  /** 用户类别 common 普通;master 风水师 */
	public FsUsr setUsrCate(String usrCate){
		 this.usrCate=usrCate;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsUsr setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
  /** 用户自定义头像 */
	public String getUsrHeadImgUrl(){
		return this.usrHeadImgUrl;
	}
  /** 用户自定义头像 */
	public FsUsr setUsrHeadImgUrl(String usrHeadImgUrl){
		 this.usrHeadImgUrl=usrHeadImgUrl;
		 return this;
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
