package com.lmy.core.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.beanstalkd.job.QueueNameConstant;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.dao.FsChatSessionDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsChatRecord;
import com.lmy.core.model.FsChatSession;
import com.lmy.core.model.FsFileStore;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsUsr;
import com.lmy.core.utils.FsExecutorUtil;
@Service
public class OrderChatServiceImpl {
	private static final Logger logger = Logger.getLogger(OrderChatServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired 
	private FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsChatSessionDao fsChatSessionDao;
	@Autowired
	private FileStoreServiceImpl fileStoreServiceImpl;
	@Autowired
	private FsChatRecordDao fsChatRecordDao;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	/**
	 * 参数校验 @see method chatParameterCheck
	 * @param chatSessionNo
	 * @param orderId
	 * @param isEscape
	 * @param content
	 * @param sendUsrId
	 * @return
	 */
	public JSONObject handMsg(String clientUniqueNo, String chatSessionNo, final Long orderId, String msgType, String isEscape, String content , CommonsMultipartFile img, long sentUsrId){
		final Date now   = new Date();
		JSONObject checkResult = chatParameterCheck(clientUniqueNo,chatSessionNo, orderId, msgType, content, img, sentUsrId , now);
		if(!JsonUtils.equalDefSuccCode(checkResult)){
			logger.warn(checkResult);
			return checkResult;
		}
		@SuppressWarnings("unchecked")
		List<FsChatSession> chatSessionList =(List<FsChatSession>) JsonUtils.getBodyValue(checkResult, "chatSessionList");
		final FsChatRecord beanForInsert = new FsChatRecord();
		if("img".equals(msgType)){
			FsFileStore files1 =  fileStoreServiceImpl.fileStore(img, FileStoreServiceImpl.FileType.CHAT , sentUsrId);
			if(files1==null){
				return JsonUtils.commonJsonReturn("9999", "系统错误");
			}
			content = files1.getHttpUrl();
			beanForInsert.setSize( files1.getFileSize() ).setSuffixName( files1.getSuffixName()  ).setWidth( files1.getWidth()  ).setHeight(  files1.getHeight() ) .setFileStoreId( files1.getId() ) ;
		}
		Long serviceProviderUsrId = getServiceProviderUsrId(chatSessionList);
		//当前发送消息人是否为服务提供者(master 风水师)
		boolean sentIsMaster = serviceProviderUsrId.equals(sentUsrId);
		beanForInsert.setContent(content).setClientUniqueNo(clientUniqueNo)  .setCreateTime(now);
		beanForInsert.setIsEscape(isEscape).setIsRead("N").setMsgType(msgType).setOrderId(orderId).setReadTime(null).setReceUsrId( getReceUsrId(chatSessionList, sentUsrId)   )
		.setSentUsrId(sentUsrId).setSessionNo(chatSessionNo).setSort(0l).setStatus("effect").setUpdateTime(now);
		//add by fidel at 2017/05/31 11:22
		beanForInsert.setSentIsMaster( sentIsMaster?"Y":"N" );
		if(beanForInsert.getSize()==null){
			beanForInsert.setSize( Long.valueOf(content.length()) );
		}
		try{
			fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					fsChatRecordDao.insert(beanForInsert);
					fsOrderDao.updateLastChatTime(orderId);
					return true;
				}
			});
			msgEventHandler(orderId, chatSessionNo, beanForInsert, sentIsMaster, now);
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "id", beanForInsert.getId());
			JsonUtils.setBody(result, "clientUniqueNo", clientUniqueNo);
			return result;
		}catch(Exception e){
			logger.warn("chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sentUsrId,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	private void msgEventHandler(long orderId,String  chatSessionNo, FsChatRecord chatReply , boolean sentIsMaster,Date now){
		JSONObject data = new JSONObject();
		data.put("sentUsrId", chatReply.getSentUsrId());
		data.put("chatRecordId", chatReply.getId());
		data.put("sentIsMaster", sentIsMaster);
		data.put("orderId", orderId);
		data.put("_cacheTime", now);
		if(sentIsMaster){
			//是否为老师第一次回复
			FsOrder order = fsOrderDao.findById(orderId);
			if (order.getSellerFirstReplyTime() == null) {
				// it's the first time for the master to reply
				asynHandIfMasterFirstReply(orderId, chatSessionNo, chatReply, now);
			} else {
				if(logger.isDebugEnabled()){
					logger.debug("老师回复 压入一个msg buyUsr 1秒钟 未读确认  orderId:"+orderId+",chatSessionNo:"+chatSessionNo+",sentIsMaster:"+sentIsMaster+",chatRecordId:"+chatReply.getId());
				}
				//10分钟 -->修改为立即发送(延迟2秒)   modify by fidel at 2017/06/01 18:29
				data.put("msgType", QueueNameConstant.MSG_ORDER_USER_NOTIFY);
				BeanstalkClient.put(QueueNameConstant.QUEUE_ORDER_CHAT, null, 2 , null, data);
			}
			data.put("msgType", QueueNameConstant.MSG_ORDER_USER_UNREAD_CHECK);
			BeanstalkClient.put(QueueNameConstant.QUEUE_ORDER_CHAT, null, 60*10, null, data);
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("buyUsr 回复 压入一个msg master 1秒钟 未读确认  orderId:"+orderId+",chatSessionNo:"+chatSessionNo+",sentIsMaster:"+sentIsMaster+",chatRecordId:"+chatReply.getId());
			}
			//30分钟 -->修改为立即发送(延迟1秒)   modify by fidel at 2017/06/01 18:29 
			data.put("msgType", QueueNameConstant.MSG_ORDER_MASTER_NOTIFY);
			BeanstalkClient.put(QueueNameConstant.QUEUE_ORDER_CHAT, null, 2 , null, data);
			
			data.put("msgType", QueueNameConstant.MSG_ORDER_MASTER_UNREAD_CHECK);
			BeanstalkClient.put(QueueNameConstant.QUEUE_ORDER_CHAT, null, 60*10, null, data);
		}
	}
	
	private void asynHandIfMasterFirstReply(final long orderId , final String chatSessionNo,  final FsChatRecord chatReply , final Date now ){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				_handleMasterFirstReply(orderId, chatSessionNo, chatReply, now);
			}
		};
		FsExecutorUtil.getExecutor().execute(r);
	}
	
	
	private boolean _handleMasterFirstReply(final long orderId , final String chatSessionNo, final FsChatRecord chatReply , Date now ){
		boolean isMasterFirstReply = false;
		try{
			String key =  CacheConstant.ORDER_CHAT_MASTER_FIRST_REPLY+ orderId;
			//第一步cache 判断
			 Object cacheValue =  RedisClient.get(key);
			 if(cacheValue ==null ){
				 Long num = this.fsChatRecordDao.statReplyNum1(orderId, chatSessionNo, chatReply.getSentUsrId(), Arrays.asList(chatReply.getId()) );
				 if(num.equals(0l)){
					 isMasterFirstReply = true;
				 }
			 }
			 if(isMasterFirstReply){
				logger.info("老师第一次回复  chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",sendUsrId:"+chatReply.getSentUsrId());
				FsOrder order = this.fsOrderDao.findById(orderId);
				final Date completedTime =   DateUtils.addDays(now, 1)  ;
				Date settlementTime =   DateUtils.addDays(completedTime, 7) ;
				//在订单完成后的第7个自然日22点是结算时间
				settlementTime = DateUtils.setHours(settlementTime, 22);
				settlementTime = DateUtils.setMinutes(settlementTime, 0);
				settlementTime = DateUtils.setSeconds(settlementTime, 0);
				settlementTime = DateUtils.setMilliseconds(settlementTime, 0);
				final FsOrder orderForUpdate = new FsOrder();
				orderForUpdate.setId(orderId);
				orderForUpdate.setUpdateTime(now).setBeginChatTime(now).setEndChatTime(completedTime)
					.setLastChatTime(now).setSellerFirstReplyTime(now)
					.setSettlementTime(settlementTime).setCompletedTime(completedTime);
				this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						fsOrderDao.update(orderForUpdate);
						fsChatSessionDao.updateExpiryDateByChatSessionNo(chatSessionNo, completedTime);
						return true;
					}
				});
				//2017/05/25 add by fidel bug fix see PayAfter24HoursManagerImpl#doSetOrderStatusToCompleted L91
				JSONObject beanstalkMsg = new JSONObject();
				beanstalkMsg.put("orderId", order.getId());
				BeanstalkClient.put(QueueNameConstant.masterNoReply24HoursAutoRefund, null, 3600 * 24 + 1, null, beanstalkMsg);
				
				JSONObject cacheValue2 = new JSONObject();
				cacheValue2.put("orderId", orderId);
				cacheValue2.put("_cacheTime",  now);
				RedisClient.set(key, cacheValue2,  RedisClient.oneDaySec * 7   );
				//老师第一次回复 push 微信通知到buyUsr
				Map<Long,FsUsr> idUsrMap =  fsUsrDao.findByUsrIdsAndConvert( Arrays.asList( order.getBuyUsrId() , order.getSellerUsrId() ));
				List<FsMasterInfo> masterInfoList =  fsMasterInfoDao.findByUsrIds2(Arrays.asList(order.getSellerUsrId()), "approved", null);
				String masterName = null;
				if(CollectionUtils.isNotEmpty(masterInfoList)){
					masterName = UsrAidUtil.getMasterNickName(masterInfoList.get(0), null, null);
				}
				if(StringUtils.isEmpty(masterName)){
					masterName=  UsrAidUtil.getNickName2( idUsrMap.get( order.getSellerUsrId() ) ,"");	
				}
				String replyContent = null;
				if( StringUtils.equals("img", chatReply.getMsgType()) ){
					replyContent = "[图片]";
				}else{
					replyContent =  chatReply.getContent().length()>20 ? chatReply.getContent().substring(0, 20)+"..."  : chatReply.getContent();
				}
				this.wxNoticeManagerImpl.masterFirstReplyUserWxMsg(orderId, order.getBuyUsrId(), 
						 	idUsrMap.get(order.getBuyUsrId()).getWxOpenId(), chatSessionNo, order.getGoodsName(), masterName, replyContent, chatReply.getId());
			 }
		}catch(Exception e){
			logger.error("orderId:"+orderId+",chatSessionNo:"+chatSessionNo+",sentUsrId:"+chatReply.getSentUsrId() +",chatRecordId:"+chatReply.getId() , e);
		}
		return isMasterFirstReply;
	}
	
	
	
	private Long getReceUsrId(List<FsChatSession> chatSessionList,long sendUsrId){
		for(FsChatSession bean : chatSessionList ){
			if(!bean.getUsrId(). equals( sendUsrId )){
				return bean.getUsrId();
			}
		}
		return null;
	}
	
	
	private Long getServiceProviderUsrId(List<FsChatSession> chatSessionList){
		for(FsChatSession bean : chatSessionList){
			if("Y".equals(bean.getIsServiceProvider())){
				return bean.getUsrId();
			}
		}
		//防止空指针错误 
		return -9999l;
	}
	
	private JSONObject chatParameterCheck(String clientUniqueNo, String chatSessionNo, Long orderId, String msgType, String content, CommonsMultipartFile img, long sendUsrId, Date now ){
		if(StringUtils.isEmpty(clientUniqueNo) ){
			logger.warn("clientUniqueNo:"+clientUniqueNo +"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  clientUniqueNo为空");
			return JsonUtils.commonJsonReturn("0001", "网络不稳定，请刷新页面");//clientUniqueNo为空
		}
		if(StringUtils.isEmpty(chatSessionNo) || orderId ==null ){
			logger.warn("clientUniqueNo:"+clientUniqueNo +"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  chatSessionNo|orderId 为空");
			return JsonUtils.commonJsonReturn("0001", "网络不稳定，请刷新页面");//chatSessionNo|orderId 为空
		}
		if( ! StringUtils.equals(msgType, "text")  &&  ! StringUtils.equals(msgType, "img")   ){
			logger.warn("clientUniqueNo:"+clientUniqueNo +"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  消息类型错误");
			return JsonUtils.commonJsonReturn("0001", "消息类型错误");
		}
		if(StringUtils.equals(msgType, "text") && StringUtils.isEmpty(content) ){
			logger.warn("clientUniqueNo:"+clientUniqueNo +"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  文本消息不能为空");
			return JsonUtils.commonJsonReturn("0001", "文本消息不能为空");
		}
		if(StringUtils.equals(msgType, "img") &&  (img==null || img.getSize()<1)  ){
			logger.warn("clientUniqueNo:"+clientUniqueNo+"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  图片(文件)不能为空");
			return JsonUtils.commonJsonReturn("0001", "图片(文件)不能为空");
		}
		try{
			FsOrder order = fsOrderDao.findById(orderId);
			List<FsChatSession> chatSessionList =  fsChatSessionDao.findByChatSessionNo(chatSessionNo);
			if(order == null || !chatSessionNo.equals(order.getChatSessionNo())){
				logger.warn("clientUniqueNo:"+clientUniqueNo +"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",   orderId|chatSessionNo 未能匹配");
				return JsonUtils.commonJsonReturn("0001", "网络不稳定，请刷新页面"); //orderId|chatSessionNo 未能匹配
			}
			if(CollectionUtils.isEmpty(chatSessionList) ){
				logger.warn("clientUniqueNo:"+clientUniqueNo+"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  为能找到对应对话人");
				return JsonUtils.commonJsonReturn("0001", "当前对话异常，请刷新页面");				
			}
			if(chatSessionList.size()!=2){
				logger.warn("clientUniqueNo:"+clientUniqueNo +		"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  当前仅支持两人对话");
				return JsonUtils.commonJsonReturn("0001", "当前仅支持两人对话");				
			}
			if(! usrIsJoinChatSession(chatSessionList , sendUsrId)){
				logger.warn("clientUniqueNo:"+clientUniqueNo + "chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+",  sendUsrId 不再对话列表中");
				return JsonUtils.commonJsonReturn("0001", "您不能在当前对话中发言");	
			}
			if(order.getEndChatTime().before(new Date())  ){
				logger.warn("clientUniqueNo:"+clientUniqueNo + "chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId+", 会话已经过期");
				return JsonUtils.commonJsonReturn("0001", "对话已结束");	
			}
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "chatSessionList", chatSessionList);
			return result;
		}catch(Exception e){
			logger.error("clientUniqueNo:"+clientUniqueNo +		"chatSessionNo:"+chatSessionNo+",orderId:"+orderId+",msgType:"+msgType+",content:"+content+",sendUsrId:"+sendUsrId,e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	private boolean usrIsJoinChatSession(List<FsChatSession> chatSessionList , long sendUsrId){
		for( FsChatSession bean : chatSessionList){
			if(bean.getUsrId().equals(  sendUsrId)) {
				return true;
			}
		}
		return false;
	}
}
