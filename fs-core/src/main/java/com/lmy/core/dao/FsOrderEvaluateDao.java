package com.lmy.core.dao ;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsOrderEvaluate;
@Component
public class FsOrderEvaluateDao extends GenericDAOImpl<FsOrderEvaluate> {
	@Override
	public String getNameSpace() {
		return "fs_order_evaluate";
	}
	
	public List<FsOrderEvaluate> findByContion1(Long orderId ,Long sellerUsrId , Long buyUsrId){
		Assert.isTrue( !(orderId == null && sellerUsrId == null && buyUsrId == null)  );
		JSONObject map = new JSONObject();
		map.put("orderId", orderId);
		map.put("sellerUsrId", sellerUsrId);
		map.put("buyUsrId", buyUsrId);
		return this.getSqlSession().selectList(this.getNameSpace()+".findByContion1", map);
	}
	
	public Long statEvaluateNum(Long orderId ,Long sellerUsrId , Long buyUsrId){
		Assert.isTrue( !(orderId == null && sellerUsrId == null && buyUsrId == null)  );
		JSONObject map = new JSONObject();
		map.put("orderId", orderId);
		map.put("sellerUsrId", sellerUsrId);
		map.put("buyUsrId", buyUsrId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".statEvaluateNum", map);
	}
	/** if not found return null  **/
	public Map statMasterAvgScore(Long orderId ,Long sellerUsrId , Long buyUsrId){
		Assert.isTrue( !(orderId == null && sellerUsrId == null && buyUsrId == null)  );
		JSONObject map = new JSONObject();
		map.put("orderId", orderId);
		map.put("sellerUsrId", sellerUsrId);
		map.put("buyUsrId", buyUsrId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".statMasterAvgScore", map);
	}
	
	public Double statMasterAvgScore2(long sellerUsrId){
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".statMasterAvgScore2", map);
	}

	public List<Map> statMasterAvgScore3(List<Long>sellerUsrIdList ){
		if(CollectionUtils.isEmpty(sellerUsrIdList)){
			return null;
		}
		JSONObject map = new JSONObject();
		map.put("sellerUsrIdList", sellerUsrIdList);
		return this.getSqlSession().selectList(this.getNameSpace()+".statMasterAvgScore3", map);
	}
	
	public List<Map> findMasterEvaluateList(long sellerUsrId,int currentPage,int perPageNum){
		JSONObject map = new JSONObject();
		map.put("sellerUsrId", sellerUsrId);
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findMasterEvaluateList", map);
	}
	
	public List<Map> statAllMasterScore1(String isPlatRecomm,Long zxCateId){
		JSONObject parameter = new JSONObject();
		if(StringUtils.isNotBlank(isPlatRecomm)){
			parameter.put("isPlatRecomm", isPlatRecomm);			
		}
		parameter.put("zxCateId", zxCateId);
		return this.getSqlSession().selectList(this.getNameSpace()+".statAllMasterScore1", parameter);
	}
	
	public List<Map<String, Object>> statEvaluateByMasterCate() {
		return this.getSqlSession().selectList(this.getNameSpace()+".statEvaluateByMasterCate");
	}
	
}
