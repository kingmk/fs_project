package com.lmy.core.dao ;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsChatRecord;
@Component
public class FsChatRecordDao extends GenericDAOImpl<FsChatRecord> {
	@Override
	public String getNameSpace() {
		return "fs_chat_record";
	}
	/** if not found return null   注意该 方法没有头像信息 **/
	public List<com.lmy.core.model.dto.FsChatRecordDto> findChatRecord(Long gtChatId ,Long ltChatId , List<Long>  excludeChatIds, Long orderId , String chatSessionNo,Integer currentPage,Integer perPageNum , String status ){
		if(currentPage == null  || currentPage < 0){
			currentPage = 0;
		}
		if(perPageNum == null || perPageNum < 1){
			perPageNum = 10;
		}
		if(StringUtils.isEmpty(status)){
			status = "effect";
		}
		JSONObject map = new JSONObject();
		map.put("orderId", orderId);
		map.put("gtChatId", gtChatId);
		map.put("ltChatId", ltChatId);
		map.put("chatSessionNo", chatSessionNo);
		map.put("status", status);
		if(CollectionUtils.isNotEmpty(excludeChatIds)){
			map.put("excludeChatIds", excludeChatIds);
		}
		map.put("limitBegin", perPageNum * currentPage);
		map.put("limitEnd", perPageNum);
		List<com.lmy.core.model.dto.FsChatRecordDto> list =  getSqlSession().selectList(this.getNameSpace()+".findChatRecord", map);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}else{
			return list;
		}
	}
	/**
	 *  按角色  统计未读数
	 * @param chatSessionNo  if null 统计所有未读数;否则 统计某一次回话未读数
	 * @param receUsrId
	 * @return
	 */
	public Long statUnReadNum1(String chatSessionNo , String isMaster, long receUsrId){
		JSONObject map = new JSONObject();
		map.put("receUsrId", receUsrId);
		if(StringUtils.isNotEmpty( chatSessionNo )){
			map.put("chatSessionNo", chatSessionNo);
		}
		if(StringUtils.isNotEmpty(isMaster)){
			map.put("isMaster", isMaster);
		}
		return this.getSqlSession().selectOne(this.getNameSpace()+".statUnReadNum1", map);
	}
	
	/**
	 *  统计未读数 
	 * @param chatSessionNoList if empty return new HashMap<String,Long>
	 * @param receUsrId
	 * @return key is chatSession ; value is 未读数目 default is 0
	 */
	public Map<String , Long> statUnReadNum2(List<String> chatSessionNoList ,  long receUsrId){
		Map<String , Long> result =  new HashMap<String,Long>(); 
		if(CollectionUtils.isEmpty(chatSessionNoList)){
			return result;
		}
		JSONObject map = new JSONObject();
		map.put("receUsrId", receUsrId);
		map.put("chatSessionNoList", chatSessionNoList);
		List<Map> list = this.getSqlSession().selectList(this.getNameSpace()+".statUnReadNum2", map);
		if(CollectionUtils.isNotEmpty(list)){
			for(Map _map : list){
				result.put((String)_map.get("session_no"),  (Long)_map.get("num") );
			}
		}
		for(String chatSessionNo :  chatSessionNoList){
			if( result.get(chatSessionNo) ==null ){
				result.put(chatSessionNo, 0l);
			}
		}
		return result;
	}
	/**
	 * @param chatSessionNo
	 * @param receUsrId  if is null 查询最后一句聊天 可能是老师的， 可能是咨询人的 comment at 2017/05/31 
	 * @return 查询用户${receUsrId} 收到的最近一笔回复，if chatSessionNo is null 查询所有回话中的最近一条回话;否则查询某次回话${chatSessionNo} 收到的最近一笔回复
	 */
	public com.lmy.core.model.dto.FsChatRecordDto findUsrReceLastReply(String chatSessionNo, Long receUsrId){
		JSONObject map = new JSONObject();
		map.put("chatSessionNo", chatSessionNo);
		map.put("receUsrId", receUsrId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findUsrReceLastReply",map  );
	}
	
	public Long statReplyNum1(Long orderId , String chatSessionNo , Long sentUsrId ,   List<Long> excludeChatIds  ){
		JSONObject map = new JSONObject();
		map.put("orderId", orderId);
		if(StringUtils.isNotEmpty(chatSessionNo)){
			map.put("orderId", orderId);
		}
		if(CollectionUtils.isNotEmpty( excludeChatIds )){
			map.put("excludeChatIds", excludeChatIds);	
		}
		map.put("sentUsrId", sentUsrId);
		return this.getSqlSession().selectOne(this.getNameSpace()+".statReplyNum1",map  );
	}
	
}
