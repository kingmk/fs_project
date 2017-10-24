package com.lmy.core.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.common.utils.SortListUtil;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderEvaluateDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsMasterServiceCate;
import com.lmy.core.model.dto.MasterShortDto1;
/**
 * 寻老师
 * @author fidel
 * @since 2017/05/01
 */
@Service
public class SearchMasterServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(SearchMasterServiceImpl.class);
	@Autowired
	private com.lmy.core.dao.FsMasterServiceCateDao fsMasterServiceCateDao;
	@Autowired
	private FsMasterInfoDao  fsMasterInfoDao;
	@Autowired 
	private FsOrderEvaluateDao fsOrderEvaluateDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	
	public JSONObject search(String isPlatRecomm,Long zxCateId,String orderBy,int currentPage,int perPageNum){
		String cacheKey = CacheConstant.FS_SEARCH_MASTER +"isPlatRecomm:"+isPlatRecomm+",zxCateId:"+zxCateId+",orderBy:"+orderBy+",currentPage:"+currentPage+",perPageNum:"+perPageNum;
		long begin = System.currentTimeMillis();
		JSONObject result = (JSONObject)  RedisClient.get(cacheKey);
		if(result!=null){
			//long end = System.currentTimeMillis();
			//logger.info("缓存命中 查询条件:"+cacheKey+",耗时:" + (end - begin)+"毫秒");
			return result;
		}
		result = search2(isPlatRecomm, zxCateId, orderBy, currentPage, perPageNum);
		long end = System.currentTimeMillis();
		if( (end - begin) > 2 * 1000 ){
			logger.warn("缓存未命中 db 查询条件:"+cacheKey+",耗时:" + (end - begin)+"毫秒");
		}
		if(!JsonUtils.codeEqual(result, "9999")){
			 RedisClient.set(cacheKey, result, 10 * 60);
		}
		return result;
	}
	
	/**
	 * 筛选和推荐互斥， 排序互斥，但筛选或推荐 和排序可共存
	 * @param platRecomm   推荐 Y|N
	 * @param zxCateId 筛选
	 * @param orderBy orderNumDesc,orderNumAsc; priceDesc,priceAsc, evaluateScoreDesc,evaluateScoreAsc
	 * @return
	 */
	 JSONObject search2(String isPlatRecomm,Long zxCateId,String orderBy,int currentPage,int perPageNum){
		//logger.info("isPlatRecomm:{},zxCateId:{},orderBy:{},currentPage:{},perPageNum:{}", isPlatRecomm , zxCateId , orderBy , currentPage ,perPageNum );
		try{
			if(	StringUtils.isEmpty(orderBy) || 	StringUtils.equals("priceDesc", orderBy)  ||   StringUtils.equals("priceAsc", orderBy)   ){
				if(StringUtils.isEmpty(orderBy)){
					orderBy = "priceAsc";
				}
				//取所有数据 , group by order by 会有问题.
				List<FsMasterServiceCate> list = this.fsMasterServiceCateDao.findOneOrderByPriceGroupByUsrId(null,Arrays.asList("ON"), (StringUtils.equals("Y", isPlatRecomm)  ?"Y":null ), zxCateId, orderBy, 0, 2000);
				if(CollectionUtils.isEmpty(list)){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				if(currentPage * perPageNum> list.size()  ){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				List<FsMasterServiceCate> subList = list.subList(  currentPage * perPageNum , Math.min(currentPage * perPageNum + perPageNum , list.size() ));
				return buildResut1(subList);
			}
			else if(  StringUtils.equals("orderNumDesc", orderBy)  ||   StringUtils.equals("orderNumAsc", orderBy)   ){
				//logger.info("isPlatRecomm:{},zxCateId:{},orderBy:{},currentPage:{},perPageNum:{}", isPlatRecomm , zxCateId , orderBy , currentPage ,perPageNum );
				//查询到所有的老师
				List<MasterShortDto1> masterShortDtoList =  this.fsMasterInfoDao.findShortInfo1("approved", "ON",StringUtils.equals("Y", isPlatRecomm) ?"Y":null  ,zxCateId);
				if(CollectionUtils.isEmpty(masterShortDtoList)){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				//统计所有老师 订单数目
				List<Map> usrIdOrderNumList = 	this.fsOrderDao.statAllSellerUsrOrderNum1(StringUtils.equals("Y", isPlatRecomm) ?"Y":null  ,zxCateId);
				_build1(masterShortDtoList, usrIdOrderNumList);
				//内存排序
				SortListUtil.sort(masterShortDtoList, ArrayUtils.toArray("serviceStatus","numberSortField1") , ArrayUtils.toArray(SortListUtil.ASC,StringUtils.equals("orderNumDesc", orderBy) ? SortListUtil.DESC:SortListUtil.ASC ));
				//内存分页 begin
				if(currentPage * perPageNum> masterShortDtoList.size()  ){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				List<MasterShortDto1> masterShortDtoSubList = masterShortDtoList.subList(  currentPage * perPageNum , Math.min(currentPage * perPageNum + perPageNum , masterShortDtoList.size() ));
				if(CollectionUtils.isEmpty(masterShortDtoSubList)){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				//内存分页 结束
				//提取 usrIds
				List<Long> usrIds =  _getUsrIdsFromSubList(masterShortDtoSubList) ;  
				List<FsMasterServiceCate> list  = this.fsMasterServiceCateDao.findOneOrderByPriceGroupByUsrId(usrIds,Arrays.asList("ON"), (StringUtils.equals("Y", isPlatRecomm)  ?"Y":null ), zxCateId, "priceAsc", 0, perPageNum);
				return buildResut1(usrIds, list);
			}
			else if(  StringUtils.equals("evaluateScoreDesc", orderBy)  ||   StringUtils.equals("evaluateScoreAsc", orderBy)   ){
				//logger.info("isPlatRecomm:{},zxCateId:{},orderBy:{},currentPage:{},perPageNum:{}", isPlatRecomm , zxCateId , orderBy , currentPage ,perPageNum );
				//查询到所有的老师
				List<MasterShortDto1> masterShortDtoList =  this.fsMasterInfoDao.findShortInfo1("approved", "ON",StringUtils.equals("Y", isPlatRecomm) ?"Y":null  ,zxCateId);
				if(CollectionUtils.isEmpty(masterShortDtoList)){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				//统计所有老师的评价
				List<Map> usrIdEvaluateNumList = this.fsOrderEvaluateDao.statAllMasterScore1(StringUtils.equals("Y", isPlatRecomm) ?"Y":null  ,zxCateId);
				_build1(masterShortDtoList, usrIdEvaluateNumList);
				//内存排序
				SortListUtil.sort(masterShortDtoList, ArrayUtils.toArray("serviceStatus","numberSortField1") , ArrayUtils.toArray(SortListUtil.ASC,StringUtils.equals("evaluateScoreDesc", orderBy) ? SortListUtil.DESC:SortListUtil.ASC ));
				//内存分页 begin
				if(currentPage * perPageNum> masterShortDtoList.size()  ){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				List<MasterShortDto1> masterShortDtoSubList = masterShortDtoList.subList(  currentPage * perPageNum , Math.min(currentPage * perPageNum + perPageNum , masterShortDtoList.size() ));
				if(CollectionUtils.isEmpty(masterShortDtoSubList)){
					return JsonUtils.commonJsonReturn("1000", "查无数据");
				}
				//内存分页 结束
				//提取 usrIds
				List<Long> usrIds =  _getUsrIdsFromSubList(masterShortDtoSubList) ;  // this.fsOrderEvaluateDao.findUsrIdListOrderByScore((StringUtils.equals("Y", isPlatRecomm) ?"Y":null ),zxCateId, orderBy, currentPage, perPageNum);
				List<FsMasterServiceCate> list  = this.fsMasterServiceCateDao.findOneOrderByPriceGroupByUsrId(usrIds,Arrays.asList("ON"), (StringUtils.equals("Y", isPlatRecomm)  ?"Y":null ), zxCateId, "priceAsc", 0, perPageNum);
				return buildResut1(usrIds, list);
			}else{
				return JsonUtils.commonJsonReturn("0001", "查询条件错误");	
			}
		}catch(Exception e){
			logger.error("isPlatRecomm:{},zxCateId:{},orderBy:{},currentPage:{},perPageNum:{}", isPlatRecomm , zxCateId , orderBy , currentPage ,perPageNum);
			logger.error("查询错误", e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");			
		}
	}
	
	private List<Long> _getUsrIdsFromSubList(List<MasterShortDto1> masterShortDtoSubList){
		List<Long> usrIds = new ArrayList<Long>();
		for( MasterShortDto1 bean : masterShortDtoSubList){
			usrIds.add(bean.getUsrId());
		}
		return usrIds;
	}
	
	private void _build1(List<MasterShortDto1> masterShortDtoList , List<Map> usrIdOrderNumList){
		if(CollectionUtils.isEmpty(masterShortDtoList) || CollectionUtils.isEmpty(usrIdOrderNumList)){
			return ;
		}
		Map<Long,Double> usrIdOrderNumMap = new HashMap<Long,Double>();
		Long _usrId = null;
		Double _orderNum = null;
		for(Map map : usrIdOrderNumList){
			_usrId = Long.valueOf( map.get("seller_usr_id").toString() );
			_orderNum = map.get("num")!=null ? Double.valueOf( map.get("num").toString() )  : 0l;
			if( !usrIdOrderNumMap.containsKey(_usrId) ){
				usrIdOrderNumMap.put(_usrId, _orderNum);
			}
		}
		for(MasterShortDto1 bean :  masterShortDtoList){
			bean.setNumberSortField1( usrIdOrderNumMap.get( bean.getUsrId() ) !=null ?  usrIdOrderNumMap.get( bean.getUsrId() )  : 0);
		}
	}
	
	
	private JSONObject buildResut1(List<Long> usrIdList,List<FsMasterServiceCate> list){
		if(CollectionUtils.isEmpty(usrIdList) || CollectionUtils.isEmpty(list) ){
			logger.warn("usrIdList:"+ArrayUtils.toString(usrIdList)+",list.size="+CommonUtils.getListSize(list));
			return JsonUtils.commonJsonReturn("1000", "查无数据");
		}
		List<FsMasterInfo> masterInfoList =  fsMasterInfoDao.findByUsrIds2(usrIdList,"approved",null);
		List<Map> orderEvaluateList =  this.fsOrderEvaluateDao.statMasterAvgScore3(usrIdList);
		Map<Long,FsMasterInfo> usrIdMasterInfoMap = this.convertMasterInfo(masterInfoList);
		Map<Long,Map> usrIdOrderEvaluate =    this.convertOrderEvaluate(orderEvaluateList);
		Map<Long, FsMasterServiceCate> usrIdMasterServiceCateMap = this.convertMasterServiceCate2(list);
		JSONArray dataList = new JSONArray();
		for( Long usrId : usrIdList ){
			FsMasterServiceCate bean = usrIdMasterServiceCateMap.get(usrId);
			if(bean !=null){
				dataList.add(this.buildSingeOne(usrIdMasterInfoMap, usrIdOrderEvaluate, bean));				
			}else{
				logger.warn("usrId:"+usrId+",usrIdList:"+ArrayUtils.toString(usrIdList));
			}
		}
		JSONObject result = JsonUtils.commonJsonReturn(); 
		JsonUtils.setBody(result, "data", dataList);
		JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(dataList) ? dataList.size() : 0);
		return result;
	}
	
	/**  KEY usrId **/
	private Map<Long, FsMasterServiceCate> convertMasterServiceCate2(List<FsMasterServiceCate> list){
		Map<Long, FsMasterServiceCate> map = new HashMap<Long,FsMasterServiceCate>();
		if(CollectionUtils.isEmpty(list)){
			return map;
		}
		for(FsMasterServiceCate bean :  list){
			if( !map.containsKey(bean.getUsrId()) ){
				map.put(bean.getUsrId(), bean);
			}
		}
		return map;
	}
	private JSONObject buildResut1(List<FsMasterServiceCate> list){
		if(CollectionUtils.isEmpty(list)){
			return JsonUtils.commonJsonReturn("1000", "查无数据");
		}
		List<Long> masterUsrIds  = getMasterUsrIds(list);
		List<FsMasterInfo> masterInfoList = fsMasterInfoDao.findByUsrIds2(masterUsrIds,"approved",null);
		List<Map> orderEvaluateList =  this.fsOrderEvaluateDao.statMasterAvgScore3(masterUsrIds);
		Map<Long,FsMasterInfo> usrIdMasterInfoMap = this.convertMasterInfo(masterInfoList);
		Map<Long,Map> usrIdOrderEvaluate =    this.convertOrderEvaluate(orderEvaluateList);
		JSONArray dataList = new JSONArray();
		for(FsMasterServiceCate bean :  list){
			dataList.add(	buildSingeOne(usrIdMasterInfoMap, usrIdOrderEvaluate,bean) );
		 }
		JSONObject result = JsonUtils.commonJsonReturn(); 
		JsonUtils.setBody(result, "data", dataList);
		JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(dataList) ? dataList.size() : 0);
		return result;
	}
	private JSONObject buildSingeOne(Map<Long, FsMasterInfo> usrIdMasterInfoMap,Map<Long, Map> usrIdOrderEvaluate, FsMasterServiceCate bean) {
		JSONObject dataOne = new JSONObject(true);
		FsMasterInfo masterInfo = usrIdMasterInfoMap.get( bean.getUsrId() ) ;
		Map  avgScore = usrIdOrderEvaluate.get(  bean.getUsrId()  );
		dataOne.put("masterNickName",   UsrAidUtil.getMasterNickName(masterInfo, null, "") );
		dataOne.put("masterHeadImgUrl",  masterInfo!=null ? ( masterInfo.getHeadImgUrl() )  : null );
		dataOne.put("goodsName", bean.getName());
		dataOne.put("goodsId", bean.getId()); 
		dataOne.put("zxCateId",  bean.getFsZxCateId() ); 
		dataOne.put("masterUsrId",  bean.getUsrId() ); 
		dataOne.put("masterInfoId",  masterInfo.getId() ); 
		dataOne.put("amt", bean.getAmt()); //单位分
		dataOne.put("amtDesc", CommonUtils.numberFormat(bean.getAmt()/100d, "###,##0.00", "0.0")); //单位元
		dataOne.put("isCertificated", masterInfo.getCertNo()!=null ? "Y" :"N");  //是否实行认证
		dataOne.put("isSignOther", masterInfo.getIsSignOther());  // 是否 在其他平台签约 Y(非独家);N(独家)
		dataOne.put("workBeginDate", masterInfo.getWorkDate());  // 工作起始日期
		dataOne.put("workYearStr",  UsrAidUtil.getWorkYearStr(masterInfo.getWorkDate()) );  // 从业年限
		dataOne.put("isTranSecuried", "Y"); //是否担保交易
		dataOne.put("serviceStatus", masterInfo.getServiceStatus());
		dataOne.put("achievement", StringEscapeUtils.escapeHtml4( masterInfo.getAchievement())); //取得主要成就 
		dataOne.put("experience", StringEscapeUtils.escapeHtml4( masterInfo.getExperience())); //相关经历 
		dataOne.put("goodAt", StringEscapeUtils.escapeHtml4( masterInfo.getGoodAt())); //擅长领域 
		//从业年限
		//评分数据
		Double respSpeed = getValueFromMap(avgScore , "resp_speed",0d)	;	
		Double majorLevel = getValueFromMap(avgScore , "major_level",0d)	;	
		Double serviceAttitude = getValueFromMap(avgScore , "service_attitude",0d)	;	
		dataOne.put("score",   (respSpeed+majorLevel +serviceAttitude)/3d ) ;  		// 评分 
		dataOne.put("scoreDesc",  CommonUtils.numberFormat( (respSpeed+majorLevel +serviceAttitude)/3d  , "###,##0.00", "0.00") ) ;  		// 评分 
		return dataOne;
	}
	private Double getValueFromMap(Map map ,String key , Double defVale){
		if(map == null || map.get(key) ==null){
			return defVale;
		}
		return Double.valueOf( map.get( key ) .toString() );
	}
	private Map<Long,Map> convertOrderEvaluate(List<Map> orderEvaluateList){
		Map<Long,Map> map = new HashMap<Long,Map>();
		if(CollectionUtils.isEmpty(orderEvaluateList)){
			return map;
		}
		for(Map bean:  orderEvaluateList ){
			Long _usrId = Long.valueOf( bean.get("seller_usr_id").toString() );
			if( !map.containsKey(  _usrId) ){
				map.put(_usrId, bean);
			}
		}
		return map;
	}
	/** method never return null **/
	private Map<Long,FsMasterInfo> convertMasterInfo(List<FsMasterInfo> masterInfoList){
		Map<Long,FsMasterInfo> map = new HashMap<Long,FsMasterInfo>();
		if(CollectionUtils.isEmpty(masterInfoList)){
			return map;
		}
		for(FsMasterInfo bean:  masterInfoList ){
			if( !map.containsKey(bean.getUsrId()) ){
				map.put(bean.getUsrId(), bean);
			}
		}
		return map;
	}
	private List<Long> getMasterUsrIds(List<FsMasterServiceCate> list){
		List<Long> masterUsrIds = new ArrayList<Long>();
		for(FsMasterServiceCate bean : list){
			if( !masterUsrIds.contains( bean.getUsrId() ) ){
				masterUsrIds.add(  bean.getUsrId()  );
			}
		}
		return masterUsrIds;
	}
}
