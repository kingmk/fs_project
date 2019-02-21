package com.lmy.core.dao;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsCalendarUserinfo;

@Component
public class FsCalendarUserinfoDao extends GenericDAOImpl<FsCalendarUserinfo> {

	@Override
	public String getNameSpace() {
		return "fs_calendar_userinfo";
	}

	
	public FsCalendarUserinfo findUserMainInfo(Long userId){
		Assert.isTrue(userId !=null);
		JSONObject map = new JSONObject();
		map.put("userId", userId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findUserMainInfo", map);
	}

	public FsCalendarUserinfo findByMobile(String mobile){
		Assert.isTrue(mobile !=null);
		JSONObject map = new JSONObject();
		map.put("mobile", mobile);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findByMobile", map);
	}
}
