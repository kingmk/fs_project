package com.lmy.core.dao ;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsChatSession;
@Component
public class FsChatSessionDao extends GenericDAOImpl<FsChatSession> {
	@Override
	public String getNameSpace() {
		return "fs_chat_session";
	}
	
	public List<FsChatSession> findByChatSessionNo(String chatSessionNo){
		Assert.isTrue(  StringUtils.isNotEmpty(chatSessionNo));
		JSONObject map = new JSONObject();
		map.put("chatSessionNo", chatSessionNo);
		return this.getSqlSession().selectList(this.getNameSpace()+".findByChatSessionNo", map);
	}
	
	public List<FsChatSession> updateExpiryDateByChatSessionNo(String chatSessionNo,Date now){
		Assert.isTrue( StringUtils.isNotEmpty(chatSessionNo) );
		if(now == null ){
			now = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("chatSessionNo", chatSessionNo);	
		map.put("expiryDate", now);
		return this.getSqlSession().selectList(this.getNameSpace()+".updateExpiryDateByChatSessionNo", map);
	}
	
	
	
	
}
