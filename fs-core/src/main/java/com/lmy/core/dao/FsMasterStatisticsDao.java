package com.lmy.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsMasterStatistics;


@Component
public class FsMasterStatisticsDao extends GenericDAOImpl<FsMasterStatistics> {

	@Override
	public String getNameSpace() {
		return "fs_master_statistics";
	}
	

	public List<FsMasterStatistics> findAll() {
		return this.getSqlSession().selectList(this.getNameSpace()+".findAll");
	}

	public FsMasterStatistics findByCateId(Long masterUsrId, Long cateId) {
		JSONObject map = new JSONObject();
		map.put("masterUsrId" , masterUsrId);
		map.put("cateId" , cateId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findByCateId", map);
	}
	
	public int incOrder(Long masterUsrId, Long cateId) {
		JSONObject map = new JSONObject();
		map.put("masterUsrId" , masterUsrId);
		map.put("cateId" , cateId);
		map.put("updateTime", new Date());
		logger.info("===== increase master statistics for order: "+map.toJSONString()+"=====");
		return this.getSqlSession().update(this.getNameSpace()+".incOrder", map);
	}
	
	public int incEvaluate(Long masterUsrId, Long cateId, Long respSpeed, Long majorLevel, Long serviceAttitude) {
		JSONObject map = new JSONObject();
		map.put("masterUsrId" , masterUsrId);
		map.put("cateId" , cateId);
		map.put("respSpeed" , respSpeed);
		map.put("majorLevel" , majorLevel);
		map.put("serviceAttitude" , serviceAttitude);
		map.put("updateTime", new Date());
		logger.info("===== increase master statistics for evaluate: "+map.toJSONString()+"=====");
		return this.getSqlSession().update(this.getNameSpace()+".incEvaluate", map);
	}
	
	public List<Map<String, Object>> searchMastersByUser(Long filterCateId, String orderType, String ascType) {
		JSONObject map = new JSONObject();
		map.put("cateId" , filterCateId);
		map.put("orderType" , orderType);
		map.put("ascType" , ascType);
		
		return this.getSqlSession().selectList(this.getNameSpace()+".searchMastersByUser", map);
	}
	
	public List<FsMasterStatistics> findByUsrIds(List<Long> usrIdList, Long cateId) {
		JSONObject map = new JSONObject();
		map.put("usrIdList", usrIdList);
		map.put("cateId" , cateId);
		return this.getSqlSession().selectList(this.getNameSpace()+".findByUsrIds", map);
	}
}
