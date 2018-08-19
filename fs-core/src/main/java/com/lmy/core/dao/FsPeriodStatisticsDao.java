package com.lmy.core.dao;

import java.util.Date;
import java.util.List;

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
	
	public List<FsPeriodStatistics> statisticPeriodOrder(Date startTime, Date endTime) {
		JSONObject map = new JSONObject();
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("statusList", OrderAidUtil.getCommAllOrderStatus());
		return this.getSqlSession().selectList(this.getNameSpace()+".statisticPeriodOrder", map);
	}

}
