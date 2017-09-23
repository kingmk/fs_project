package com.lmy.core.dao ;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.dto.MasterShortDto1;
@Component
public class FsMasterInfoDao extends GenericDAOImpl<FsMasterInfo> {
	@Override
	public String getNameSpace() {
		return "fs_master_info";
	}
	
	public List<FsMasterInfo> findBtCondition1(Long id ,Long usrId,  String wxOpenId , List<String> auditStatusList , List<String> serviceStatusList){
		JSONObject map = new JSONObject();
		map.put("usrId",usrId);
		map.put("id",id);
		if(StringUtils.isEmpty(wxOpenId)){
			map.put("wxOpenId",wxOpenId);
		}
		if(CollectionUtils.isNotEmpty(auditStatusList)){
			map.put("auditStatusList",auditStatusList);
		}
		if(CollectionUtils.isNotEmpty(serviceStatusList)){
			map.put("serviceStatusList",serviceStatusList);
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findBtCondition1", map);
	}
	
	public Long statRecordNum1(Long usrId , List<String> auditStatusList , List<String> serviceStatusList){
		JSONObject map = new JSONObject();
		map.put("usrId",usrId);
		if(CollectionUtils.isNotEmpty(auditStatusList)){
			map.put("auditStatusList",auditStatusList);
		}
		if(CollectionUtils.isNotEmpty(serviceStatusList)){
			map.put("serviceStatusList",serviceStatusList);
		}
		return this.getSqlSession().selectOne(this.getNameSpace()+".statRecordNum1", map);
	}
	
	public int configOderTaking(Long fsMasterInfoId ,  Long usrId , String serviceStatus){
		Assert.isTrue( StringUtils.isNotEmpty(serviceStatus) );
		Assert.isTrue( !( fsMasterInfoId == null && usrId == null )   );
		JSONObject map = new JSONObject();
		map.put("id" , fsMasterInfoId);
		map.put("usrId" , usrId);
		map.put("serviceStatus" , serviceStatus);
		return this.getSqlSession().update(this.getNameSpace()+".configOderTaking", map);
	}
	public List<FsMasterInfo> findByUsrIds2(List<Long> usrIdList,String auditStatus , String serviceStatus){
		Assert.isTrue(  CollectionUtils.isNotEmpty(usrIdList)  );
		JSONObject map = new JSONObject();
		map.put("usrIdList", usrIdList);
		if(StringUtils.isNotBlank(auditStatus)){
			map.put("auditStatus", auditStatus);	
		}
		if(StringUtils.isNotBlank(serviceStatus)){
			map.put("serviceStatus", serviceStatus);	
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findByUsrIds2", map);
	}
	
	public List<MasterShortDto1> findShortInfo1(String auditStatus, String status,String isPlatRecomm,Long zxCateId){
		JSONObject map = new JSONObject();
		if(StringUtils.isNotBlank(auditStatus)){
			map.put("auditStatus", auditStatus);
		}
		if(StringUtils.isNotBlank(status)){
			map.put("status", status);
		}
		if(StringUtils.isNotBlank(isPlatRecomm)){
			map.put("isPlatRecomm", isPlatRecomm);			
		}
		map.put("zxCateId", zxCateId);
		return this.getSqlSession().selectList(this.getNameSpace()+".findShortInfo1", map);
	}
	
}
