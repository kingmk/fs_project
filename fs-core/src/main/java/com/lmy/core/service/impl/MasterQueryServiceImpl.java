package com.lmy.core.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsMasterCardDao;
import com.lmy.core.dao.FsMasterFansDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsMasterServiceCateDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderEvaluateDao;
import com.lmy.core.dao.FsOrderSettlementDao;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsMasterCard;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsMasterServiceCate;
import com.lmy.core.model.FsOrderSettlement;
import com.lmy.core.model.FsZxCate;
import com.lmy.core.model.dto.MasterServiceCateDto;
/**
 * 简单的 master 查询
 * @author fidel
 */
@Service
public class MasterQueryServiceImpl {
	private static final Logger logger = Logger.getLogger(MasterQueryServiceImpl.class);
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	private FsZxCateDao  fsZxCateDao;
	@Autowired 
	private FsMasterServiceCateDao fsMasterServiceCateDao;
	@Autowired
	private FsMasterFansDao fsMasterFansDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired 
	private FsOrderEvaluateDao fsOrderEvaluateDao;
	@Autowired
	private FsOrderSettlementDao fsOrderSettlementDao;
	@Autowired
	private FsMasterCardDao fsMasterCardDao;
	
	public List<FsMasterInfo> findBtCondition1(Long id , Long usrId,  String wxOpenId , List<String> auditStatusList , List<String> serviceStatusList){
		return fsMasterInfoDao.findBtCondition1(id,usrId, wxOpenId, auditStatusList, serviceStatusList);
	}
	
	/**
	 * 所有可配置(含已配置新)的服务, 含自定义
	 * @param usrId
	 * @return
	 */
	public List<MasterServiceCateDto> findForConfigServerStep1(Long usrId){
		List<FsZxCate>  zxCateList = fsZxCateDao.findZxCate2( 2l, "N", "EFFECT", usrId);
		List<FsMasterServiceCate>  masterServiceCateList = fsMasterServiceCateDao.findByUsrIdAndStatus(usrId, Arrays.asList("ON"));
		List<FsMasterInfo> masterInfoList = this.findBtCondition1(null, usrId, null, Arrays.asList("ing", "approved"), null);
		if(CollectionUtils.isEmpty(masterInfoList) || masterInfoList.size()!=1){
			logger.warn("记录数不为1 usrId："+usrId+", size="+( CollectionUtils.isNotEmpty(masterInfoList)  ? masterInfoList.size() :0) );
		}
		FsMasterInfo masterInfo = CollectionUtils.isNotEmpty(masterInfoList) ? masterInfoList.get( masterInfoList.size() -1 ) : null;
		Map<Long,FsMasterServiceCate> map = this.convert(masterServiceCateList);
		List<MasterServiceCateDto> resultList = new ArrayList<MasterServiceCateDto>();
		for(FsZxCate bean :  zxCateList){
			resultList.add(   findForConfigServer_bulidresult(bean, map.get( bean.getId() ), usrId , masterInfo!=null ? masterInfo.getId() : null )   );
		}
		return resultList;
	}
	
	public Long statRecordNum1(Long usrId , List<String> auditStatusList , List<String> serviceStatusList){
		return this.fsMasterInfoDao.statRecordNum1(usrId, auditStatusList, serviceStatusList);
	}

	private MasterServiceCateDto findForConfigServer_bulidresult(FsZxCate zxCate , FsMasterServiceCate masterServiceCate,Long usrId ,Long masterInfoId ){
		
		MasterServiceCateDto bean = new MasterServiceCateDto();
		bean.setAmt(masterServiceCate!=null ? masterServiceCate.getAmt() : zxCate.getSuggestAmt());
		bean.setFsMasterInfoId(masterInfoId);
		bean.setFsZxCateId(zxCate.getId());
		bean.setFsZxCateParentId(zxCate.getParentId());
		bean.setFsZxCateParentName(zxCate.getParentName());
		bean.setId( masterServiceCate!=null ?masterServiceCate.getId():null );
		bean.setIsPlatRecomm(masterServiceCate!=null ? masterServiceCate.getIsPlatRecomm():null);
		bean.setName(  zxCate.getName() );
		bean.setPlatRecommTime( masterServiceCate!=null ? masterServiceCate.getPlatRecommTime():null );
		bean.setStatus(masterServiceCate!=null ? masterServiceCate.getStatus():null);
		bean.setSuggestAmt( zxCate.getSuggestAmt() );
		bean.setUsrId(  usrId );
		return bean;
	}
	/**key fs_zx_cate_id关联表fs_master_info.id  **/
	private Map<Long,FsMasterServiceCate> convert(List<FsMasterServiceCate>  masterServiceCateList){
		Map<Long,FsMasterServiceCate> map = new HashMap<Long,FsMasterServiceCate>();
		if(CollectionUtils.isEmpty(masterServiceCateList)){
			return map;
		}
		for(FsMasterServiceCate bean :  masterServiceCateList){
			map.put( bean.getFsZxCateId() , bean);
		}
		return map;
	}
	
	/** 是否配置的 服务类别 **/
	public boolean isConfigSeriveCate(Long usrId , Long  masterInfoId){
		Long num = this.fsMasterServiceCateDao.statServiceRecordNum(usrId, masterInfoId, null);
		if(num>0){
			return true;
		}else{
			return false;	
		}
	}
	public JSONObject findMasterServiceCateInfo(long masterUsrId){
		List<FsMasterInfo> masterInfoList =  findBtCondition1(null,masterUsrId,null, Arrays.asList("ing", "approved"), null);
		if(CollectionUtils.isEmpty(masterInfoList) || masterInfoList.size()!=1){
			return JsonUtils.commonJsonReturn("0001", "参数错误");			
		}
		List<FsMasterServiceCate> serviceCateList  = fsMasterServiceCateDao.findByUsrIdAndStatus(masterUsrId, Arrays.asList("ON"));
		JSONObject result = JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "serviceCateList", getMasterCurrServiceCateInfo(masterUsrId, serviceCateList));
		JsonUtils.setBody(result, "serviceCateSize", CollectionUtils.isNotEmpty(serviceCateList)  ?serviceCateList.size():0);
		JsonUtils.setBody(result, "masterUsrId", masterUsrId);
		return result;
	}
	
	
	public JSONObject masterPersonalHomePage(long masterUsrId){
		List<FsMasterInfo> masterInfoList =  findBtCondition1(null,masterUsrId,null, Arrays.asList("ing", "approved"), null);
		if(CollectionUtils.isEmpty(masterInfoList) || masterInfoList.size()!=1){
			return JsonUtils.commonJsonReturn("0001", "参数错误");			
		}
		List<FsMasterServiceCate> serviceCateList  = fsMasterServiceCateDao.findByUsrIdAndStatus(masterUsrId, Arrays.asList("ON"));
		FsMasterInfo masterInfo = masterInfoList.get(0);
		
		JSONObject body = new JSONObject(true);
		body.put("masterName", masterInfo.getName()) ;
		body.put("materHeadImgUrl", masterInfo.getHeadImgUrl()) ;
		body.put("sex",  masterInfo!=null ? ( masterInfo.getSex() )  : null );
		body.put("isCertificated", StringUtils.isEmpty(masterInfo.getCertNo()) ? "Y":"N") ;  // 是否实名认证 Y|N
		body.put("isSignOther", masterInfo.getIsSignOther());  // 是否 在其他平台签约 Y(非独家);N(独家)
		body.put("isTranSecuried", "Y"); //是否担保交易
		Map  avgScore = 	this.fsOrderEvaluateDao.statMasterAvgScore(null, masterUsrId, null);
		Double respSpeed = UsrAidUtil.getEvaluateFromMap(avgScore , "resp_speed",0d)	;	
		Double majorLevel = UsrAidUtil.getEvaluateFromMap(avgScore , "major_level",0d)	;	
		Double serviceAttitude = UsrAidUtil.getEvaluateFromMap(avgScore , "service_attitude",0d)	;	
		body.put("score",  (respSpeed+majorLevel +serviceAttitude)/3d ) ;  		// 评分 
		body.put("scoreDesc",  CommonUtils.numberFormat( (respSpeed+majorLevel +serviceAttitude)/3d  , "###,##0.00", "0.00") ) ;  		// 评分 
		body.put("orderTotalNum", this.fsOrderDao.statOrderNum(masterUsrId, null,  OrderAidUtil.getMasterLeiJitatus() )) ; 	
		body.put("fansTotalNum", fsMasterFansDao.statEffectFansNum(masterInfo.getUsrId())) ; 											//粉丝数 
		body.put("masterInfoId", masterInfo.getId()) ; 							
		body.put("serviceCateList",  getMasterCurrServiceCateInfo(masterUsrId, serviceCateList) ) ;  			//服务项目
		body.put("serviceCateSize", CollectionUtils.isNotEmpty(serviceCateList)  ?serviceCateList.size():0) ;  			//服务项目数
		//~~~大师介绍 begin 
		body.put("school", StringEscapeUtils.escapeHtml4( masterInfo.getSchool())) ;  					//    所学门派 
		body.put("experience", StringEscapeUtils.escapeHtml4( masterInfo.getExperience())) ;  		//    相关经历	
		body.put("achievement", StringEscapeUtils.escapeHtml4( masterInfo.getAchievement())) ;  //		取得主要成就
		body.put("goodAt",StringEscapeUtils.escapeHtml4(  masterInfo.getGoodAt())) ;  				//		擅长领域 
		body.put("evaluateTotal",  this.fsOrderEvaluateDao.statEvaluateNum(null, masterUsrId, null) ); //评价总数
		body.put("respSpeedAvgScore",  respSpeed );   //响应速度平均分
		body.put("majorLevelAvgScore", majorLevel );  //专业水平平均分
		body.put("serviceAttitudeAvgScore", serviceAttitude );  //服务态度平均分
		//用户评价 
		JSONObject result = JsonUtils.commonJsonReturn(); 
		result.put("body", body);
		return result;
	}
	
	private JSONArray getMasterCurrServiceCateInfo(long masterUsrId,List<FsMasterServiceCate> serviceCateList){
		JSONArray list = new JSONArray();
		if(CollectionUtils.isEmpty(serviceCateList)){
			return list;
		}
		List<Long> cateIds = new ArrayList<Long>();
		for(FsMasterServiceCate bean :  serviceCateList){
			if(!cateIds.contains( bean.getFsZxCateId() )){
				cateIds.add(bean.getFsZxCateId());
			}
		}
		Map<Long, FsZxCate>  cateIdsMap = this.fsZxCateDao.findByIdsForResutMap(cateIds);
		Map<Long,Long> cateIsNum = this.fsOrderDao.statSuccNumBySellerUsrIdWithGroupByCateIds(masterUsrId, cateIds);
		for(FsMasterServiceCate  bean : serviceCateList){
			JSONObject dataOne = new JSONObject(true);
			dataOne.put("cateParentId",  cateIdsMap.get( bean.getFsZxCateId() ) !=null ? cateIdsMap.get( bean.getFsZxCateId() ).getParentId() : ""  );
			dataOne.put("cateParentName",  cateIdsMap.get( bean.getFsZxCateId() ) !=null ? cateIdsMap.get( bean.getFsZxCateId() ).getParentName() : ""  );
			dataOne.put("cateName",  bean.getName());
			dataOne.put("amt",  bean.getAmt());
			dataOne.put("serllerNum",  cateIsNum.get( bean.getFsZxCateId() )  != null ? cateIsNum.get( bean.getFsZxCateId() ) : 0);
			dataOne.put("id",  bean.getId());
			dataOne.put("cateId",  bean.getFsZxCateId());
			dataOne.put("masterInfoId",  bean.getFsMasterInfoId());
			list.add(dataOne);
		}
		return list;
	}
	
	private MasterServiceCateDto getCurrMasterServiceCate(List<MasterServiceCateDto> serviceCateDtoList ,  long cateId){
		if(CollectionUtils.isEmpty(serviceCateDtoList)){
			return null;
		}
		for(MasterServiceCateDto bean :  serviceCateDtoList){
			if(bean.getFsZxCateId().equals(cateId )){
				return bean;
			}
		}
		return null;
	}
	
	
	private List<MasterServiceCateDto> build(List<FsMasterServiceCate> serviceCateList){
		if(CollectionUtils.isEmpty(serviceCateList)){
			return null;
		}
		List<Long> zxCateIds = new ArrayList<Long>();
		for(FsMasterServiceCate serviceCateBean :  serviceCateList){
			if( !zxCateIds.contains( serviceCateBean.getFsZxCateId()  ) ){
				zxCateIds.add(  serviceCateBean.getFsZxCateId()   );
			}
		}
		List<FsZxCate> zxCateList = this.fsZxCateDao.findByIds(zxCateIds);
		Map<Long,FsZxCate> idBeanMap = new HashMap<Long,FsZxCate>();
		
		for(FsZxCate bean :  zxCateList){
			if( !idBeanMap.containsKey( bean.getId() ) ){
				idBeanMap.put(bean.getId(), bean);
			}
		}
		 List<MasterServiceCateDto> resultList = new ArrayList<MasterServiceCateDto>();
		 for(FsMasterServiceCate serviceCateBean :  serviceCateList){
			 resultList.add(  bulidresult(idBeanMap.get(serviceCateBean.getFsZxCateId()), serviceCateBean, serviceCateList.get(0).getUsrId(), serviceCateList.get(0).getId())  );
		 }
		return resultList;
	}
	
	private MasterServiceCateDto bulidresult(FsZxCate zxCate , FsMasterServiceCate masterServiceCate,Long masterUsrId ,Long masterInfoId ){
		MasterServiceCateDto bean = new MasterServiceCateDto();
		bean.setAmt(masterServiceCate!=null ? masterServiceCate.getAmt() : zxCate.getSuggestAmt());
		bean.setFsMasterInfoId(masterInfoId);
		bean.setFsZxCateId(zxCate.getId());
		bean.setFsZxCateParentId(zxCate.getParentId());
		bean.setFsZxCateParentName(zxCate.getParentName());
		bean.setId( masterServiceCate!=null ?masterServiceCate.getId():null );
		bean.setIsPlatRecomm(masterServiceCate!=null ? masterServiceCate.getIsPlatRecomm():null);
		bean.setName(  zxCate.getName() );
		bean.setPlatRecommTime( masterServiceCate!=null ? masterServiceCate.getPlatRecommTime():null );
		bean.setStatus(masterServiceCate!=null ? masterServiceCate.getStatus():null);
		bean.setSuggestAmt( zxCate.getSuggestAmt() );
		bean.setUsrId(  masterUsrId );
		return bean;
	}
	
	/** 普通用户视角） | 大师主页 - 大师介绍页 
	 * @param enterType afterSupplySubmit ; afterServiceConfigSubmit,sourceCateIntro,sourceFollowList
	 * */
	public JSONObject commonIntro(String enterType ,long masterInfoId , Long cateId , long loginUsrId){
		FsMasterInfo masterInfo = this.fsMasterInfoDao.findById(masterInfoId);
		if("sourceCateIntro".equals(enterType) && cateId == null){
			logger.info( "masterInfoId="+masterInfoId+"  , cateId="+cateId+",参数cateId未空"  );
			return JsonUtils.commonJsonReturn("0001", "参数错误");			
		}
		List<FsMasterServiceCate> serviceCateList = 	this.fsMasterServiceCateDao.findByMasterInfoIdAndCateId(masterInfoId, cateId, Arrays.asList("ON")) ;	
		if(CollectionUtils.isEmpty(serviceCateList) ){
			logger.info( "masterInfoId="+masterInfoId+"  , cateId="+cateId+",数据错误"  );
			return JsonUtils.commonJsonReturn("0002", "数据错误");
		}
		//build result 
		JSONObject body = new JSONObject(true);
		body.put("masterUsrId", masterInfo.getUsrId()) ;
		body.put("masterInfoId", masterInfo.getId()) ;
		body.put("serviceStatus", masterInfo.getServiceStatus()); 
		body.put("masterNickName",  UsrAidUtil.getMasterNickName(masterInfo, null, ""));
		body.put("masterHeadImgUrl",  masterInfo!=null ? ( masterInfo.getHeadImgUrl() )  : null );
		body.put("sex",  masterInfo!=null ? ( masterInfo.getSex() )  : null );
		body.put("isCertificated", StringUtils.isNotEmpty(masterInfo.getCertNo()) ? "Y":"N") ;  // 是否实名认证 Y|N
		body.put("isSignOther", masterInfo.getIsSignOther()) ;  													// 是否独家 Y(非独家)|N (独家)
		body.put("isTranSecuried", "Y"); //是否担保交易
		body.put("workBeginDate", masterInfo.getWorkDate());  // 工作起始日期
		body.put("workYearStr",  UsrAidUtil.getWorkYearStr(masterInfo.getWorkDate()) );  // 从业年限
		Map  avgScore = 	this.fsOrderEvaluateDao.statMasterAvgScore(null, masterInfo.getUsrId(), null);
		Double respSpeed = UsrAidUtil.getEvaluateFromMap(avgScore , "resp_speed",0d)	;	
		Double majorLevel = UsrAidUtil.getEvaluateFromMap(avgScore , "major_level",0d)	;	
		Double serviceAttitude = UsrAidUtil.getEvaluateFromMap(avgScore , "service_attitude",0d)	;	
		//body.put("score",  CommonUtils.numberFormat( (respSpeed+majorLevel +serviceAttitude)/3d  , "###,##0.00", "0.00") ) ;  		// 评分 
		body.put("score",  (respSpeed+majorLevel +serviceAttitude)/3d ) ;  		// 评分 
		body.put("scoreDesc",  CommonUtils.numberFormat( (respSpeed+majorLevel +serviceAttitude)/3d  , "###,##0.00", "0.00") ) ;  		// 评分 
		body.put("respSpeedAvgScore",  respSpeed );   //响应速度平均分
		body.put("majorLevelAvgScore", majorLevel );  //专业水平平均分
		body.put("serviceAttitudeAvgScore", serviceAttitude );  //服务态度平均分
		body.put("orderTotalNum", this.fsOrderDao.statOrderNum(masterInfo.getUsrId(), null,  OrderAidUtil.getMasterLeiJitatus() )) ; 	
		body.put("fansTotalNum", fsMasterFansDao.statEffectFansNum(masterInfo.getUsrId())) ; 											//粉丝数 
		body.put("evaluateTotal",  this.fsOrderEvaluateDao.statEvaluateNum(null, masterInfo.getUsrId(), null) ); //评价总数
		body.put("isFollowed",  fsMasterFansDao.isCurrFollowed(loginUsrId, masterInfo.getUsrId()) == true ? 'Y' :'N'  ) ; 	//是否已关注 Y|N
		body.put("masterInfoId", masterInfoId) ; 		
		List<MasterServiceCateDto> serviceCateDtoList = build(serviceCateList);
		if(cateId!=null){
			body.put("curServiceCateInfo",  getCurrMasterServiceCate(serviceCateDtoList, cateId) ) ;  			//当前服务类别 		
		}
		//body.put("serviceCateList", serviceCateDtoList) ;  					//当前所有服务类别 		
		//~~~大师介绍 begin 
		body.put("school",  StringEscapeUtils.escapeHtml4( masterInfo.getSchool() )) ;  					//    所学门派 
		body.put("experience", StringEscapeUtils.escapeHtml4( masterInfo.getExperience() )) ;  		//    相关经历	
		body.put("achievement", StringEscapeUtils.escapeHtml4( masterInfo.getAchievement() )) ;  //		取得主要成就
		body.put("goodAt", StringEscapeUtils.escapeHtml4( masterInfo.getGoodAt() ) ) ;  				//		擅长领域 
		//~~~大师介绍 end
		JSONObject result = JsonUtils.commonJsonReturn(); 
		result.put("body", body);
		return result;
	}
	/** 大师端 我的账号
	 * @param enterType afterSupplySubmit ; afterServiceConfigSubmit,sourceCateIntro,sourceFollowList
	 * */
	public JSONObject masterAct(long masterUsrId){
		List<FsMasterInfo> masterInfoList =  findBtCondition1(null,masterUsrId,null, Arrays.asList("ing", "approved"), null);
		if(CollectionUtils.isEmpty(masterInfoList) || masterInfoList.size()>1){
			logger.info( "masterUsrId="+masterUsrId+",数据错误 masterInfoList.size="  +CommonUtils.getListSize(masterInfoList)+",将会重定向到普通用户页");
			return JsonUtils.commonJsonReturn("0001", "数据错误");
		}
		FsMasterInfo masterInfo = masterInfoList.get(0);
		List<FsMasterServiceCate> serviceCateList = this.fsMasterServiceCateDao.findByUsrIdAndStatus(masterUsrId, Arrays.asList("ON"))  ;
		//build result 
		JSONObject body = new JSONObject(true);
		body.put("masterInfoId", masterInfo.getId()) ;
		body.put("masterName",   masterInfo.getName()) ;
		body.put("masterNickName", UsrAidUtil.getMasterNickName(masterInfo, null, "")) ;
		body.put("materHeadImgUrl", masterInfo.getHeadImgUrl()) ;
		body.put("auditStatus", masterInfo.getAuditStatus()) ;  //审批/申请状态 ing 审批中;approved 审批通过;refuse 审批拒绝(不通过)
		body.put("serviceStatus", masterInfo.getServiceStatus()) ;  //当前服务状态 ING 服务中;NOTING 非服务状态
		body.put("isCertificated", StringUtils.isEmpty(masterInfo.getCertNo()) ? "Y":"N") ;  // 是否实名认证 Y|N
		body.put("isSignOther", masterInfo.getIsSignOther());  // 是否 在其他平台签约 Y(非独家);N(独家)
		body.put("isTranSecuried", "Y"); //是否担保交易
		FsOrderSettlement statSettlement = this.fsOrderSettlementDao.stat1(masterUsrId, Arrays.asList("succ"));
		//道账税前收入
		body.put("dzsqsr", statSettlement==null ?  0  :  ( statSettlement.getRealArrivalAmt()+statSettlement.getPersonalIncomeTaxRmbAmt()) ) ;  	  //单位分					
		//累计导账收入
		body.put("ljdzsr", statSettlement==null ?  0 : statSettlement.getOrderTotalPayRmbAmt() ) ;  						// 单位分
		
		Double score = fsOrderEvaluateDao.statMasterAvgScore2(masterUsrId);
		body.put("score", score) ;  																																	// 评分  
		body.put("scoreDesc", CommonUtils.numberFormat(score, "###,##0.00", "0.00")) ;  											// 评分 格式化  
		body.put("currServiceCateNum", CollectionUtils.isNotEmpty(serviceCateList) ? serviceCateList.size(): 0) ; 				//我的服务项目
		body.put("isSetServiceList", CollectionUtils.isNotEmpty(serviceCateList) ? "Y": "N") ; 											// 是否设置了服务项目
		body.put("isPerfectPersonalData", UsrAidUtil.isPerfectPersonalData(masterInfo) ? "Y":"N") ; 											//是否完善了个人资料
		
		FsMasterCard fsMasterCard = fsMasterCardDao.findByMasterId(masterUsrId);
		body.put("hasCard", Boolean.toString(fsMasterCard != null));
		JSONObject result = JsonUtils.commonJsonReturn(); 
		result.put("body", body);
		return result;
	}
	
	public JSONObject saveMasterCard(FsMasterCard masterCard){
		try{
			if(masterCard == null || masterCard.getMasterUsrId() ==null){
				return JsonUtils.commonJsonReturn("0001","数据错误");
			}
			FsMasterCard beanTmp = fsMasterCardDao.findByMasterId(masterCard.getMasterUsrId());
			if(beanTmp == null){
				masterCard.setCreateTime(new Date());
				fsMasterCardDao.insert(masterCard);
			} else {
				masterCard.setId(beanTmp.getId());
				masterCard.setUpdateTime(new Date());
				fsMasterCardDao.update(masterCard);
			}
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("masterCard:"+masterCard,e);
			return JsonUtils.commonJsonReturn("9999","系统异常");
		}
	}
	
	public JSONObject masterCard(Long masterUsrId) {

		JSONObject result = JsonUtils.commonJsonReturn(); 
		FsMasterCard masterCard= this.fsMasterCardDao.findByMasterId(masterUsrId);
		JSONObject body = new JSONObject(true);
		if (masterCard != null) {
			body.put("masterUsrId", masterCard.getMasterUsrId());
			body.put("holderName", masterCard.getHolderName());
			body.put("bankName", masterCard.getBankName());
			body.put("bankNo", masterCard.getBankNo());
			body.put("province", masterCard.getProvince());
			body.put("city", masterCard.getCity());
		}

		result.put("body", body);
		return result;
	}
	
	public FsMasterInfo findByMasterInfoId(Long masterInfoId){
		return this.fsMasterInfoDao.findById(masterInfoId);
	}
}