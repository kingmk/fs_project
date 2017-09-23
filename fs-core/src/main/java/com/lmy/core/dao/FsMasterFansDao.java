package com.lmy.core.dao ;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsMasterFans;
@Component
public class FsMasterFansDao extends GenericDAOImpl<FsMasterFans> {
	@Override
	public String getNameSpace() {
		return "fs_master_fans";
	}
	/** 取消关注 **/
	public int unfollow(Long id , long followUsrId ,long focusUsrId){
		JSONObject map = new JSONObject();
		map.put("id" , id);
		map.put("followUsrId" , followUsrId);
		map.put("focusUsrId" , focusUsrId);
		map.put("updateTime" , new Date());
		return this.getSqlSession().update( this.getNameSpace()+".unfollow" , map);
	}
	
	/** 关注  能够处理在一次关注 **/
	public FsMasterFans follow(Long id , long followUsrId ,long focusUsrId){
		Date now  = new Date();
		JSONObject map = new JSONObject();
		if(id == null){
			FsMasterFans beanForInsert = new FsMasterFans();
			beanForInsert.setId(id);
			beanForInsert.setUpdateTime(now).setCreateTime(now);
			beanForInsert.setFollowUsrId(followUsrId).setFocusUsrId(focusUsrId).setStatus("followed");
			this.insert(beanForInsert);
			return beanForInsert;
		}else{
			map.put("id" , id);
			map.put("followUsrId" , followUsrId);
			map.put("focusUsrId" , focusUsrId);
			map.put("updateTime" , new Date());
			 this.getSqlSession().update( this.getNameSpace()+".followagain" , map);
			FsMasterFans bean = new FsMasterFans();
			bean.setId(id);
			bean.setUpdateTime(now);
			bean.setFollowUsrId(followUsrId).setFocusUsrId(focusUsrId).setStatus("followed");
			return bean;
		}
	}
	/** 统计有效的 粉丝数 **/ 
	public Long statEffectFansNum(long focusUsrId){
		JSONObject map = new JSONObject();
		map.put("focusUsrId" , focusUsrId);
		return this.getSqlSession().selectOne( this.getNameSpace()+".statEffectFansNum" , map);
	}
	
	/** 用户${followUsrId  当前是否有关注 ${focusUsrId} **/ 
	public boolean isCurrFollowed( long followUsrId ,long focusUsrId){
		JSONObject map = new JSONObject();
		map.put("followUsrId" , followUsrId);
		map.put("focusUsrId" , focusUsrId);
		Long num =   this.getSqlSession().selectOne( this.getNameSpace()+".isCurrFollowed" , map);
		return num >0 ;
	}
	/**  查询到关注的人id(masterUsrId) **/
	public List<Long> findCurFocusUsrIdsByFollowUsrId(long followUsrId , int currentPage,int perPageNum){
		JSONObject map = new JSONObject();
		map.put("followUsrId" , followUsrId);
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findCurFocusUsrIdsByFollowUsrId", map);
	}
	
}
