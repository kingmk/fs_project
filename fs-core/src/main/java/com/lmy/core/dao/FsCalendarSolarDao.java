package com.lmy.core.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsCalendarSolar;

@Component
public class FsCalendarSolarDao extends GenericDAOImpl<FsCalendarSolar> {
	@Override
	public String getNameSpace() {
		return "fs_calendar_solar";
	}
	
	public List<FsCalendarSolar> getSolarByPeriod(Date dateStart, Date dateEnd) {
		JSONObject map = new JSONObject();
		map.put("dateStart", dateStart);
		map.put("dateEnd", dateEnd);
		return this.getSqlSession().selectList(this.getNameSpace()+".getSolarByPeriod", map);
	}
}
