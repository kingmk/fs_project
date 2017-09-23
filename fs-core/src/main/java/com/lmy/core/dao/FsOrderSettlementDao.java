package com.lmy.core.dao ;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsOrderSettlement;
@Component
public class FsOrderSettlementDao extends GenericDAOImpl<FsOrderSettlement> {
	@Override
	public String getNameSpace() {
		return "fs_order_settlement";
	}
	public FsOrderSettlement stat1(long sellerUsrId , List<String> statusList){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		if(CollectionUtils.isNotEmpty(statusList)){
			parameter.put("statusList", statusList);	
		}
		return this.getSqlSession().selectOne( this.getNameSpace()+".stat1",  parameter);
	}
	public List<FsOrderSettlement> find1(long sellerUsrId , List<String> statusList,int currentPage,int perPageNum){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		if(CollectionUtils.isNotEmpty(statusList)){
			parameter.put("statusList", statusList);	
		}
		parameter.put("limitBegin", perPageNum * currentPage);
		parameter.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList( this.getNameSpace()+".find1",  parameter);
	}
}
