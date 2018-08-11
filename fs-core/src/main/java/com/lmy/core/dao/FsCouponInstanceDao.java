package com.lmy.core.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsCouponInstance;

@Component
public class FsCouponInstanceDao extends GenericDAOImpl<FsCouponInstance> {

	@Override
	public String getNameSpace() {
		return "fs_coupon_instance";
	}

	public Long countInstance(Long usrId, Long templateId, String usable) {
		JSONObject map = new JSONObject();
		map.put("usrId", usrId);
		map.put("templateId", templateId);
		map.put("usable", usable);
		map.put("now", new Date());
		
		return this.getSqlSession().selectOne(this.getNameSpace()+".countInstance", map);
	}
	
	public List<FsCouponInstance> findByUser(Long usrId, String usable, Integer page, Integer pagesize) {
		JSONObject map = new JSONObject();
		map.put("usrId", usrId);
		map.put("usable", usable);
		if (page != null && pagesize != null) {
			map.put("start", page*pagesize);
			map.put("pagesize", pagesize);
		}
		map.put("now", new Date());
		
		return this.getSqlSession().selectList(this.getNameSpace()+".findByUser", map);
	}
	
	public int updateStatus(Long couponId, String status) {
		JSONObject map = new JSONObject();
		map.put("id", couponId);
		map.put("status", status);
		
		return this.getSqlSession().update(this.getNameSpace()+".updateStatus", map);
	}
}
