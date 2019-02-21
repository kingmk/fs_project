package com.lmy.core.dao;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsCalendarUser;

@Component
public class FsCalendarUserDao extends GenericDAOImpl<FsCalendarUser> {

	@Override
	public String getNameSpace() {
		return "fs_calendar_user";
	}
	

	public FsCalendarUser findByOpenId(String openId){
		Assert.isTrue(!StringUtils.isEmpty(openId));
		JSONObject map = new JSONObject();
		map.put("openId", openId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findByOpenId", map);
	}

}
