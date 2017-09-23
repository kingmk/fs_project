package com.lmy.core.beanstalkd.job;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.queue.beanstalkd.QueueHandler;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsChatRecord;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.UsrAidUtil;
@Service
public class OrderChatReadConfirmManagerServiceImpl extends QueueHandler {
	private static Logger logger = LoggerFactory.getLogger(OrderChatReadConfirmManagerServiceImpl.class);
	@Autowired
	private FsChatRecordDao fsChatRecordDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsUsrDao fsUsrDao;	
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	@Override
	public String getQueueName() {
		return QueueNameConstant.ORDER_CHAT_NEWMSG_UNREADCONFIRM;
	}
	/**
	 *  data.put("sentUsrId", sentUsrId);
		data.put("chatRecordId", chatRecordId);
		data.put("sentIsMaster", sentIsMaster);
		data.put("_cacheTime", now);
	 */
	@Override
	public Object hander(JSONObject data) throws Exception {
		try{
			if(data == null || data.isEmpty() || !data.containsKey("chatRecordId")){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			Long chatRecordId = data.getLong("chatRecordId");
			FsChatRecord chatRecord = this.fsChatRecordDao.findById(chatRecordId);
			if(chatRecord == null ){
				logger.warn(" 消息为空 chatRecordId:{}",chatRecordId);
				return null;
			}
			if("Y".equals(chatRecord.getIsRead()) || chatRecord.getReadTime() !=null ){
				/*logger.info(" 消息已读 本次操作忽略 消息丢弃 chatRecordId:{} , orderId:{},readTime:{},createTime:{}"
						,chatRecordId ,chatRecord.getOrderId()
						,CommonUtils.dateToString(chatRecord.getReadTime(), CommonUtils.dateFormat2, "")
						,CommonUtils.dateToString(chatRecord.getCreateTime(), CommonUtils.dateFormat2, ""));*/
				return null;
			}
			if(!"effect".equals(chatRecord.getStatus())){
				logger.info(" 消息已无效 本次操作忽略 消息丢弃 chatRecordId:{} , orderId:{},readTime:{},createTime:{}"
						,chatRecordId ,chatRecord.getOrderId()
						,CommonUtils.dateToString(chatRecord.getReadTime(), CommonUtils.dateFormat2, "")
						,CommonUtils.dateToString(chatRecord.getCreateTime(), CommonUtils.dateFormat2, ""));
				return null;
			}
			FsOrder order = this.fsOrderDao.findById( chatRecord.getOrderId()  );
			if(order == null ){
				logger.warn("订单未查询到 本次操作忽略 消息丢弃 chatRecordId:{} , orderId:{},readTime:{},createTime:{}"
						,chatRecordId ,chatRecord.getOrderId()
						,CommonUtils.dateToString(chatRecord.getReadTime(), CommonUtils.dateFormat2, "")
						,CommonUtils.dateToString(chatRecord.getCreateTime(), CommonUtils.dateFormat2, ""));
				return null;
			}
			if(!"pay_succ".equals(order.getStatus()) && !"completed".equals(order.getStatus()) ){
				logger.warn("订单状态错误 本次操作忽略 消息丢弃 orderId:{},当前状态:{};期望状态:{}", order.getId(), order.getStatus(), "pay_succ , completed");
				return null;
			}
			Date now = new Date();
			if( now.after(order.getEndChatTime()) ){
				logger.warn("订单聊天最大截止时间已过 本次操作忽略 消息丢弃 orderId:{},截止时间:{}", order.getId(), CommonUtils.dateToString(now, CommonUtils.dateFormat2, "")  );
				return null;				
			}

			if( chatRecord.getReceUsrId() .equals( order.getSellerUsrId() )  ){
				handerMasterUnReadMsg(chatRecord, order, now, data);
			}else{
				handerBuyUsrUnReadMsg(chatRecord, order);
			}
		}catch(Exception e){
			logger.error("处理出错 data:{}",data,e);
			pushInQueueAgain(data);
		}
		return null;
	}
	
	private void handerBuyUsrUnReadMsg(FsChatRecord chatRecord , FsOrder order){
		Map<Long,FsUsr> idUsrMap = 	this.fsUsrDao.findByUsrIdsAndConvert(Arrays.asList( order.getBuyUsrId(),order.getSellerUsrId() ));
		List<FsMasterInfo> masterInfoList =  fsMasterInfoDao.findByUsrIds2(Arrays.asList(order.getSellerUsrId()), "approved", null);
		String masterName = null;
		if(CollectionUtils.isNotEmpty(masterInfoList)){
			masterName = UsrAidUtil.getMasterNickName(masterInfoList.get(0), null, null);
		}
		if(StringUtils.isEmpty(masterName)){
			masterName=  UsrAidUtil.getNickName2( idUsrMap.get( order.getSellerUsrId() ) ,"");	
		}
		String replyContent = this.getReplyContentForWxMsg(chatRecord);
		wxNoticeManagerImpl.masterReply30MinUnReadToBuyUsr(order.getId(), order.getChatSessionNo(), order.getGoodsName(), replyContent, chatRecord.getId(),
				masterName, idUsrMap.get(order.getBuyUsrId()).getId(), idUsrMap.get(order.getBuyUsrId()).getWxOpenId());
	}
	
	private String getReplyContentForWxMsg(FsChatRecord chatRecord){
		if("img".equals(chatRecord.getMsgType())){
			return "[图片]";
		}else{
			return chatRecord.getContent().length()>20 ? chatRecord.getContent().substring(0, 20)+"..."  : chatRecord.getContent();
		}
	}
	
	private void handerMasterUnReadMsg(FsChatRecord unReadChatRecord , FsOrder order,Date now ,JSONObject beanstalkd){
		//10分钟 -->修改为立即发送(延迟2秒)   modify by fidel at 2017/06/01 18:29 
		//客户发送消息后如果10分钟内未读，则向老师推送，但如果该新消息之前已经推送过一条新消息通知，并且该新消息未被老师读取，则不会再做推送
		//查看 之前是否已经发送过消息
		String key =   CacheConstant.ORDER_CHAT_MASTER_UNREAD_LAST_PUSH + order.getId();
		//JSON 字符串 定包含 chatRecordId
		JSONObject cacheValue = (JSONObject)RedisClient.get(key);
		boolean needSentWxMsg  = false;
		if(cacheValue !=null){
			Long lastUnReadchatRecordId = cacheValue.getLong("chatRecordId");
			FsChatRecord lastUnReadcChatRecord = this.fsChatRecordDao.findById(lastUnReadchatRecordId);
			if(lastUnReadcChatRecord ==null ||lastUnReadcChatRecord.getReadTime()==null ){
				needSentWxMsg = true;
				if(logger.isDebugEnabled())	{
					logger.debug("orderId:{} 上一次未读的消息 lastUnReadchatRecordId:{}  master 未读 现2秒内(后)又出现未读 上一次都未读,本次需要 发送微信通知",
							order.getId() ,lastUnReadchatRecordId );	
				}
			}
			else if(lastUnReadcChatRecord.getReadTime()!=null &&  CommonUtils.calculateDiffSeconds(lastUnReadcChatRecord.getReadTime(), new Date()) > 2  ){
				needSentWxMsg = true;
				if(logger.isDebugEnabled())	{
					logger.debug("orderId:{} 上一次未读的消息 lastUnReadchatRecordId:{}  master 已读 现1秒中内(后)上一次已读时间:{},超过 2秒  本次需要 发送微信通知",
							order.getId() ,lastUnReadchatRecordId ,CommonUtils.dateToString(lastUnReadcChatRecord.getReadTime(), CommonUtils.dateFormat2, "")  );					
				}
			}else{
				if(logger.isDebugEnabled())	{
					logger.debug("orderId:{} 上一次未读的消息 chatRecordId:{}  master 已读 现2秒内(后)又出现未读2秒中后未读,不再push微信信息. 本次操作忽略 消息丢弃  cacheValue:{}",
							order.getId() ,unReadChatRecord.getId(),cacheValue );								
				}
			}
		}else{
			needSentWxMsg = true;
		}
		if(needSentWxMsg){
			if(logger.isDebugEnabled())	{
				logger.debug("老师2秒内未读 , orderId:{} ,chatRecordId:{},聊天记录创建时间:{} 发送微信消息", unReadChatRecord.getOrderId(),unReadChatRecord.getId(), CommonUtils.dateToString(unReadChatRecord.getCreateTime(), CommonUtils.dateFormat2, "") );			
			}
			Map<Long,FsUsr> idUsrMap = 	this.fsUsrDao.findByUsrIdsAndConvert(Arrays.asList( order.getBuyUsrId(),order.getSellerUsrId() ));
			String buyUsrName = 	UsrAidUtil.getNickName2(idUsrMap.get( order.getBuyUsrId() ), "匿名");
			this.wxNoticeManagerImpl.masterNewMsgUnRead10min(order, unReadChatRecord, 	idUsrMap.get(order.getSellerUsrId()).getWxOpenId(), buyUsrName);
			RedisClient.set(key, beanstalkd,  RedisClient.oneDaySec );			
		}
	}
	private void pushInQueueAgain(JSONObject data){
		Integer _errorTimes = data.getInteger("_errorTimes");
		if(_errorTimes == null ){
			_errorTimes = 0;
		}
		_errorTimes = _errorTimes + 1;
		if(_errorTimes>3){
			logger.error("聊天会话记录 3次确认未成功 本次放弃 等待 人工处理 data:{}", data);
			return ;
		}
		data.put("_errorTimes", _errorTimes);
		logger.info("聊天会话记录 碰到问题 3 秒后再一次确认 data:{}", data);
		this.put(null, 3, null, data);
	}
}
