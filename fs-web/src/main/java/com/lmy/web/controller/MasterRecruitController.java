package com.lmy.web.controller;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.IDCardUtil;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.model.FsFileStore;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.dto.LoginCookieDto;
import com.lmy.core.service.impl.FileStoreServiceImpl;
import com.lmy.core.service.impl.FsZxCateQueryServiceImpl;
import com.lmy.core.service.impl.MasterQueryServiceImpl;
import com.lmy.core.service.impl.MasterRecruitServiceImpl;
import com.lmy.core.service.impl.UsrAidUtil;
import com.lmy.web.common.WebUtil;
/**
 * 申请成为风水师、完善个人资料(风水师)、服务设置 等
 * @author zhouzhaohua
 * @since 2017/04/04
 */
@Controller
public class MasterRecruitController {
	private static final Logger logger = Logger.getLogger(MasterRecruitController.class);
	@Autowired
	private MasterRecruitServiceImpl masterRecruitServiceImpl;
	@Autowired
	private MasterQueryServiceImpl masterQueryServiceImpl;
	@Autowired 
	private FileStoreServiceImpl fileStoreServiceImpl;
	@Autowired
	private FsZxCateQueryServiceImpl fsZxCateQueryServiceImpl;	
	/**
	 * 申请成为风水师 申请(资料提交)提交页   
	 */
	@RequestMapping(value="/usr/master/recruit/apply_nav")
	public String apply_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "masterInfoId" , required = false) Long masterInfoId
			,@RequestParam(value = "isFirstAfterApply" , required = false) String  isFirstAfterApply  //Y|N 
			){
		//可能导向不同页面 无申请记录 ； 有申请记录 申请中、审批通过|审批不通过  
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		List<FsMasterInfo> list = masterQueryServiceImpl.findBtCondition1(masterInfoId,loginDto.getUserId(), loginDto.getOpenId(), Arrays.asList("ing", "approved"), null);
		if(CollectionUtils.isEmpty(list)){
			return "/usr/master/recruit/apply_nav";
		}else{
			if(list.size()>1){
				logger.warn("记录条数不为1 usrId:"+loginDto.getUserId()+",size="+list.size());
			}
			FsMasterInfo lastOne = list.get( list.size() -1 );
			if(StringUtils.equals("approved", lastOne.getAuditStatus())){
				if(!UsrAidUtil.isPerfectPersonalData(lastOne)){
					modelMap.put("fsMasterInfo",    lastOne);
					return "/usr/master/recruit/apply_audit_approved_step1";
				}else{
					modelMap.put("fsMasterInfo",    lastOne);
					return "redirect:/usr/master/account";					
				}
			}
			else if(StringUtils.equals("ing", lastOne.getAuditStatus())){
				modelMap.put("isFirstAfterApply",    isFirstAfterApply);
				modelMap.put("fsMasterInfo",    lastOne);
				modelMap.put("mobileEnd4",    StringUtils.isNotEmpty(lastOne.getContactMobile()) ? lastOne.getContactMobile().substring( lastOne.getContactMobile().length()-4 ) :"");
				//申请结果展示页 //TODO 多个分支 使用 forward
				return "/usr/master/recruit/apply_result_show";				
			}
			else if(StringUtils.equals("refuse", lastOne.getAuditStatus())){
				return "/usr/master/recruit/apply_nav";
			}else{
				return WebUtil.failedResponse(response, "");
			}
		}
	}
	
	/**
	 * 申请成为风水师 审核 微信通知 查看
	 */
	@RequestMapping(value="/usr/master/recruit/apply_notice_view")
	public String apply_weixin_notice_view(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "masterInfoId" , required = true) long masterInfoId
			,@RequestParam(value = "from" , required = false) String fromWeiXinNotice  //weixin ; other
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		List<FsMasterInfo> list = masterQueryServiceImpl.findBtCondition1(masterInfoId,loginDto.getUserId(), loginDto.getOpenId(), null, null);
		if(CollectionUtils.isEmpty(list)){
			logger.warn("非法请求 masterInfoId:"+masterInfoId+", 响应空白页");
			return WebUtil.failedResponse(response,"");
		}
		FsMasterInfo masterInfo = list.get( list.size() -1 );
		modelMap.put("masterInfo", masterInfo) ;
		boolean isPerfectPersonalData =UsrAidUtil.isPerfectPersonalData(masterInfo);
		boolean isConfigSeriveCate = false;
		modelMap.put("isPerfectPersonalData", isPerfectPersonalData);
		if(isPerfectPersonalData){
			//前往完善个人资料页
			isConfigSeriveCate  = this.masterQueryServiceImpl.isConfigSeriveCate(loginDto.getUserId(), masterInfo.getId());
			modelMap.put("isConfigSeriveCate", isConfigSeriveCate);
		}
		//个人资料与 服务都已配置
		if(isPerfectPersonalData){
			logger.info("个人资料与 服务都已配置masterInfoId:"+masterInfoId +", 前往风水是首页" );
			return "forward:/usr/master/account";
		}
		return "/usr/master/recruit/apply_notice_view";
	}
	
	/** 	
	 * 申请成为风水师 提交页
	 */
	@RequestMapping(value="/usr/master/recruit/apply_submit",method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String apply_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "realName" , required = true) String realName,    							//证件上 姓名
			@RequestParam(value = "nickName" , required = false) String nickName,    						//用户昵称
			@RequestParam(value = "certImg1" , required = false) CommonsMultipartFile certImg1,   //证件照正面
			@RequestParam(value = "certImg2" , required = false) CommonsMultipartFile certImg2,   //证件照 反面
			@RequestParam(value = "liveAddress" , required = true) String liveAddress,   						//现居住地址
			@RequestParam(value = "certNo" , required = true) String certNo,				   						//证件号码
			@RequestParam(value = "contactMobile" , required = true) String contactMobile	,			//联系手机号码
			@RequestParam(value = "contactQq" , required = true) String contactQq	,						//联系qq号码
			@RequestParam(value = "contactWeixin" , required = true) String contactWeixin	,			//联系微信号码
			@RequestParam(value = "school" , required = false) String school	,									//所学门派
			@RequestParam(value = "experience" , required = false) String experience	,						//相关经历
			@RequestParam(value = "achievement" , required = false) String achievement	,				//取得主要成就
			@RequestParam(value = "goodAt" , required = false) String goodAt	,								//擅长领域
			@RequestParam(value = "profession" , required = false) String profession	,						//现职业
			@RequestParam(value = "isSignOther" , required = false) String isSignOther,						//是否已签约其他风水平台 , Y 有签约其他风水平台;N 未签约其他风水平台(雷门易专属)
			@RequestParam(value = "isFullTime" , required = false) String isFullTime								//是否专职 , Y;N
			){
		
		if(!CommonUtils.isChinese(realName)){
			logger.warn("姓名错误realName:"+realName);
			return JsonUtils.commonJsonReturn("0001", "姓名格式错误").toJSONString();
		}
		if(!IDCardUtil.isIDCard(certNo)){
			logger.warn("证件号码错误certNo:"+certNo);
			return JsonUtils.commonJsonReturn("0001", "证件号码错误").toJSONString();
		}
		if(	(certImg1==null || certImg1.getSize()<1)  || (certImg2==null || certImg2.getSize()<1)){
			logger.warn("证件照必填");
			return JsonUtils.commonJsonReturn("0001", "证件照必填").toJSONString();
		}
		if(StringUtils.isEmpty(contactMobile)){
			logger.warn("联系手机号码必填contactMobile:"+contactMobile);
			return JsonUtils.commonJsonReturn("0001", "手机号码").toJSONString();
		}
		if(!CommonUtils.checkForMobile(contactMobile)){
			logger.warn("联系手机号码格式错误contactMobile:"+contactMobile);
			return JsonUtils.commonJsonReturn("0001", "手机号码格式错误").toJSONString();
		}
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		//统计 申请中 与 申请通过的记录数 （不统计审批拒接掉的）
		Long applyRecordNum = this.masterQueryServiceImpl.statRecordNum1(loginDto.getUserId(),  Arrays.asList("ing", "approved"), null);
		if(applyRecordNum>1){
			logger.warn("已提交申请，请勿再次提交,applyRecordNum:"+applyRecordNum);
			return JsonUtils.commonJsonReturn("0002", "记录已存在").toJSONString();
		}
		Date now = new Date();
		FsMasterInfo masterInfo = new FsMasterInfo();
		masterInfo.setAchievement(achievement).setAuditStatus("ing").setServiceStatus("NOTING").setAuditTime(null);
		masterInfo.setBirthDate( IDCardUtil.getBirthMothDayIdNum(certNo) ).setBirthYear(	Integer.valueOf(IDCardUtil.getBirthYearIdNum(certNo))).setSex( IDCardUtil.getSexFromIdNum(certNo) )
		.setName(realName).setNickName(nickName)  .setCertNo(certNo).setCertType("0").setContactMobile(contactMobile).setContactQq(contactQq).setContactWeixin(contactWeixin)
		.setExperience(experience).setGoodAt(goodAt).setIsFullTime(isFullTime).setIsSignOther(isSignOther).setLiveAddress(liveAddress).setProfession(profession).setSchool(school)
		.setUsrId(loginDto.getUserId()).setWxOpenId(loginDto.getOpenId());
		masterInfo.setUpdateTime(now).setCreateTime(now);
		
		 FsFileStore files1 =  fileStoreServiceImpl.fileStore(certImg1, FileStoreServiceImpl.FileType.AUDIT , loginDto.getUserId());
		 FsFileStore files2 = fileStoreServiceImpl.fileStore(certImg2, FileStoreServiceImpl.FileType.AUDIT , loginDto.getUserId());
		 masterInfo.setCertImg1Url(files1!=null ? files1.getHttpUrl():null ).setCertImg2Url( files2 !=null ? files2.getHttpUrl():null );
		JSONObject applyResult = masterRecruitServiceImpl.applyMaster(masterInfo);
		JsonUtils.setBody(applyResult, "mobileEnd4", contactMobile.substring(  contactMobile.length() - 4 )) ;
		 return  applyResult.toJSONString() ;
	}

	/**
	 * 成为风水师 完善资料页
	 */
	@RequestMapping(value="/usr/master/recruit/apply_info_supply_nav")
	public String apply_info_supply_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "masterInfoId" , required = true) long masterInfoId
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		Long loginUsrId = WebUtil.getUserId(request);
		if (isMasterFired(loginUsrId)) {
			modelMap.put("error_msg", "您已与雷门易平台解约，您无法编辑个人资料");
			return "redirect:/common/error";
		}
		List<FsMasterInfo> list = masterQueryServiceImpl.findBtCondition1(masterInfoId,loginDto.getUserId(), loginDto.getOpenId(), null, null);
		if(CollectionUtils.isEmpty(list)){
			return WebUtil.failedResponse(response,"");
		}
		modelMap.put("masterInfo", list.get(0));
		return "/usr/master/recruit/apply_info_supply_nav";
	}
	
	/**
	 * 成为风水师 完善资料 提交 请求处理
	 */
	@RequestMapping(value="/usr/master/recruit/supply_submit",method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String supply_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "masterInfoId" , required = true) long masterInfoId,        						
			@RequestParam(value = "realName" , required = true) String realName,   						
			@RequestParam(value = "nickName" , required = true) String nickName,    						
			@RequestParam(value = "englishName" , required = false) String englishName,    				
			@RequestParam(value = "headImg" , required = false) CommonsMultipartFile headImg,    						
			@RequestParam(value = "workDate" , required = false) Date workDate,      							//从业开始时间 eg:2015/01/01		
			@RequestParam(value = "school" , required = false) String school	,									//所学门派
			@RequestParam(value = "experience" , required = false) String experience	,						//相关经历
			@RequestParam(value = "achievement" , required = false) String achievement	,				//取得主要成就
			@RequestParam(value = "goodAt" , required = false) String goodAt	,								//擅长领域
			@RequestParam(value = "profession" , required = false) String profession	,						//现职业
			@RequestParam(value = "isSignOther" , required = false) String isSignOther,						//是否已签约其他风水平台 , Y 有签约其他风水平台;N 未签约其他风水平台(雷门易专属)
			@RequestParam(value = "isFullTime" , required = false) String isFullTime								//是否专职 , Y;N
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		//统计 申请中 与 申请通过的记录数 （不统计审批拒接掉的）
		Long applyRecordNum = this.masterQueryServiceImpl.statRecordNum1(loginDto.getUserId(),  Arrays.asList("ing", "approved"), null);
		if(applyRecordNum>1){
			logger.warn("已提交申请，请勿再次提交applyRecordNum:"+applyRecordNum);
			return JsonUtils.commonJsonReturn("0002", "记录已存在").toJSONString();
		}
		Date now = new Date();
		FsMasterInfo masterInfoForUpdate = new FsMasterInfo();
		masterInfoForUpdate.setUpdateTime(now).setWorkDate(workDate).setId(masterInfoId);
		masterInfoForUpdate.setName(realName).setNickName(nickName).
		setExperience(experience).setGoodAt(goodAt).setIsFullTime(isFullTime).setIsSignOther(isSignOther).setProfession(profession).setSchool(school).setAchievement(achievement);
		if(headImg!=null && headImg.getSize()>0){
			FsFileStore files1 =  fileStoreServiceImpl.fileStore(headImg, FileStoreServiceImpl.FileType.HEADIMG , loginDto.getUserId());
			masterInfoForUpdate.setHeadImgUrl( files1!=null ? files1.getHttpUrl():null);
		}
		return this.masterRecruitServiceImpl.supplyInfo(masterInfoForUpdate, loginDto.getUserId()).toJSONString();
	}

	
	
	/**
	 * 前往 服务类别 设置 页
	 */
	@RequestMapping(value="/usr/master/recruit/service_cate_config_nav")
	public String service_config_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		//拉取所有可以配置的服务
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		Long loginUsrId = WebUtil.getUserId(request);
		if (isMasterFired(loginUsrId)) {
			modelMap.put("error_msg", "您已与雷门易平台解约，您无法编辑服务项目");
			return "redirect:/common/error";
		}
		modelMap.put("serviceCateList",  this.masterQueryServiceImpl.findForConfigServerStep1(loginDto.getUserId())  );
		return "/usr/master/recruit/service_cate_config_nav";
	}
	
	/**
	 * 服务设类别 置 提交
	 */
	@RequestMapping(value="/usr/master/recruit/service_cate_config_submit",method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String service_config_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="data" , required = true) String data  //[{"id":"","amt":"","status":"ON","fsZxCateId":"100002","fsMasterInfoId":"100001"},{"id":"","amt":"","status":"ON","fsZxCateId":"100003","fsMasterInfoId":"100001"}]
			){
		JSONArray jdata = JSONArray.parseArray( data);
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		return this.masterRecruitServiceImpl.configServiceInfo(jdata, loginDto.getUserId()).toJSONString() ;
	}

	
	/**
	 * 配置接单状态
	 */
	@RequestMapping(value="/usr/master/recruit/update_service_status",method={RequestMethod.POST})
	@ResponseBody
	public String update_service_status(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
												,@RequestParam(value="masterInfoId" , required = false) Long masterInfoId
												,@RequestParam(value="serviceStatus" , required = true) String serviceStatus  //当前服务状态 ING 服务中;NOTING 非服务状态
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		return this.masterRecruitServiceImpl.configOderTaking(masterInfoId, loginDto.getUserId(), serviceStatus).toJSONString();
	}
	
	private Boolean isMasterFired(Long loginUsrId) {
		FsMasterInfo master = masterQueryServiceImpl.findByUsrId(loginUsrId);
		return master.getServiceStatus().equals("FIRED");
	}
}
