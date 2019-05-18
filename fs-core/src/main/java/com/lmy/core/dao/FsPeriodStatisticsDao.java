package com.lmy.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsPeriodStatistics;
import com.lmy.core.service.impl.OrderAidUtil;
@Component
public class FsPeriodStatisticsDao extends GenericDAOImpl<FsPeriodStatistics> {

	@Override
	public String getNameSpace() {
		return "fs_period_statistics";
	}
	
	public List<FsPeriodStatistics> statisticPeriodOrder(Long cateId, Date startTime, Date endTime) {
		JSONObject map = new JSONObject();
		map.put("cateId", cateId);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("statusList", OrderAidUtil.getCommAllOrderStatus());
		return this.getSqlSession().selectList(this.getNameSpace()+".statisticPeriodOrder", map);
	}

	
	public List<FsPeriodStatistics> findByCond(String periodName, Long sellerUsrId, Long cateId, String type) {
		JSONObject map = new JSONObject();
		map.put("periodName", periodName);
		map.put("sellerUsrId", sellerUsrId);
		map.put("cateId", cateId);
		map.put("type", type);
		return this.getSqlSession().selectList(this.getNameSpace()+".findByCond", map);
	}
	

	public List<Map<String, Object>> searchSortedMasters(Long filterCateId, String order) {
		JSONObject map = new JSONObject();
		map.put("cateId" , filterCateId);
		
		if (order.toLowerCase().endsWith("asc")) {
			map.put("order", "asc");
		} else {
			map.put("order", "desc");
		}
		
		
		return this.getSqlSession().selectList(this.getNameSpace()+".searchSortedMasters", map);
	}
}
