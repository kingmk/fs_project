package com.lmy.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsMasterServiceCateDao;
import com.lmy.core.dao.FsMasterStatisticsDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderEvaluateDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsMasterStatistics;

@Service
public class MasterStatisticsServiceImpl {
	
	private static final Logger logger = Logger.getLogger(MasterStatisticsServiceImpl.class);
	@Autowired
	FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	FsMasterStatisticsDao fsMasterStatisticsDao;
	@Autowired
	FsOrderDao fsOderDao;	
	@Autowired
	FsOrderEvaluateDao fsOrderEvaluateDao;
	@Autowired
	FsMasterServiceCateDao fsMasterServiceCateDao;
	
	public void calculateStatistics() {
		logger.info("====start adjust master statistics====");
		List<Map<String, Object>> orderStatistics = this.fsOderDao.statOrdersByMasterCate();
		List<Map<String, Object>> evaluateStatistics = this.fsOrderEvaluateDao.statEvaluateByMasterCate();
		List<Map<String, Object>> servicesStatistics = this.fsMasterServiceCateDao.findByMasterCate();
		
		List<Long> masterUsrIds = new ArrayList<Long>();
		Map<Long, Map<Long, FsMasterStatistics>> masterCateStatMap = new HashMap<Long, Map<Long, FsMasterStatistics>>();
		
		statOrders(orderStatistics, masterUsrIds, masterCateStatMap);
		statEvaluates(evaluateStatistics, masterUsrIds, masterCateStatMap);
		statServices(servicesStatistics, masterUsrIds, masterCateStatMap);
		for(Long masterUsrId: masterUsrIds) {
			saveOneMasterStatistics(masterUsrId, masterCateStatMap.get(masterUsrId));
		}
	}

	private void statOrders(List<Map<String, Object>> orderStatistics,
			List<Long> masterUsrIds,
			Map<Long, Map<Long, FsMasterStatistics>> masterCateStatMap) {
		for (Map<String, Object> orderStat : orderStatistics) {
			Long masterUsrId = Long.valueOf((Integer)orderStat.get("seller_usr_id"));
			Long cateId = Long.valueOf((Integer)orderStat.get("zx_cate_id"));
			Map<Long, FsMasterStatistics> cateStatMap = null;
			if (!masterUsrIds.contains(masterUsrId)) {
				// a new master
				cateStatMap = new HashMap<Long, FsMasterStatistics>();
				masterUsrIds.add(masterUsrId);
			} else {
				// an existed master
				cateStatMap = masterCateStatMap.get(masterUsrId);
			}
			FsMasterStatistics masterStatistics = null;
			if (!cateStatMap.containsKey(cateId)) {
				// a new cate statistics
				masterStatistics = new FsMasterStatistics();
			} else {
				// an existed cate statistics
				masterStatistics = cateStatMap.get(cateId);
			}
			String cateName = (String)orderStat.get("goods_name");
			Long countOrder = (Long)orderStat.get("count_order");
			
			masterStatistics.setMasterUsrId(masterUsrId).setCateId(cateId)
				.setCateName(cateName).setCountOrder(countOrder);
			
			cateStatMap.put(cateId, masterStatistics);
			masterCateStatMap.put(masterUsrId, cateStatMap);
		}
	}

	private void statEvaluates(List<Map<String, Object>> evaluateStatistics,
			List<Long> masterUsrIds,
			Map<Long, Map<Long, FsMasterStatistics>> masterCateStatMap) {
		for (Map<String, Object> evaluateStat : evaluateStatistics) {
			Long masterUsrId = Long.valueOf((Integer)evaluateStat.get("seller_usr_id"));
			Long cateId = Long.valueOf((Integer)evaluateStat.get("zx_cate_id"));
			Map<Long, FsMasterStatistics> cateStatMap = null;
			if (!masterUsrIds.contains(masterUsrId)) {
				// a new master
				cateStatMap = new HashMap<Long, FsMasterStatistics>();
				masterUsrIds.add(masterUsrId);
			} else {
				// an existed master
				cateStatMap = masterCateStatMap.get(masterUsrId);
			}
			FsMasterStatistics masterStatistics = null;
			if (!cateStatMap.containsKey(cateId)) {
				// a new cate statistics
				masterStatistics = new FsMasterStatistics();
			} else {
				// an existed cate statistics
				masterStatistics = cateStatMap.get(cateId);
			}
			String cateName = (String)evaluateStat.get("goods_name");
			Long count = (Long)evaluateStat.get("count");
			Long sumRespSpeed = ((BigDecimal)evaluateStat.get("sum_resp_speed")).longValue();
			Long sumMajorLevel = ((BigDecimal)evaluateStat.get("sum_major_level")).longValue();
			Long sumServiceAttitude = ((BigDecimal)evaluateStat.get("sum_service_attitude")).longValue();
			
			masterStatistics.setMasterUsrId(masterUsrId).setCateId(cateId).setCateName(cateName).setCountEvaluate(count)
				.setSumRespSpeed(sumRespSpeed).setSumMajorLevel(sumMajorLevel).setSumServiceAttitude(sumServiceAttitude);

			cateStatMap.put(cateId, masterStatistics);
			masterCateStatMap.put(masterUsrId, cateStatMap);
		}
	}

	private void statServices(List<Map<String, Object>> servicesStatistics,
			List<Long> masterUsrIds,
			Map<Long, Map<Long, FsMasterStatistics>> masterCateStatMap) {
		for (Map<String, Object> serviceStat : servicesStatistics) {
			Long masterUsrId = Long.valueOf((Integer)serviceStat.get("usr_id"));
			Long cateId = Long.valueOf((Integer)serviceStat.get("zx_cate_id"));
			Map<Long, FsMasterStatistics> cateStatMap = null;
			if (!masterUsrIds.contains(masterUsrId)) {
				// a new master
				cateStatMap = new HashMap<Long, FsMasterStatistics>();
				masterUsrIds.add(masterUsrId);
			} else {
				// an existed master
				cateStatMap = masterCateStatMap.get(masterUsrId);
			}
			FsMasterStatistics masterStatistics = null;
			if (!cateStatMap.containsKey(cateId)) {
				// a new cate statistics
				masterStatistics = new FsMasterStatistics();
			} else {
				// an existed cate statistics
				masterStatistics = cateStatMap.get(cateId);
			}
			String cateName = (String)serviceStat.get("name");
			String isPlatRecomm = (String)serviceStat.get("is_plat_recomm");
			Long price = Long.valueOf((Integer)serviceStat.get("amt"));
			String status = (String)serviceStat.get("status");
			
			masterStatistics.setMasterUsrId(masterUsrId).setCateId(cateId).setCateName(cateName)
				.setMinPrice(price).setIsPlatRecomm(isPlatRecomm).setStatus(status);

			cateStatMap.put(cateId, masterStatistics);
			masterCateStatMap.put(masterUsrId, cateStatMap);
		}
		
	}
	
	private void saveOneMasterStatistics(Long masterUsrId, Map<Long, FsMasterStatistics> cateStatMap) {
//		logger.info("==== to save for master["+masterUsrId+"], map: "+cateStatMap.toString()+"====");
		FsMasterInfo masterInfo = this.fsMasterInfoDao.findByUsrId(masterUsrId);
		if (masterInfo == null) {
			logger.info("==== dirty data, no such master, usrId: "+masterUsrId+" ====");
			return;
		}
		Long masterInfoId = masterInfo.getId();
		
		FsMasterStatistics masterAllStat = new FsMasterStatistics();
		masterAllStat.setMasterUsrId(masterUsrId).setMasterInfoId(masterInfoId).setCateId(1L).setCateName("全部");
		long countOrder = 0L;
		long countEvaluate = 0L;
		long sumRespSpeed = 0L;
		long sumMajorLevel = 0L;
		long sumServiceAttitude = 0L;
		long minPrice = Long.MAX_VALUE;
		String isPlatRecomm = "N";
		Date now = new Date();
		for (Long cateId : cateStatMap.keySet()) {
			FsMasterStatistics cateStat = cateStatMap.get(cateId);
			cateStat.setMasterInfoId(masterInfoId);
			cateStat.defaultValue();
			countOrder += cateStat.getCountOrder();
			countEvaluate += cateStat.getCountEvaluate();
			sumRespSpeed += cateStat.getSumRespSpeed();
			sumMajorLevel += cateStat.getSumMajorLevel();
			sumServiceAttitude += cateStat.getSumServiceAttitude();
			if (cateStat.getStatus().equals("ON")) {
				minPrice = Math.min(minPrice, cateStat.getMinPrice());
				if (cateStat.getIsPlatRecomm().equals("Y")) {
					isPlatRecomm = "Y";
				}
			}
			FsMasterStatistics tmpStat = this.fsMasterStatisticsDao.findByCateId(masterUsrId, cateId);
			if ( tmpStat != null) {
				cateStat.setUpdateTime(now);
				cateStat.setId(tmpStat.getId());
				this.fsMasterStatisticsDao.update(cateStat);
			} else {
				cateStat.setCreateTime(now);
				cateStat.defaultValue();
				this.fsMasterStatisticsDao.insert(cateStat);
			}
		}
		
		String status = "OFF"; 
		if (masterInfo.getServiceStatus().equals("ING")) {
			status = "ON";
		} else if (masterInfo.getServiceStatus().equals("NOTING")) {
			status = "OFF";
		} else if (masterInfo.getServiceStatus().equals("FORBID")) {
			status = "FORBID";
		}
		
		masterAllStat.setCountOrder(countOrder).setCountEvaluate(countEvaluate).setSumRespSpeed(sumRespSpeed)
			.setSumMajorLevel(sumMajorLevel).setSumServiceAttitude(sumServiceAttitude).setMinPrice(minPrice)
			.setIsPlatRecomm(isPlatRecomm).setStatus(status);

		FsMasterStatistics tmpStat = this.fsMasterStatisticsDao.findByCateId(masterUsrId, 1L);
		if (tmpStat != null) {
			masterAllStat.setId(tmpStat.getId());
			masterAllStat.setUpdateTime(now);
			this.fsMasterStatisticsDao.update(masterAllStat);
		} else {
			masterAllStat.setCreateTime(now);
			masterAllStat.defaultValue();
			this.fsMasterStatisticsDao.insert(masterAllStat);
		}
	}
	
	public JSONObject searchMasters(Long filterCateId, String orderBy, int page, int pageSize) {
		if (filterCateId == null) {
			filterCateId = 1L;
		}
		
		String orderType = null;
		String ascType = null;
		if (orderBy == null) {
			orderType = "default";
			ascType = "desc";
		} else {
			if (orderBy.endsWith("Asc")) {
				ascType = "asc";
			} else if (orderBy.endsWith("Desc")) {
				ascType = "desc";
			} else {
				ascType = "desc";
			}
			
			if (orderBy.startsWith("order")) {
				orderType = "default";
			} else if (orderBy.startsWith("price")) {
				orderType = "price";
			} else if (orderBy.startsWith("evaluate")) {
				orderType = "evaluate";
			} else {
				orderType = "default";
			}
		}
		
		String cacheKey = CacheConstant.FS_SEARCH_MASTER +"_zxCateId:"+filterCateId+"_orderType:"
				+orderType+"_ascType:"+ascType+"_new";
		JSONArray jMasterStatArray = (JSONArray) RedisClient.get(cacheKey);
		if (jMasterStatArray == null) {
			logger.info("===== get search result from db =====");
			List<Map<String, Object>> masterMapList = this.fsMasterStatisticsDao.searchMastersByUser(filterCateId, orderType, ascType);		
			jMasterStatArray = buildSearchResult(masterMapList, filterCateId);
			RedisClient.set(cacheKey, jMasterStatArray, 600);
		} else {
			logger.info("===== load search result from cache ["+cacheKey+"]=====");
		}
		
		if (jMasterStatArray.size() == 0 || page*pageSize > jMasterStatArray.size()) {
			return JsonUtils.commonJsonReturn("1000", "查无数据");
		}
		
		int start = page*pageSize;
		int end = Math.min((page+1)*pageSize, jMasterStatArray.size());
		JSONArray jDataArr = new JSONArray();
		for (int i = start; i < end; i++) {
			jDataArr.add(jMasterStatArray.get(i));
		}
		JSONObject result = JsonUtils.commonJsonReturn(); 
		JsonUtils.setBody(result, "data", jDataArr);
		JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(jDataArr) ? jDataArr.size() : 0);
		return result;
	}
	
	private JSONArray buildSearchResult(List<Map<String, Object>> list, Long cateId) {
		JSONArray jDataArr = new JSONArray();

		if(CollectionUtils.isEmpty(list)){
			return jDataArr;
		}
		
		List<Long> usrIdList = new ArrayList<Long>(list.size());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> mapdata = list.get(i);
			JSONObject jData = new JSONObject();
			Long masterUsrId = Long.valueOf((Integer)mapdata.get("usr_id"));
			usrIdList.add(masterUsrId);
			jData.put("masterUsrId", masterUsrId); 
			jData.put("masterInfoId", mapdata.get("master_info_id"));
			jData.put("masterNickName", mapdata.get("nick_name"));
			jData.put("masterHeadImgUrl", mapdata.get("head_img_url"));
			jData.put("zxCateId", mapdata.get("cate_id"));
			Integer price = (Integer)mapdata.get("min_price");
			jData.put("amt", mapdata.get("min_price")); //单位分
			jData.put("amtDesc", CommonUtils.numberFormat(price/100d, "###,##0.00", "0.0")); //单位元
			jData.put("isCertificated", mapdata.get("cert_no")!=null ? "Y" :"N");  //是否实行认证
			jData.put("isSignOther", mapdata.get("is_sign_other"));  // 是否 在其他平台签约 Y(非独家);N(独家)
			Date workDate = (Date) mapdata.get("work_date");
			jData.put("workBeginDate", workDate);  // 工作起始日期
			jData.put("workYearStr",  UsrAidUtil.getWorkYearStr(workDate) );  // 从业年限
			jData.put("isTranSecuried", "Y"); //是否担保交易
			jData.put("serviceStatus", mapdata.get("service_status"));
			jData.put("achievement", StringEscapeUtils.escapeHtml4( (String) mapdata.get("achievement"))); //取得主要成就 
			jData.put("experience", StringEscapeUtils.escapeHtml4( (String) mapdata.get("experience"))); //相关经历 
			jData.put("goodAt", StringEscapeUtils.escapeHtml4( (String) mapdata.get("good_at"))); //擅长领域
			
			jData.put("isPlatRecomm", mapdata.get("is_plat_recomm"));
			jData.put("countOrder", mapdata.get("count_order"));
			Integer sumRespSpeed = (Integer) mapdata.get("sum_resp_speed");
			Integer sumMajorLevel = (Integer) mapdata.get("sum_major_level");
			Integer sumServiceAttitude = (Integer) mapdata.get("sum_service_attitude");
			Integer countEvaluate = (Integer) mapdata.get("count_evaluate");
			
			
			Double score = 0.00d;
			if (countEvaluate > 0) {
				score = Double.valueOf(sumRespSpeed+sumMajorLevel+sumServiceAttitude)/ Double.valueOf(countEvaluate);
				score = score*2.0d/3.0d;
			}
			jData.put("score", score); // 评分 
			jData.put("scoreDesc", CommonUtils.numberFormat(score, "###,##0.00", "0.00")) ; // 评分
			
			jDataArr.add(jData);
		}
		if (cateId != 1L){
			// need to load all master data in cate 1 to get the evaluate scores on all cates
			List<FsMasterStatistics> masterStatList = this.fsMasterStatisticsDao.findByUsrIds(usrIdList, 1L);
			Map<Long, FsMasterStatistics> masterStatMap = new HashMap<>();
			for (int i = 0; i < masterStatList.size(); i++) {
				FsMasterStatistics masterStatistics = masterStatList.get(i);
				masterStatMap.put(masterStatistics.getMasterUsrId(), masterStatistics);
			}
			
			for (int i = 0; i < jDataArr.size(); i++) {
				JSONObject jData = (JSONObject) jDataArr.get(i);
				Long masterUsrId = (Long)jData.get("masterUsrId");
				FsMasterStatistics masterStatistics = masterStatMap.get(masterUsrId);
				Double score = 0.00d;
				if (masterStatistics.getCountEvaluate() > 0) {
					score = Double.valueOf(masterStatistics.getSumRespSpeed()+masterStatistics.getSumMajorLevel()+
							masterStatistics.getSumServiceAttitude())/Double.valueOf(masterStatistics.getCountEvaluate());
					score = score*2.0d/3.0d;
				}
				jData.put("score", score); // 评分 
				jData.put("scoreDesc", CommonUtils.numberFormat(score, "###,##0.00", "0.00")) ; // 评分
				
			}
		}
		
		return jDataArr;
	}
}
