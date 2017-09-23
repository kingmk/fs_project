package com.lmy.core.dao ;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsUsr;
@Component
public class FsUsrDao extends GenericDAOImpl<FsUsr> {
	@Override
	public String getNameSpace() {
		return "fs_usr";
	}
	
	public FsUsr findByWxOpenIdOrId(String wxOpenId,Long id){
		Assert.isTrue( ! (StringUtils.isEmpty(wxOpenId) && id ==null   )   );
		JSONObject map = new JSONObject();
		map.put("id", id);
		if(StringUtils.isNotEmpty(wxOpenId)){
			map.put("wxOpenId", wxOpenId);
		}
		return this.getSqlSession().selectOne(this.getNameSpace()+".findByWxOpenIdOrId", map);
	}
	
	public Long statNumByMobile(String mobile){
		Assert.isTrue( StringUtils.isNotEmpty(mobile) );
		JSONObject map = new JSONObject();
		map.put("mobile", mobile);
		return this.getSqlSession().selectOne(this.getNameSpace()+".statNumByMobile", map);
	}
	/** never return null  ( id,wx_open_id,register_mobile,usr_head_img_url,real_name,nick_name,english_name)**/
	public Map<Long,FsUsr> findShortInfo1ByUsrIds(List<Long> idList){
		Map<Long , FsUsr > result = new HashMap<Long,FsUsr>();
		if(CollectionUtils.isEmpty(idList)){
			return result;
		}
		JSONObject parameter = new JSONObject();
		parameter.put("idList", idList);
		@SuppressWarnings("rawtypes")
		List<FsUsr> list = this.getSqlSession().selectList(this.getNameSpace()+".findShortInfo1ByUsrIds", parameter);
		if(CollectionUtils.isNotEmpty(list)){
			for(FsUsr bean : list){
				result.put(bean.getId(),bean);
			}
		}
		return result;
	}
	
	public List<FsUsr> findByUsrIds(List<Long> idList){
		JSONObject parameter = new JSONObject();
		parameter.put("idList", idList);
		return  this.getSqlSession().selectList(this.getNameSpace()+".findByUsrIds", parameter);
	}
	public Map<Long,FsUsr> findByUsrIdsAndConvert(List<Long> idList){
		Map<Long , FsUsr > result = new HashMap<Long,FsUsr>();
		List<FsUsr> list = findByUsrIds(idList);
		if(CollectionUtils.isNotEmpty(list)){
			for(FsUsr bean : list){
				result.put(bean.getId(),bean);
			}
		}
		return result;
	}
	
	
}
