package com.lmy.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.lmy.core.dao.FsPeriodStatisticsDao;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsMasterStatistics;
import com.lmy.core.model.FsPeriodStatistics;
import com.lmy.core.model.FsZxCate;
import com.lmy.core.utils.FsExecutorUtil;

@Service
public class StatisticsServiceImpl {

	private static final Logger logger = Logger.getLogger(StatisticsServiceImpl.class);
	@Autowired
	FsPeriodStatisticsDao fsPeriodStatisticsDao;
	@Autowired
	FsZxCateDao fsCateDao;
	
	public void monthlyStatistics() {
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		DateFormat df1 = new SimpleDateFormat("yyyy-MM");
		String dStr1 = df1.format(now)+"-01 00:00:00";
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		DateFormat df3 = new SimpleDateFormat("yyyyMM");
		try {
			Date endTime = df2.parse(dStr1);
			c.setTime(endTime);
			c.add(Calendar.MONTH, -1);
			Date startTime = c.getTime();
			
			String type = FsPeriodStatistics.TYPE_MONTH;
			String periodName = df3.format(startTime);
			
			logger.info("=====statistics for "+periodName+", startTime:"+startTime+", endTime:"+endTime+"=====");
			this.createPeriodStatistics(periodName, type, null, startTime, endTime);
			
		} catch (ParseException e) {
			logger.warn("=======parse date failed======="+e);
		}
	}
	
	public void monthlyStatistics(String curMonth) {
		Calendar c = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		DateFormat df2 = new SimpleDateFormat("yyyyMM");
		String dStr = curMonth+"-01 00:00:00";
		try {
			Date startTime = df.parse(dStr);
			c.setTime(startTime);
			String periodName = df2.format(startTime);
			c.add(Calendar.MONTH, 1);
			Date endTime = c.getTime();
			String type = FsPeriodStatistics.TYPE_MONTH;
			logger.info("=====statistics for "+periodName+", startTime:"+startTime+", endTime:"+endTime+"=====");
			this.createPeriodStatistics(periodName, type, null, startTime, endTime);
		} catch (ParseException e) {
			logger.warn("=======parse date failed======="+e);
		}	
	}
	
	public void createPeriodStatistics(String periodName, String type, Long cateId, Date startTime, Date endTime) {
		
		List<FsPeriodStatistics> beanList = fsPeriodStatisticsDao.statisticPeriodOrder(cateId, startTime, endTime);
		Date now = new Date();
		for (int i = 0; i < beanList.size(); i++) {
			FsPeriodStatistics tmp = beanList.get(i);
			tmp.setPeriodName(periodName);
			if (cateId != null) {
				tmp.setCateId(cateId);
			} else {
				tmp.setCateId(1l);
			}
			if (tmp.getSumEvaluate() == null) {
				tmp.setSumEvaluate(0l);
			}
			tmp.setType(type);
			tmp.setStartTime(startTime);
			tmp.setEndTime(endTime);
			tmp.setCreateTime(now);
			tmp.setUpdateTime(now);
			tmp.setStatus(FsPeriodStatistics.STATUS_VALID);
			tmp.setSortScore(OrderAidUtil.calcMasterScore(tmp));
		}
		if (beanList.size() > 0) {
			fsPeriodStatisticsDao.batchInsert(beanList);
		} else {
			logger.info("=====no statistics data in the given period between "+startTime+" and "+endTime+"=====");
		}
	}
	

	public void sortStatistics() {
		Calendar c = Calendar.getInstance();
		Date endTime = new Date();
		
		c.setTime(endTime);
		c.add(Calendar.MONTH, -1);
		Date startTime = c.getTime();
		String periodName = "SORT_CATE_MONTH";
		String type = "REALMONTH";
		List<FsZxCate> cateList = fsCateDao.findZxCate1(null, null, 2l, null, null);
		for (FsZxCate cate: cateList) {
			sortCateStatistics(periodName, type, cate.getId(), startTime, endTime);
		}
		
		c.setTime(endTime);
		c.add(Calendar.YEAR, -1);
		startTime = c.getTime();
		periodName = "SORT_CATE_YEAR";
		type = "REALYEAR";
		sortCateStatistics(periodName, type, null, startTime, endTime);
		clearSearchCache();
	}
	
	private void sortCateStatistics(String periodName, String type, Long cateId, Date startTime, Date endTime) {
		// get the new statistics during the given period
		List<FsPeriodStatistics> statNewList = fsPeriodStatisticsDao.statisticPeriodOrder(cateId, startTime, endTime);
		// get the old statistics for update
		List<FsPeriodStatistics> statOldList = fsPeriodStatisticsDao.findByCond(periodName, null, (cateId!=null?cateId:1l), type);
		
		// sort the old statistics into map
		Map<String, Long> statOldIdMap = new HashMap<String, Long>();
		for (FsPeriodStatistics stat: statOldList) {
			String key = type+stat.getSellerUsrId()+"_"+stat.getCateId();
			statOldIdMap.put(key, stat.getId());
		}
		int countInsert = 0;
		int countUpdate = 0;
		int countInvalid = 0;
		
		List<Long> updateIdList = new ArrayList<>();
		Date now = new Date();
		for (FsPeriodStatistics stat: statNewList) {
			stat.setPeriodName(periodName);
			if (cateId != null) {
				stat.setCateId(cateId);
			} else {
				stat.setCateId(1l);
			}
			if (stat.getSumEvaluate() == null) {
				stat.setSumEvaluate(0l);
			}
			stat.setType(type);
			stat.setStartTime(startTime);
			stat.setEndTime(endTime);
			stat.setCreateTime(now);
			stat.setUpdateTime(now);
			stat.setStatus(FsPeriodStatistics.STATUS_VALID);
			stat.setSortScore(OrderAidUtil.calcMasterScore(stat));

			String key = type+stat.getSellerUsrId()+"_"+stat.getCateId();
			// if the seller's statistics in the cate exists, only need update, 
			// otherwise, need insert
			if (statOldIdMap.containsKey(key)){
				Long id = statOldIdMap.get(key);
				updateIdList.add(id);
				stat.setId(id);
				fsPeriodStatisticsDao.update(stat);
				countUpdate++;
			} else {
				fsPeriodStatisticsDao.insert(stat);
				countInsert++;
			}
		}
		
		for (FsPeriodStatistics stat: statOldList) {
			Long id = stat.getId();
			// if the stat been updated, nothing should be done, 
			// otherwise, the stat should be update invalid
			if (!updateIdList.contains(id)) {
				FsPeriodStatistics tmp = new FsPeriodStatistics();
				tmp.setId(id);
				tmp.setUpdateTime(now);
				tmp.setStatus(FsPeriodStatistics.STATUS_INVALID);
				fsPeriodStatisticsDao.update(tmp);
				countInvalid++;
			}
		}
		
		// to reset the cache
		if (cateId == null) {
			cateId = 1l;
		}
		
		logger.info("======sorted for cate="+cateId+", insert: "+countInsert+
				", update: "+countUpdate+", invalid:"+countInvalid+"=======");
	}
	
	public JSONObject searchSortedMasters(Long cateId, String order, Boolean dbDemand) {
		if (cateId == null) {
			cateId = 1l;
		}
		
		String cacheKey = CacheConstant.FS_SEARCH_SORTED_MASTER + 
				"_zxCateId=" + cateId + "_order=" + order;

		JSONArray jMasterStatArray = (JSONArray) RedisClient.get(cacheKey);
		if (dbDemand || jMasterStatArray == null) {
			logger.info("===== get search result from db =====");
			List<Map<String, Object>> masterMapList = fsPeriodStatisticsDao.searchSortedMasters(cateId, order);

			logger.info("===== master map list: "+ masterMapList);
			jMasterStatArray = buildSearchResult(masterMapList, cateId);
			RedisClient.set(cacheKey, jMasterStatArray, 7200);
		} else {
			logger.info("===== load search result from cache ["+cacheKey+"]=====");
		}
		
		if (jMasterStatArray.size() == 0 ) {
			return JsonUtils.commonJsonReturn("1000", "查无数据");
		}
		
		

		JSONObject result = JsonUtils.commonJsonReturn(); 
		JsonUtils.setBody(result, "data", jMasterStatArray);
		JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(jMasterStatArray) ? jMasterStatArray.size() : 0);
		return result;
	}
	

	private JSONArray buildSearchResult(List<Map<String, Object>> list, Long cateId) {
		JSONArray jDataArr = new JSONArray();

		if(CollectionUtils.isEmpty(list)){
			return jDataArr;
		}
		
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> mapdata = list.get(i);
			JSONObject jData = new JSONObject();
			Long masterUsrId = Long.valueOf((Integer)mapdata.get("usr_id"));
			jData.put("masterUsrId", masterUsrId); 
			jData.put("masterInfoId", mapdata.get("master_info_id"));
			jData.put("masterNickName", mapdata.get("nick_name"));
			jData.put("masterHeadImgUrl", mapdata.get("head_img_url"));
			jData.put("zxCateId", cateId);

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
			Integer countOrder = (Integer)mapdata.get("count_order2");
			jData.put("countOrder", (countOrder!=null? countOrder:0));
			Integer countEvaluate = (Integer) mapdata.get("count_evaluate");
			Integer sumEvaluate = (Integer) mapdata.get("sum_evaluate");
			
			Double score = 0.00d;
			
			if (countEvaluate != null && countEvaluate > 0) {
				score = Double.valueOf(sumEvaluate)/ Double.valueOf(countEvaluate);
				score = score*2.0d/3.0d;
			}
			jData.put("score", score); // 评分 
			jData.put("scoreDesc", CommonUtils.numberFormat(score, "###,##0.00", "0.00")) ; // 评分
			
			// how to fetch the price
			
			Integer price = (Integer)mapdata.get("min_price");
			jData.put("amt", mapdata.get("min_price")); //单位分
			jData.put("amtDesc", CommonUtils.numberFormat(price/100d, "###,##0.00", "0.0")); //单位元
			
			
			jDataArr.add(jData);
		}
		
		return jDataArr;
	}

	public JSONObject clearSearchCache() {
		
		Set<String> keySet = RedisClient.getKeys(CacheConstant.FS_SEARCH_SORTED_MASTER+"*");
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String keyStr = it.next();
			RedisClient.delete(keyStr);
			logger.info("#########del cache: "+keyStr);
		}
		
		JSONObject result = JsonUtils.commonJsonReturn(); 
		return result;
	}
	
}
