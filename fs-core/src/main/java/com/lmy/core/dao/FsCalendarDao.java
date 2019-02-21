package com.lmy.core.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsCalendar;

@Component
public class FsCalendarDao extends GenericDAOImpl<FsCalendar> {
	@Override
	public String getNameSpace() {
		return "fs_calendar";
	}
	
	public List<FsCalendar> getCalendarsByPeriod(Integer type, Long userId, Date dateStart, Date dateEnd) {
		JSONObject map = new JSONObject();
		map.put("type", type);
		map.put("userId", userId);
		map.put("dateStart", dateStart);
		map.put("dateEnd", dateEnd);
		return this.getSqlSession().selectList(this.getNameSpace()+".getCalendarsByPeriod", map);
	}

}
