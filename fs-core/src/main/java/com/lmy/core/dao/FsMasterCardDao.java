package com.lmy.core.dao;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsMasterCard;

@Component
public class FsMasterCardDao extends GenericDAOImpl<FsMasterCard> {

	@Override
	public String getNameSpace() {
		return "fs_master_card";
	}
	/** Get Master Card by master info id **/ 
	public FsMasterCard findByMasterId(Long masterUsrId){
		JSONObject map = new JSONObject();
		map.put("masterUsrId" , masterUsrId);
		return this.getSqlSession().selectOne( this.getNameSpace()+".findByMasterId" , map);
	}

}
