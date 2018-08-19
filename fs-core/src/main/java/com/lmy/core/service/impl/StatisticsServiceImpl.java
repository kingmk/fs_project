package com.lmy.core.service.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.core.dao.FsPeriodStatisticsDao;
import com.lmy.core.model.FsPeriodStatistics;

@Service
public class StatisticsServiceImpl {

	private static final Logger logger = Logger.getLogger(StatisticsServiceImpl.class);
	@Autowired
	FsPeriodStatisticsDao fsPeriodStatisticsDao;
	
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
			this.createPeriodStatistics(periodName, type, startTime, endTime);
			
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
			this.createPeriodStatistics(periodName, type, startTime, endTime);
		} catch (ParseException e) {
			logger.warn("=======parse date failed======="+e);
		}
		
		
	}
	
	public void createPeriodStatistics(String periodName, String type, Date startTime, Date endTime) {
		
		List<FsPeriodStatistics> beanList = fsPeriodStatisticsDao.statisticPeriodOrder(startTime, endTime);
		Date now = new Date();
		for (int i = 0; i < beanList.size(); i++) {
			FsPeriodStatistics tmp = beanList.get(i);
			tmp.setPeriodName(periodName);
			tmp.setType(type);
			tmp.setStartTime(startTime);
			tmp.setEndTime(endTime);
			tmp.setCreateTime(now);
		}
		if (beanList.size() > 0) {
			fsPeriodStatisticsDao.batchInsert(beanList);
		} else {
			logger.info("=====no statistics data in the given period between "+startTime+" and "+endTime+"=====");
		}
	}
}
