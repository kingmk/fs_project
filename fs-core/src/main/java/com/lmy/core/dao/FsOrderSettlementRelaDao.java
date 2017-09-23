package com.lmy.core.dao ;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsOrderSettlementRela;
@Component
public class FsOrderSettlementRelaDao extends GenericDAOImpl<FsOrderSettlementRela> {
	@Override
	public String getNameSpace() {
		return "fs_order_settlement_rela";
	}
	public List<Long> findOrderIds1(long orderSettlementId , long sellerUsrId){
		JSONObject parameter = new JSONObject();
		parameter.put("sellerUsrId", sellerUsrId);	
		parameter.put("orderSettlementId", orderSettlementId);	
		return this.getSqlSession().selectList(this.getNameSpace()+".findOrderIds1", parameter);
	}
	
}
