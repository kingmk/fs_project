package com.lmy.core.dao ;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsZxCate;
@Component
public class FsZxCateDao extends GenericDAOImpl<FsZxCate> {
	@Override
	public String getNameSpace() {
		return "fs_zx_cate";
	}
	
	public List<FsZxCate> findZxCate1(Long id , Long parentId , Long level , String usrDefined, String status){
		JSONObject map = new JSONObject();
		map.put("id", id);
		map.put("parentId", parentId);
		map.put("level", level);
		if(StringUtils.isNotEmpty(usrDefined)){
			map.put("usrDefined", usrDefined);	
		}
		if(StringUtils.isNotEmpty(status)){
			map.put("status", status);
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findZxCate1",map);
	}
	
	/** 查询所有可配置的服务, 包括自定义的  **/
	public List<FsZxCate> findZxCate2(Long level , String usrDefined, String status,Long customUsrId){
		JSONObject map = new JSONObject();
		map.put("level", level);
		if(StringUtils.isNotEmpty(usrDefined)){
			map.put("usrDefined", usrDefined);	
		}
		if(StringUtils.isNotEmpty(status)){
			map.put("status", status);
		}
		map.put("customUsrId" , customUsrId);
		return this.getSqlSession().selectList(this.getNameSpace()+".findZxCate2",map);
	}
	
	public List<FsZxCate> findByIds(List<Long> ids){
		if(CollectionUtils.isEmpty(ids)){
			return null;
		}
		JSONObject map = new JSONObject();
		map.put("ids", ids);
		return this.getSqlSession().selectList(this.getNameSpace()+".findByIds", map);
	}

	/**  method never return null  if not found return new HashMap<Long,FsZxCate>()**/
	public Map<Long ,FsZxCate> findByIdsForResutMap(List<Long> ids){
		List<FsZxCate>list = this.findByIds(ids);
		Map<Long ,FsZxCate> map = new HashMap<Long,FsZxCate>();
		if(CollectionUtils.isNotEmpty(list)){
			for(FsZxCate bean : list){
				map.put(bean.getId(), bean);
			}
		}
		return map;
	}
}
