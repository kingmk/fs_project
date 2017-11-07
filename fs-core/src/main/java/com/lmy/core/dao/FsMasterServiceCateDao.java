package com.lmy.core.dao ;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsMasterServiceCate;
@Component
public class FsMasterServiceCateDao extends GenericDAOImpl<FsMasterServiceCate> {
	@Override
	public String getNameSpace() {
		return "fs_master_service_cate";
	}
	
	public List<FsMasterServiceCate> findByUsrIdAndStatus(Long usrId, List<String> statusList){
		if(usrId ==null){
			return null;
		}
		JSONObject map = new JSONObject();
		map.put("usrId", usrId);
		if(CollectionUtils.isNotEmpty(statusList)){
			map.put("statusList", statusList);
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findByUsrIdAndStatus", map);
	}
	public Long statServiceRecordNum(Long usrId , Long  masterInfoId , 	List<String> statusList ){
		Assert.isTrue( !(usrId==null && masterInfoId == null)   );
		JSONObject map = new JSONObject();
		map.put("usrId", usrId);
		map.put("masterInfoId", masterInfoId);
		if(CollectionUtils.isNotEmpty(statusList)){
			map.put("statusList", statusList);	
		}
		return this.getSqlSession().selectOne(this.getNameSpace()+".statServiceRecordNum", map);
	}
	
	public List<FsMasterServiceCate> findByMasterInfoIdAndCateId(Long masterInfoId,Long cateId , List<String> statusList){
		JSONObject map = new JSONObject();
		map.put("masterInfoId", masterInfoId);
		map.put("cateId", cateId);
		if(CollectionUtils.isNotEmpty(statusList)){
			map.put("statusList", statusList);
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findByMasterInfoIdAndCateId", map);
	}
	/**
	 * 每个用户取一条数据
	 * @param orderBy priceDesc,priceAsc
	 * @return
	 */
	public List<FsMasterServiceCate> findOneOrderByPriceGroupByUsrId(List<Long> usrIdList,  List<String>statusList, String isPlatRecomm , Long zxCateId,   String orderBy,int currentPage,int perPageNum ){
		JSONObject parameter = new JSONObject();
		if(CollectionUtils.isNotEmpty(usrIdList)){
			parameter.put("usrIdList", usrIdList);
		}
		if(CollectionUtils.isNotEmpty(statusList)){
			parameter.put("statusList", statusList);
		}
		if(StringUtils.isNotBlank(orderBy)){
			parameter.put("orderBy", orderBy);			
		}
		if(StringUtils.isNotBlank(isPlatRecomm)){
			parameter.put("isPlatRecomm", isPlatRecomm);			
		}
		parameter.put("zxCateId", zxCateId);			
		parameter.put("limitBegin", perPageNum * currentPage);
		parameter.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findOneOrderByPriceGroupByUsrId", parameter);
	}
	/**
	 * 
	 * @param zxCateId
	 * @param orderBy  orderNumDesc,orderNumAsc
	 * @param currentPage
	 * @param perPageNum
	 * @return
	 */
	public List<FsMasterServiceCate> findOrderByOrerNum(Long zxCateId,  String orderBy,int currentPage,int perPageNum ){
		JSONObject parameter = new JSONObject();
		parameter.put("orderBy", orderBy);
		parameter.put("zxCateId", zxCateId);
		parameter.put("limitBegin", perPageNum * currentPage);
		parameter.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findOrderByOrerNum", parameter);
	}
	
	public List<FsMasterServiceCate> findByIds(List<Long> idList){
		if(CollectionUtils.isEmpty(idList)){
			return null;
		}
		JSONObject parameter = new JSONObject();
		parameter.put("idList", idList);		
		return this.getSqlSession().selectList(this.getNameSpace()+".findByIds", parameter);
	}
	
	public int offlineMasterCateService(Long masterInfoId, Long cateId) {
		JSONObject parameter = new JSONObject();
		parameter.put("masterInfoId", masterInfoId);
		parameter.put("cateId", cateId);
		
		return this.getSqlSession().update(this.getNameSpace()+".offlineMasterCateService", parameter);
	}
}
