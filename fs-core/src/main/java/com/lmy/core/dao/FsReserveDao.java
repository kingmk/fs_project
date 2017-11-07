package com.lmy.core.dao;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsReserve;

@Component
public class FsReserveDao extends GenericDAOImpl<FsReserve> {

	@Override
	public String getNameSpace() {
		return "fs_reserve";
	}
	
	public FsReserve findReserve(long reserveUsrId, long masterUsrId) {
		JSONObject map = new JSONObject();
		map.put("reserveUsrId", reserveUsrId);
		map.put("masterUsrId", masterUsrId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findReserve", map);
		
	}

	public List<FsReserve> findByMasterUsrId(long masterUsrId) {
		return this.getSqlSession().selectList(this.getNameSpace()+".findByMasterUsrId", masterUsrId);
	}

	public List<FsReserve> findByReserveUsrId(long reserveUsrId) {
		return this.getSqlSession().selectList(this.getNameSpace()+".findByReserveUsrId", reserveUsrId);
	}
	
	public int noticeReserveUsrByMaster(long masterUsrId, Date now) {
		JSONObject map = new JSONObject();
		map.put("masterUsrId", masterUsrId);
		map.put("noticeTime", now);
		return this.getSqlSession().update(this.getNameSpace()+".noticeReserveUsrByMaster", map);
	}
}
