package com.lmy.core.manage.impl;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.dao.FsWeixinMsgPushRecordDao;
import com.lmy.core.model.FsChatRecord;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsWeixinMsgPushRecord;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;
import com.lmy.core.utils.FsExecutorUtil;
/**
 * 微信消息都是异步推送<br>
 * 这里的 public 方式 全做成 异步 推送
 * @author fidel
 * @since 2017/05/7
 */
@Service
public class WxNoticeManagerImpl {
	private static final Logger logger = LoggerFactory.getLogger(WxNoticeManagerImpl.class);
	private static final String fsServiceBaseHost = ResourceUtils.getValue("lmy-core","fs.service.basehost");
	@Autowired
	private FsWeixinMsgPushRecordDao fsWeixinMsgPushRecordDao;
	/**
	 * @param order
	 * @param sellerUsrOpenId
	 * @param buyUsrName  取用户 微信昵称 如果取不到 则显示 匿名
	 */
	public void masterNewOrderWxMsg(final FsOrder order , final String sellerUsrOpenId, String buyUsrName){
		String template_id = "cfFWR-Z8WHTFTQSa4j4nKKNAbdBTDglLbhnfhkda918"; 
		String clickUrl = fsServiceBaseHost+"/order/chat_index?chatSessionNo="+order.getChatSessionNo()+"&orderId="+order.getId();
		String title = "您有新订单";
		String remarkValue = buyUsrName+"于"+CommonUtils.dateToString(new Date(), "HH:mm", "")+"咨询问题";
		JSONObject pushToWxdata =  buildWxJsonData2(title, buyUsrName, order.getGoodsName(), remarkValue);
		logger.info("========to send new order wx msg to master========");
		asynHand(order.getId(), order.getSellerUsrId(), null, sellerUsrOpenId, template_id, title,  clickUrl, pushToWxdata);
	}
	
	public void masterWaitFirstReplyWxMsg(final FsOrder order , final String sellerUsrOpenId, String buyUsrName) {
		String template_id = "cfFWR-Z8WHTFTQSa4j4nKKNAbdBTDglLbhnfhkda918"; 
		String clickUrl = fsServiceBaseHost+"/order/chat_index?chatSessionNo="+order.getChatSessionNo()+"&orderId="+order.getId();
		String title = "请尽快接单";
		String remarkValue = buyUsrName+"于"+CommonUtils.dateToString(order.getPayConfirmTime(), "HH:mm", "")+"咨询问题，您还未接单，请尽快回复客户";
		JSONObject pushToWxdata =  buildWxJsonData2(title, buyUsrName, order.getGoodsName(), remarkValue);
		asynHand(order.getId(), order.getSellerUsrId(), null, sellerUsrOpenId, template_id, title,  clickUrl, pushToWxdata);
	}

	/**
	 * @param buyUsrName  取用户 微信昵称 如果取不到 则显示 匿名
	 */
	public void userReplyMasterWxMsg(final FsOrder order,  final FsChatRecord unReadChatRecord, String sellerUsrOpenId, String buyUsrName){
		String template_id = "Rn7HJBcL2yUh4xGIb311Vwa8lVDSPzkminEXYI6MALU"; 
		String clickUrl = fsServiceBaseHost+"/order/chat_index?chatSessionNo="+order.getChatSessionNo()+"&orderId="+order.getId();
		String title = "您的订单有新消息";
		String keyword2Value = null;
		if("img".equals(unReadChatRecord.getMsgType())){
			keyword2Value="[图片]";
		}else{
			keyword2Value = unReadChatRecord.getContent().length()>20 ? unReadChatRecord.getContent().substring(0, 20)+"..."  : unReadChatRecord.getContent();
		}
		if(StringUtils.isEmpty(buyUsrName)){
			buyUsrName = "匿名";
		}
		String remarkValue = buyUsrName+"于"+CommonUtils.dateToString(unReadChatRecord.getCreateTime(), "HH:mm", "")+"发新消息";
		JSONObject pushToWxdata =  buildWxJsonData2(title, order.getGoodsName(), keyword2Value, remarkValue);
		asynHand(order.getId(), order.getSellerUsrId(), unReadChatRecord.getId(), sellerUsrOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	
	/**
	 * 超过10分钟未读提醒老师赶紧回复
	 * @param order
	 * @param unReadChatRecord
	 * @param sellerUsrOpenId
	 * @param buyUsrName
	 */
	public void userReplyMasterUnreadWxMsg(final FsOrder order,  final FsChatRecord unReadChatRecord, String sellerUsrOpenId, String buyUsrName){
		String template_id = "Rn7HJBcL2yUh4xGIb311Vwa8lVDSPzkminEXYI6MALU"; 
		String clickUrl = fsServiceBaseHost+"/order/chat_index?chatSessionNo="+order.getChatSessionNo()+"&orderId="+order.getId();
		String title = "尽快回复用户";
		String keyword2Value = null;
		if("img".equals(unReadChatRecord.getMsgType())){
			keyword2Value="[图片]";
		}else{
			keyword2Value = unReadChatRecord.getContent().length()>20 ? unReadChatRecord.getContent().substring(0, 20)+"..."  : unReadChatRecord.getContent();
		}
		if(StringUtils.isEmpty(buyUsrName)){
			buyUsrName = "匿名";
		}
		String remarkValue = buyUsrName+"于"+CommonUtils.dateToString(unReadChatRecord.getCreateTime(), "HH:mm", "")+"发的消息，您还没有回复哦，请尽快回复用户，以免被投诉";
		JSONObject pushToWxdata =  buildWxJsonData2(title, order.getGoodsName(), keyword2Value, remarkValue);
		asynHand(order.getId(), order.getSellerUsrId(), unReadChatRecord.getId(), sellerUsrOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	/** 订单被评价 给master  推送消息   **/
	public void orderEvaluateMasterWxMsg(String masterOpenId, final long masterUsrId, final long orderId,String chatSessionNo,   final String goodsName , String buyUsrName , long respSpeedScore , long majorLevelScore , long serviceAttitudeScore , double totalScore){
		String template_id = "1PmvHfjSe9hYCd7HOwHF5VHE2Nowhf0O5kbtaZd7blk"; 
		String clickUrl =   fsServiceBaseHost+"/order/chat_index?chatSessionNo="+chatSessionNo+"&orderId="+orderId;
		String title = "您的订单已被评价";
		String serviceType =goodsName;
		String serviceStatus = "订单被评价";
		String time = CommonUtils.dateToString(new Date(), CommonUtils.dateFormat4, "") ;
		String remark = buyUsrName+"已作出评价：响应速度得分 " +respSpeedScore+",专业度得分 "+majorLevelScore+",服务态度得分 " +serviceAttitudeScore+",总评分 "+CommonUtils.numberFormat(totalScore, "###,##0.00", "") ;
		JSONObject pushToWxdata =  buildWxJsonData1(title,serviceType , serviceStatus, time, remark);
		asynHand(orderId, masterUsrId, null, masterOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	/** 订单被退款 给master  推送消息 **/
	public void orderRefundMsgMasterWxMsg(long orderId, String chatSessionNo , long sellerUsrId, String sellerUsrOpenId,  String goodsName ,  long refundAmt ,String buyUsrName , String refundReason ){
		String template_id = "1PmvHfjSe9hYCd7HOwHF5VHE2Nowhf0O5kbtaZd7blk"; 
		String clickUrl =   fsServiceBaseHost+"/order/chat_index?chatSessionNo="+chatSessionNo+"&orderId="+orderId;
		String title = "您的订单被退款";
		String serviceType = goodsName;
		String serviceStatus = "退款" + CommonUtils.numberFormat(refundAmt/100d, "###,##0.00", "") ;
		String time = CommonUtils.dateToString(new Date(), CommonUtils.dateFormat4, "") ;
		String remark = buyUsrName+"发起退款投诉。雷门易同意退款，退款原因："+refundReason ;
		JSONObject pushToWxdata =  buildWxJsonData1(title,serviceType , serviceStatus, time, remark);
		asynHand(orderId, sellerUsrId, null, sellerUsrOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	/** 老师第一次回复消息 给用户发送消息  **/
	public void masterFirstReplyUserWxMsg(long orderId,  long buyUsrId, String buyUsrOpenId,String chatSessionNo,String goodsName,String masterName, String replyContent, long replayId){
		String template_id = "Rn7HJBcL2yUh4xGIb311Vwa8lVDSPzkminEXYI6MALU"; 
		String clickUrl =  fsServiceBaseHost+"/order/chat_index?chatSessionNo="+chatSessionNo+"&orderId="+orderId;
		String title = "老师已接单";
		String remarkValue = masterName + "于" +CommonUtils.dateToString(new Date(), "HH:mm", "") + "接了您的订单，点击查看详情" ;
		JSONObject pushToWxdata = buildWxJsonData2(title, goodsName, replyContent, remarkValue);
		asynHand(orderId, buyUsrId, null, buyUsrOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	/** 老师回复消息立即 给 buyUsr 用户发送消息  **/
	public void masterReplyUserWxMsg(long orderId,String chatSessionNo, String goodsName, String replyContent, long replyId,String masterName,long buyUsrId,String buyUsrOpenId){
		String template_id = "Rn7HJBcL2yUh4xGIb311Vwa8lVDSPzkminEXYI6MALU"; 
		String clickUrl = fsServiceBaseHost+"/order/chat_index?chatSessionNo="+chatSessionNo+"&orderId="+orderId;
		String title = "老师有新回复";
		String remarkValue = masterName + "于" +CommonUtils.dateToString(new Date(), "HH:mm", "") + "发了新消息，点击查看详情" ;
		JSONObject pushToWxdata = buildWxJsonData2(title, goodsName, replyContent, remarkValue);
		asynHand(orderId, buyUsrId, null, buyUsrOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	
	/** 订单被退款 给 buyUsr  推送消息 **/
	public void orderRefundMsgUserWxMsg(long orderId ,String goodsName, long buyUsrId,String buyUsrOpenId,String chatSessionNo,   boolean isRefundSucc , String refundAuditWord){
		String template_id = "1PmvHfjSe9hYCd7HOwHF5VHE2Nowhf0O5kbtaZd7blk"; 
		String clickUrl =    fsServiceBaseHost+"/order/chat_index?chatSessionNo="+chatSessionNo+"&orderId="+orderId;
		String title = "您申请的退款已处理";
		String serviceType =goodsName;
		String serviceStatus =  isRefundSucc  ? "退款成功" :"退款失败";
		String time = CommonUtils.dateToString(new Date(), CommonUtils.dateFormat4, "") ;
		String remark = "退款处理意见：" + refundAuditWord ;
		if(isRefundSucc){
			remark = remark + "（退款金额到账时间视微信退款时间规定，如使用银行卡支付，请确保该银行卡还与您的微信账号绑定）";
		}
		JSONObject pushToWxdata =  buildWxJsonData1(title,serviceType , serviceStatus, time, remark);
		asynHand(orderId, buyUsrId, null, buyUsrOpenId, template_id, title, clickUrl, pushToWxdata);
	}
	
	// 退款、评价消息
	private JSONObject buildWxJsonData1(String serviceInfo, String serviceType,String serviceStatus,String time,String remark){
		JSONObject data = new JSONObject();
		JSONObject serviceInfoJson = new JSONObject();
		serviceInfoJson.put("value", serviceInfo);
		JSONObject serviceTypeJson = new JSONObject();
		serviceTypeJson.put("value", serviceType);
		JSONObject serviceStatuseJson = new JSONObject();
		serviceStatuseJson.put("value", serviceStatus);
		JSONObject timeStatuseJson = new JSONObject();
		timeStatuseJson.put("value", time);
		JSONObject remarkJson = new JSONObject();
		remarkJson.put("value", remark);
		//data.put("title", titleJson);
		data.put("serviceInfo", serviceInfoJson);
		data.put("serviceType", serviceTypeJson);
		data.put("serviceStatus", serviceStatuseJson);
		data.put("time", timeStatuseJson);
		data.put("remark", remarkJson);
		return data;
	}

	// 新订单、老师接单、老师回复、用户回复消息
	private JSONObject buildWxJsonData2(String firstValue , String keyword1Value , String keyword2Value ,String remarkValue){
		JSONObject data = new JSONObject();
		JSONObject first = new JSONObject();
		first.put("value", firstValue);
		first.put("color", "#ff0000");
		JSONObject keyword1 = new JSONObject();
		keyword1.put("value", keyword1Value);		
		JSONObject keyword2 = new JSONObject();
		keyword2.put("value", keyword2Value);		
		JSONObject remark = new JSONObject();
		remark.put("value", remarkValue);		
		data.put("first", first);
		data.put("keyword1", keyword1);
		data.put("keyword2", keyword2);
		data.put("remark", remark);
		return data;
	}
	
	private void asynHand(final Long orderId, final Long usrId , final Long  chatRecordId , 
			final String openId , final String  template_id , final String title , final String clickUrl ,final JSONObject pushToWxdata ){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try{
					Date now = new Date();
					JSONObject sentResult = WeiXinInterServiceImpl.sendTextMsg1(openId, template_id, clickUrl, pushToWxdata);
					String respMsgid = (String)JsonUtils.getBodyValue(sentResult, "msgid");
					String respCode = (String)JsonUtils.getBodyValue(sentResult, "errcode");
					String respMsg = (String)JsonUtils.getBodyValue(sentResult, "errmsg");
					pushMsgPersisDb(orderId, usrId, openId, template_id, title, pushToWxdata.toJSONString(), respMsgid, respCode, respMsg, chatRecordId, now);
				}catch(Exception  e){
					logger.error("orderId:{},usrId:{},chatRecordId{},openId:{},template_id:{},title:{},content:{},clickUrl:{},pushToWxdata:{}", 
							orderId ,usrId,chatRecordId,openId,template_id,title,pushToWxdata.toJSONString(),clickUrl,pushToWxdata);
					logger.error("系统错误", e);
				}				
			}
		};
		FsExecutorUtil.getExecutor().execute(r);
	}
	private void pushMsgPersisDb(Long orderId , Long usrId , String openId , String template_id , String title ,String  content ,  String respMsgid , String respCode , String respMsg ,Long chatRecordId,Date now ){
		FsWeixinMsgPushRecord beanForInsert = new FsWeixinMsgPushRecord();
		try{
			beanForInsert.setId(null);
			beanForInsert.setCreateTime(now);
			beanForInsert.setContent(content).setOpenId(openId).setOrderId(orderId).setRespCode(respCode).setRespMsg(respMsg).setRespMsgid(respMsgid).setTemplateId(template_id).setTitle(title).setUserId(usrId);
			if(chatRecordId !=null){
				beanForInsert.setRemark("{chatRecordId:"+chatRecordId+"}") ;	
			}
			this.fsWeixinMsgPushRecordDao.insert(beanForInsert)	;
		}catch(Exception e){
			logger.error(beanForInsert.toString(),e);
		}
	}
	
}
