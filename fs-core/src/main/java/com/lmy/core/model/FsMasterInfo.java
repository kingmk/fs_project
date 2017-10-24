package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsMasterInfo  extends BaseObject {
  /**  */
  private  Long  usrId;
  /** 微信opendid */
  private  String  wxOpenId;
  /** 审批/申请状态 ing 审批中;approved 审批通过;refuse 审批拒绝(不通过) */
  private  String  auditStatus;
  /** 审批时间 */
  private  Date  auditTime;
  /** 审批人语 */
  private  String  auditWord;
  /** 当前服务状态 ING 服务中;NOTING 非服务状态 */
  private  String  serviceStatus;
  /** 姓名 */
  private  String  name;
  /** 昵称 */
  private  String  nickName;
  /** 外文名 */
  private  String  englishName;
  /** 性别 M男人;F女人;O其他 */
  private  String  sex;
  /** 证件类型 0:身份证 1:护照 2:军官证 3:士兵证 4:回乡证 5:临时身份证 6:户口簿 7:警官证 8:台胞证 9:营业执照 10其它证件 */
  private  String  certType;
  /** 证件号码 */
  private  String  certNo;
  /** 证件照片地址 正面 */
  private  String  certImg1Url;
  /** 证件照片地址 反面 */
  private  String  certImg2Url;
  /** 头像地址 */
  private  String  headImgUrl;
  /** 出生年份 eg: 1986  1986年出生 */
  private  Integer  birthYear;
  /** 出生日期 :eg:0112  一月12号 */
  private  String  birthDate;
  /** 现居住地址 */
  private  String  liveAddress;
  /** 联系手机号码 */
  private  String  contactMobile;
  /** 联系qq号码 */
  private  String  contactQq;
  /** 联系微信账号 */
  private  String  contactWeixin;
  /** 所学门派 */
  private  String  school;
  /** 相关经历 */
  private  String  experience;
  /** 取得主要成就 */
  private  String  achievement;
  /** 擅长领域 */
  private  String  goodAt;
  /** 现职业 */
  private  String  profession;
  /** 从业开始时间-->推到出从业年限 eg: 2015/01/01  从业 1.5年  */
  private  Date  workDate;
  /** 是否专职 , Y;N */
  private  String  isFullTime;
  /** 是否已签约其他风水平台 , Y 有签约其他风水平台;N 未签约其他风水平台(雷门易专属) */
  private  String  isSignOther;
  /**  */
  private  Date  updateTime;
  
  private Date forbidTime;
  
  private String forbidReason;
  /**  */
	public Long getUsrId(){
		return this.usrId;
	}
  /**  */
	public FsMasterInfo setUsrId(Long usrId){
		 this.usrId=usrId;
		 return this;
	}
  /** 微信opendid */
	public String getWxOpenId(){
		return this.wxOpenId;
	}
  /** 微信opendid */
	public FsMasterInfo setWxOpenId(String wxOpenId){
		 this.wxOpenId=wxOpenId;
		 return this;
	}
  /** 审批/申请状态 ing 审批中;approved 审批通过;refuse 审批拒绝(不通过) */
	public String getAuditStatus(){
		return this.auditStatus;
	}
  /** 审批/申请状态 ing 审批中;approved 审批通过;refuse 审批拒绝(不通过) */
	public FsMasterInfo setAuditStatus(String auditStatus){
		 this.auditStatus=auditStatus;
		 return this;
	}
  /** 审批时间 */
	public Date getAuditTime(){
		return this.auditTime;
	}
  /** 审批时间 */
	public FsMasterInfo setAuditTime(Date auditTime){
		 this.auditTime=auditTime;
		 return this;
	}
  /** 审批人语 */
	public String getAuditWord(){
		return this.auditWord;
	}
  /** 审批人语 */
	public FsMasterInfo setAuditWord(String auditWord){
		 this.auditWord=auditWord;
		 return this;
	}
  /** 当前服务状态 ING 服务中;NOTING 非服务状态 */
	public String getServiceStatus(){
		return this.serviceStatus;
	}
  /** 当前服务状态 ING 服务中;NOTING 非服务状态 */
	public FsMasterInfo setServiceStatus(String serviceStatus){
		 this.serviceStatus=serviceStatus;
		 return this;
	}
  /** 姓名 */
	public String getName(){
		return this.name;
	}
  /** 姓名 */
	public FsMasterInfo setName(String name){
		 this.name=name;
		 return this;
	}
	
  public String getNickName() {
	return nickName;
}
public FsMasterInfo setNickName(String nickName) {
	this.nickName = nickName;
	return this;
}
/** 外文名 */
	public String getEnglishName(){
		return this.englishName;
	}
/** 外文名 */
	public FsMasterInfo setEnglishName(String englishName){
		 this.englishName=englishName;
		 return this;
	}
/** 性别 M男人;F女人;O其他 */
	public String getSex(){
		return this.sex;
	}
  /** 性别 M男人;F女人;O其他 */
	public FsMasterInfo setSex(String sex){
		 this.sex=sex;
		 return this;
	}
  /** 证件类型 0:身份证 1:护照 2:军官证 3:士兵证 4:回乡证 5:临时身份证 6:户口簿 7:警官证 8:台胞证 9:营业执照 10其它证件 */
	public String getCertType(){
		return this.certType;
	}
  /** 证件类型 0:身份证 1:护照 2:军官证 3:士兵证 4:回乡证 5:临时身份证 6:户口簿 7:警官证 8:台胞证 9:营业执照 10其它证件 */
	public FsMasterInfo setCertType(String certType){
		 this.certType=certType;
		 return this;
	}
  /** 证件号码 */
	public String getCertNo(){
		return this.certNo;
	}
  /** 证件号码 */
	public FsMasterInfo setCertNo(String certNo){
		 this.certNo=certNo;
		 return this;
	}
  /** 证件照片地址 正面 */
	public String getCertImg1Url(){
		return this.certImg1Url;
	}
  /** 证件照片地址 正面 */
	public FsMasterInfo setCertImg1Url(String certImg1Url){
		 this.certImg1Url=certImg1Url;
		 return this;
	}
  /** 证件照片地址 反面 */
	public String getCertImg2Url(){
		return this.certImg2Url;
	}
  /** 证件照片地址 反面 */
	public FsMasterInfo setCertImg2Url(String certImg2Url){
		 this.certImg2Url=certImg2Url;
		 return this;
	}
	  /** 头像地址 */
		public String getHeadImgUrl(){
			return this.headImgUrl;
		}
	  /** 头像地址 */
		public FsMasterInfo setHeadImgUrl(String headImgUrl){
			 this.headImgUrl=headImgUrl;
			 return this;
		}
  /** 出生年份 eg: 1986  1986年出生 */
	public Integer getBirthYear(){
		return this.birthYear;
	}
  /** 出生年份 eg: 1986  1986年出生 */
	public FsMasterInfo setBirthYear(Integer birthYear){
		 this.birthYear=birthYear;
		 return this;
	}
  /** 出生日期 :eg:0112  一月12号 */
	public String getBirthDate(){
		return this.birthDate;
	}
  /** 出生日期 :eg:0112  一月12号 */
	public FsMasterInfo setBirthDate(String birthDate){
		 this.birthDate=birthDate;
		 return this;
	}
  /** 现居住地址 */
	public String getLiveAddress(){
		return this.liveAddress;
	}
  /** 现居住地址 */
	public FsMasterInfo setLiveAddress(String liveAddress){
		 this.liveAddress=liveAddress;
		 return this;
	}
  /** 联系手机号码 */
	public String getContactMobile(){
		return this.contactMobile;
	}
  /** 联系手机号码 */
	public FsMasterInfo setContactMobile(String contactMobile){
		 this.contactMobile=contactMobile;
		 return this;
	}
  /** 联系qq号码 */
	public String getContactQq(){
		return this.contactQq;
	}
  /** 联系qq号码 */
	public FsMasterInfo setContactQq(String contactQq){
		 this.contactQq=contactQq;
		 return this;
	}
  /** 联系微信账号 */
	public String getContactWeixin(){
		return this.contactWeixin;
	}
  /** 联系微信账号 */
	public FsMasterInfo setContactWeixin(String contactWeixin){
		 this.contactWeixin=contactWeixin;
		 return this;
	}
  /** 所学门派 */
	public String getSchool(){
		return this.school;
	}
  /** 所学门派 */
	public FsMasterInfo setSchool(String school){
		 this.school=school;
		 return this;
	}
  /** 相关经历 */
	public String getExperience(){
		return this.experience;
	}
  /** 相关经历 */
	public FsMasterInfo setExperience(String experience){
		 this.experience=experience;
		 return this;
	}
  /** 取得主要成就 */
	public String getAchievement(){
		return this.achievement;
	}
  /** 取得主要成就 */
	public FsMasterInfo setAchievement(String achievement){
		 this.achievement=achievement;
		 return this;
	}
  /** 擅长领域 */
	public String getGoodAt(){
		return this.goodAt;
	}
  /** 擅长领域 */
	public FsMasterInfo setGoodAt(String goodAt){
		 this.goodAt=goodAt;
		 return this;
	}
  /** 现职业 */
	public String getProfession(){
		return this.profession;
	}
  /** 现职业 */
	public FsMasterInfo setProfession(String profession){
		 this.profession=profession;
		 return this;
	}
  /** 从业开始时间-->推到出从业年限 eg: 2015/01/01  从业 1.5年  */
	public Date getWorkDate(){
		return this.workDate;
	}
  /** 从业开始时间-->推到出从业年限 eg: 2015/01/01  从业 1.5年  */
	public FsMasterInfo setWorkDate(Date workDate){
		 this.workDate=workDate;
		 return this;
	}
  /** 是否专职 , Y;N */
	public String getIsFullTime(){
		return this.isFullTime;
	}
  /** 是否专职 , Y;N */
	public FsMasterInfo setIsFullTime(String isFullTime){
		 this.isFullTime=isFullTime;
		 return this;
	}
  /** 是否已签约其他风水平台 , Y 有签约其他风水平台;N 未签约其他风水平台(雷门易专属) */
	public String getIsSignOther(){
		return this.isSignOther;
	}
  /** 是否已签约其他风水平台 , Y 有签约其他风水平台;N 未签约其他风水平台(雷门易专属) */
	public FsMasterInfo setIsSignOther(String isSignOther){
		 this.isSignOther=isSignOther;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsMasterInfo setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
	
	public Date getForbidTime() {
		return forbidTime;
	}
	public FsMasterInfo setForbidTime(Date forbidTime) {
		this.forbidTime = forbidTime;
		return this;
	}
	
	public String getForbidReason() {
		return forbidReason;
	}
	public FsMasterInfo setForbidReason(String forbidReason) {
		this.forbidReason = forbidReason;
		return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
