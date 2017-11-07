package com.lmy.core.service.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsUsr;
@Service
public class UsrServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(UsrServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	public FsUsr findByOpenIdIfNotExistCreate(String wxOpenId,String defUsrHeadImg) {
		FsUsr usr = this.fsUsrDao.findByWxOpenIdOrId(wxOpenId, null);
		if(usr == null){
			Date now = new Date();
			usr  = new FsUsr();
			usr.setUsrCate("common") .setUsrHeadImgUrl(defUsrHeadImg) 
			.setWxOpenId(wxOpenId).setUpdateTime(now).setCreateTime(now);
			fsUsrDao.insert(usr);
		}
		return usr;
	}

	public JSONObject usrRegMobileWithNoCheck(Long usrId, String mobile, String source) {
		Date now = new Date();
		final FsUsr usrForUpdate = new FsUsr();
		usrForUpdate.setId(usrId);
		//setRegisterTime(now) add by fidel at 2017/06/02 20:37 bugfix 
		usrForUpdate.setRegisterMobile(mobile).setUpdateTime(now).setRegisterTime(now);
		if (source != null && source.length() > 0) {
			usrForUpdate.setRegisterSrc(source);
		}
		
		try{
			 fsTransactionTemplate.execute(new TransactionCallback<Integer>() {
				@Override
				public Integer doInTransaction(TransactionStatus status) {
					int effectNum = fsUsrDao.update(usrForUpdate);
					Assert.isTrue( effectNum ==1 );
					return effectNum;
				}
			});
			//logger.info("effectNum:"+effectNum);
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.info( "usrId:"+usrId + ", mobile:"+ mobile,e );
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	
	public JSONObject supplementUsrInfo(long loginUsrId , String realName , String nickName , String englishName,Integer birthYear  ,String birthDate , String birthAddress,String sex , String marriageStatus , String familyRank
			,String birthTimeType
			,String birthTime
			){
		boolean paramsCheckOk = supplementUsrInfoParamsCheck(realName, nickName, birthYear, birthDate, sex, marriageStatus, familyRank);
		if(!paramsCheckOk){
			return JsonUtils.commonJsonReturn("0001", "参数错误");
		}
		FsUsr beanForUdate = new FsUsr();
		beanForUdate.setId(loginUsrId);
		//2017/05/01 添加的两个参数
		if(StringUtils.isNotBlank(birthAddress) && !StringUtils.equals(	StringUtils.trim(birthAddress), "placeholder")){
			beanForUdate.setBirthAddress(birthAddress);
		}
		beanForUdate.setEnglishName(StringUtils.trim(englishName));
		beanForUdate.setRealName(StringUtils.trim(realName)).setNickName(StringUtils.trim(nickName)).setBirthYear(birthYear).setBirthDate(birthDate)
		.setSex(sex).setMarriageStatus(marriageStatus).setFamilyRank(familyRank).setUpdateTime(new Date()).setBirthTimeType(birthTimeType).setBirthTime(birthTime);
		try{
			this.fsUsrDao.update(beanForUdate);
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("保持用户补充信息错误  realName:{},nickName:{},birthYear:{},birthDate:{} ,sex:{} , marriageStatus:{} ,familyRank:{} ", 
					realName ,nickName ,birthYear , birthDate , sex ,  marriageStatus ,  familyRank ,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	@SuppressWarnings("deprecation")
	private boolean isCorrectyyyyMMdd(Integer birthYear  ,String birthDate){
		try{
			if(birthYear !=null && StringUtils.isNotEmpty(birthDate)){
				 String dateFormat1="yyyyMMdd";
				 SimpleDateFormat timeF1 = new SimpleDateFormat( dateFormat1);
				 timeF1.parse(birthYear+birthDate);
			}
			else if(birthYear !=null && StringUtils.isEmpty(birthDate)){
				Assert.isTrue(   birthYear>1917 && birthYear< ( ( new Date()) .getYear() +1 )       );
			}
			else if(birthYear == null && StringUtils.isNotEmpty(birthDate) ){
				if(birthDate.length()!=4){
					return false;
				}
				String birthDatePre2 = birthDate.substring(0,2);
				String birthDateEnd2 = birthDate.substring(2);
				Integer  pre2 = Integer.parseInt(  birthDatePre2);
				Integer  end2 = Integer.parseInt(  birthDateEnd2);
				Assert.isTrue( pre2>0 && pre2<13 );
				Assert.isTrue( end2>0 && end2<32 );
			}
			else if(birthYear == null && StringUtils.isEmpty(birthDate) ){
				return true;
			}
			return true;
		}catch(Exception e){
			logger.error("格式错误  birthYear:{},birthDate:{}"
					,birthYear , birthDate ,e);
			return false;
		}
	}
	
	private boolean supplementUsrInfoParamsCheck(String realName , String nickName , Integer birthYear  ,String birthDate , String sex , String marriageStatus , String familyRank){
		if(StringUtils.isEmpty(realName)
			&& StringUtils.isEmpty(nickName)
			&& birthYear == null 
					&& StringUtils.isEmpty(birthDate)
					&& StringUtils.isEmpty(sex)
					&& StringUtils.isEmpty(marriageStatus)
					&& StringUtils.isEmpty(familyRank)
				){
			logger.warn("所有参数不能全为空  realName:{},nickName:{},birthYear:{},birthDate:{} ,sex:{} , marriageStatus:{} ,familyRank:{} ", 
					realName ,nickName ,birthYear , birthDate , sex ,  marriageStatus ,  familyRank);
			return false;
		}
		if(StringUtils.isNotEmpty(realName) &&( realName.length()<2 ||  !CommonUtils.isChinese(realName)  )){
			logger.warn("真实姓名错误  realName:{},nickName:{},birthYear:{},birthDate:{} ,sex:{} , marriageStatus:{} ,familyRank:{} ", 
					realName ,nickName ,birthYear , birthDate , sex ,  marriageStatus ,  familyRank);
			return false;
		}
		if(! isCorrectyyyyMMdd(birthYear, birthDate)){
			logger.warn("出生年份/生日错误  realName:{},nickName:{},birthYear:{},birthDate:{} ,sex:{} , marriageStatus:{} ,familyRank:{} ", 
					realName ,nickName ,birthYear , birthDate , sex ,  marriageStatus ,  familyRank);
			return false;
		}
		//性别 M男人;F女人;O其他
		String [] correctSex = new String[]{"M","F","O"};
		if(StringUtils.isNotEmpty(sex) && !ArrayUtils.contains(correctSex, sex)   ){
			logger.warn("性别错误  realName:{},nickName:{},birthYear:{},birthDate:{} ,sex:{} , marriageStatus:{} ,familyRank:{} ", 
					realName ,nickName ,birthYear , birthDate , sex ,  marriageStatus ,  familyRank);
			return false;
		}
		//single 单身;celibate 独身;married 已婚;divorce 离异;widowed 丧偶;remarriage 在婚
		String [] correctMarriageStatus = new String[]{"single","celibate","married","divorce","widowed","remarriage"};
		if(StringUtils.isNotEmpty(marriageStatus) && !ArrayUtils.contains(correctMarriageStatus, marriageStatus)   ){
			logger.warn("婚姻状态错误  realName:{},nickName:{},birthYear:{},birthDate:{} ,sex:{} , marriageStatus:{} ,familyRank:{} ", 
					realName ,nickName ,birthYear , birthDate , sex ,  marriageStatus ,  familyRank);
			return false;
		}
		return true;
	}
	
	
}
