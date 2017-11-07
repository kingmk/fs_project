package com.lmy.core.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.Bazi;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.component.LunarSolarConverter;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderEvaluateDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsChatRecord;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.dto.FsChatRecordDto;
import com.lmy.core.model.enums.OrderStatus;
import com.lmy.core.utils.FsExecutorUtil;
/**
 * @author fidel
 * @since 2017/04/17
 */
@Service
public class OrderChatQueryServiceImpl {
	private static final Logger logger = Logger.getLogger(OrderChatQueryServiceImpl.class);
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsMasterInfoDao  fsMasterInfoDao;
	@Autowired 
	private FsChatRecordDao fsChatRecordDao;
	@Autowired
	private FsOrderEvaluateDao fsOrderEvaluateDao;
	
	

    private JSONArray orderExTraInfo(String orderExtraInfo) {
    	JSONArray orderExtraInfoList = null;
    	try{
        	if(StringUtils.isEmpty(orderExtraInfo)){
        		return null;
        	}
        	orderExtraInfoList	 = JSONArray.parseArray(orderExtraInfo);
    		for(int i =0;i<orderExtraInfoList.size();i++){
    			JSONObject info = orderExtraInfoList.getJSONObject(i);
    			//计算八字
    			if(StringUtils.isNotEmpty(	info.getString("birthDate")  )){
    				Date _birthDate = null;
    				String birthTime = info.getString("birthTime");
    				Boolean birthTimeUnknown = (!StringUtils.isNotBlank(birthTime) || birthTime.contains("不清楚"));
    				Boolean hourUnknown = false;
    				String birth = "";
    				if (!birthTimeUnknown) {
    					birth = info.getString("birthDate")   + " "+ birthTime.split("~")[0].replaceAll(" ", "");
    				} else if (birthTime.equals("不清楚")){
    					hourUnknown = true;
    					birth = info.getString("birthDate")+ " 00:00";
    				} else if (birthTime.endsWith(":不清楚")) {
    					birth = info.getString("birthDate")+ " "+birthTime.split(":")[0].trim()+":00";
    				}

					_birthDate = CommonUtils.stringToDate(birth, CommonUtils.dateFormat4);
    				Bazi bazi = new Bazi(_birthDate);
    				String strBazi = bazi.getbazi();
    				
    				strBazi = strBazi.substring(0, 6)+(hourUnknown?"--":strBazi.substring(6,8));
    				
    				info.put("bazi",  strBazi);  //八字
    				Calendar cal = Calendar.getInstance();
    				cal.setTime(_birthDate);
    				//{1986,1,12,0}
    				int obj []= LunarSolarConverter.solarToLunar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
    				cal.set(obj[0], obj[1]-1, obj[2]);
    		        info.put("nlBirthDateDesc",  CommonUtils.dateToString(cal.getTime(), "yyyy年MM月dd日", "") );  //农历生日
    			}
    		}		
    		return orderExtraInfoList;
        
    	}catch(Exception e){
    		logger.warn("计算八字 农历 错误 orderExtraInfo:"+orderExtraInfo,e);
    		return orderExtraInfoList;
    	}
    }
    
    
    public JSONObject queryForChatIndex(long orderId , long loginUsrId , String chatSessionNo,Boolean afterCommUsrSupplyInfo){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null || !StringUtils.equals(order.getChatSessionNo(), chatSessionNo )){
				logger.warn("数据错误	orderId:"+orderId+",loginUsrId:"+loginUsrId+",chatSessionNo:"+chatSessionNo);
				return JsonUtils.commonJsonReturn("0001","数据错误");
			}
			if(!order.getSellerUsrId().equals(loginUsrId) && !order.getBuyUsrId().equals(loginUsrId) ){
				logger.warn("不能查看他人订单数据	orderId:"+orderId+",loginUsrId:"+loginUsrId+",chatSessionNo:"+chatSessionNo+",orderSellerUsrId:"+order.getSellerUsrId()+",orderBuyUsrId:"+order.getBuyUsrId());
				return JsonUtils.commonJsonReturn("0010","不能查看他人订单数据");
			}
			//获取 master 信息
			FsMasterInfo masterInfo = queryForChatIndex_getMaster(order.getSellerUsrId());
			//当前用户是否为master
			boolean isMaster = masterInfo.getUsrId() .equals(loginUsrId) ;
			if(!isMaster && OrderStatus.pay_succ.getStrValue().equals( order.getStatus() )
					&&   OrderAidUtil.getNeedSupplyOrderInfoZxCateIds().contains( order.getZxCateId()  )  && (StringUtils.isEmpty(order.getOrderExtraInfo())  && !Boolean.TRUE.equals(afterCommUsrSupplyInfo)  ))   {
				logger.warn("咨询人待补充数据  orderId:"+orderId+",buyUsrId:"+loginUsrId+",chatSessionNo:"+chatSessionNo+",zxCateId:"+order.getZxCateId()+",orderExtraInfo:"+order.getOrderExtraInfo()+",afterCommUsrSupplyInfo:"+afterCommUsrSupplyInfo);
				return JsonUtils.commonJsonReturn("1000","咨询人待补充数据");
			}
			return queryForChatIndex(masterInfo, order, isMaster);
		}catch(Exception e){
			logger.error("orderId:"+orderId+",loginUsrId:"+loginUsrId+",chatSessionNo:"+chatSessionNo, e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}

	private JSONObject queryForChatIndex(FsMasterInfo masterInfo, FsOrder order, Boolean isMaster) {
		Date now = new Date();
		boolean isServiceEnd = now.after( order.getEndChatTime() ) || (order.getRefundApplyTime()!=null ||  StringUtils.equals(order.getIsAutoRefund(), "Y"));
		boolean isWaitMasterService = ( OrderStatus.pay_succ.getStrValue().equals(order.getStatus() )  && order.getSellerFirstReplyTime()==null );
		//本次服务时间剩余时间(秒)**
		long chatServiceSurplusSec = (isServiceEnd || !OrderStatus.pay_succ.getStrValue().equals(order.getStatus() ) )? 0l : CommonUtils.calculateDiffSeconds(now, order.getEndChatTime());
		boolean hadEvaluate = order.getEvaluateTime() !=null ;
		JSONObject result = JsonUtils.commonJsonReturn();
		JsonUtils.setBody(result, "isMaster", isMaster);  //当前用户是否为master
		JsonUtils.setBody(result, "isServiceEnd", isServiceEnd ? "Y":"N"); //聊天服务是否已过截止时间
		JsonUtils.setBody(result, "isWaitMasterService",  isWaitMasterService ? 'Y' :'N' ); //是否等待老师服务
		JsonUtils.setBody(result, "goodsName", order.getGoodsName());
		JsonUtils.setBody(result, "payRmbAmt",order.getPayRmbAmt());  //单位分
		JsonUtils.setBody(result, "payRmbAmtDesc",CommonUtils.numberFormat(order.getPayRmbAmt()/100d , "###,##0.00", "0.0"));
		JsonUtils.setBody(result, "orderId", order.getId());
		JsonUtils.setBody(result, "orderExtraInfo", orderExTraInfo(order.getOrderExtraInfo())); //用户信息
		JsonUtils.setBody(result, "masterInfoId", masterInfo.getId());
		JsonUtils.setBody(result, "orderStatus", order.getStatus());
		JsonUtils.setBody(result, "chatServiceSurplusSec", chatServiceSurplusSec);  //聊天服务剩余秒数
		JsonUtils.setBody(result, "payConfirmTime", CommonUtils.dateToString(order.getPayConfirmTime(), CommonUtils.dateFormat4, "")   );  //支付确认时间
		JsonUtils.setBody(result, "completedTime", CommonUtils.dateToString(order.getCompletedTime(), CommonUtils.dateFormat4, "")  );  // 订单服务完成时间
		JsonUtils.setBody(result, "refundApplyTime", CommonUtils.dateToString(order.getRefundApplyTime(), CommonUtils.dateFormat4, "")   );  //退款申请时间
		JsonUtils.setBody(result, "refundConfirmTime", CommonUtils.dateToString(order.getRefundConfirmTime(), CommonUtils.dateFormat4, "")   );  //退款确认时间
		JsonUtils.setBody(result, "hadRefund", (order.getRefundApplyTime()!=null  ) ? "Y":"N");  //是否发起过投诉
		JsonUtils.setBody(result, "hadEvaluate", hadEvaluate ? "Y":"N");  //是否已评价
		
		if (isMaster) {
			FsUsr buyUsr = this.fsUsrDao.findById(order.getBuyUsrId());
			JSONObject cacheBuyUsrWxInfo = UsrAidUtil.getCacheWeiXinInfo(buyUsr);
			JsonUtils.setBody(result, "contactUsrId", buyUsr.getId());
			JsonUtils.setBody(result, "contactUsrName", UsrAidUtil.getNickName2(buyUsr, cacheBuyUsrWxInfo, "") );  
			JsonUtils.setBody(result, "contactUsrHeadImgUrl", UsrAidUtil.getUsrHeadImgUrl2(buyUsr, cacheBuyUsrWxInfo, "")); 
			long contactCnt = fsOrderDao.countContactOrders(order.getSellerUsrId(), order.getBuyUsrId());
			JsonUtils.setBody(result, "contactCnt", contactCnt);
		} else {
			JsonUtils.setBody(result, "contactUsrId", masterInfo.getUsrId());
			JsonUtils.setBody(result, "contactUsrName", UsrAidUtil.getMasterNickName(masterInfo, null, ""));  //与 谁聊天
			JsonUtils.setBody(result, "contactUsrHeadImgUrl", UsrAidUtil.getMasterHeadImg(masterInfo,null,""));  //与 谁聊天 人的头像
			boolean isCanRefund =OrderAidUtil.isOrderCanApplyRefund(order, now);
			//退款剩余秒数
			long refundSurplusSec = isCanRefund?CommonUtils.calculateDiffSeconds(DateUtils.addDays(order.getSellerFirstReplyTime(), 7),now) : 0l;
			//是否可以点评
			boolean isCanEvaluate = ( order.getEvaluateTime() == null && OrderAidUtil.getCanEvaluateStatus().contains(order.getStatus())); 
			JsonUtils.setBody(result, "isCanEvaluate", isCanEvaluate ? 'Y':'N'); //是否可以点评
			JsonUtils.setBody(result, "isCanRefund", isCanRefund ? "Y":"N"); //是否可发起退款
			JsonUtils.setBody(result, "refundSurplusSec", refundSurplusSec); //发起退款剩余秒数
		}

		return result;
	}
	
    private Long getCurrMaxReplyId(List<FsChatRecordDto> chatList , long loginUsrId){
    	Long maxId = null;
    	for(FsChatRecordDto bean : chatList){
    		if(bean.getReceUsrId().equals(loginUsrId) ){
    			if(maxId == null ||  bean.getId()>maxId ){
    				maxId = bean.getId();
    			}
    		}
    	}
    	return maxId;
    }

	public JSONObject queryAjax(Long gtChatId ,Long ltChatId, List<Long> excludeChatIds, long loginUsrId, long orderId, String chatSessionNo, int currentPage, int perPageNum){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null  || !StringUtils.equals(order.getChatSessionNo(), chatSessionNo )){
				return JsonUtils.commonJsonReturn("0001","数据错误");
			}
			JSONObject result = JsonUtils.commonJsonReturn();
			List<FsChatRecordDto> chatList = findChatRecord( gtChatId ,ltChatId, excludeChatIds ,orderId, chatSessionNo, currentPage, perPageNum) ;
			if(CollectionUtils.isNotEmpty(chatList)){
				Long curMaxReplyId = getCurrMaxReplyId(chatList, loginUsrId);
				Collections.reverse(chatList);
				addHeadUrl(chatList, order );
				asynSetReadTime(chatList, loginUsrId);
				JsonUtils.setBody(result, "curMaxReplyId",  curMaxReplyId!=null ? curMaxReplyId :"");
				JsonUtils.setBody(result, "curMaxId", chatList.get(  chatList.size()-1    ).getId());
				JsonUtils.setBody(result, "curMinId", chatList.get( 0).getId());
				JsonUtils.setBody(result, "chatList", chatList);
				JsonUtils.setBody(result, "size", CollectionUtils.isNotEmpty(chatList) ? chatList.size(): 0);
			}else{
				JsonUtils.setBody(result, "curMaxId", "");
				JsonUtils.setBody(result, "curMinId", "");
				JsonUtils.setBody(result, "chatList", null);
				JsonUtils.setBody(result, "size", 0);
			}
			return  result;
		}catch(Exception e){
			logger.error("orderId:"+orderId+",chatSessionNo:"+chatSessionNo+",currentPage:"+currentPage+",perPageNum:"+perPageNum, e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	/** 设置头像 **/
	private void addHeadUrl(List<FsChatRecordDto> chatList ,FsOrder order ){
		if(CollectionUtils.isEmpty(chatList)){
			return ;
		}
		List<Long> usrIds = new ArrayList<Long>();
		for(FsChatRecordDto bean : chatList){
			if(! usrIds.contains( bean.getSentUsrId() ) ){
				usrIds.add(bean.getSentUsrId());	
			}
			if(! usrIds.contains( bean.getReceUsrId() ) ){
				usrIds.add(bean.getReceUsrId());	
			}
		}
		FsMasterInfo masterInfo = queryForChatIndex_getMaster(order.getSellerUsrId());
		Map<Long,JSONObject> usrIdCacheWxInfoMap = new HashMap<Long,JSONObject>();
		Map<Long ,FsUsr> usrIdHeadImgUrlMap = this.fsUsrDao.findShortInfo1ByUsrIds(usrIds);
		for(FsChatRecordDto bean :  chatList){
			//master
			if(bean.getSentUsrId().equals( order.getSellerUsrId() )){
				bean.setSendtUsrHeadImgUrl( UsrAidUtil.getMasterHeadImg(masterInfo, usrIdHeadImgUrlMap.get( bean.getSentUsrId()), "") );
			}else{
				bean.setSendtUsrHeadImgUrl(  _getUsrImgUrl(  usrIdHeadImgUrlMap.get( bean.getSentUsrId()),usrIdCacheWxInfoMap  ));
			}
			if( "text".equals(bean.getMsgType()) ){
				bean.setContent( StringEscapeUtils.escapeHtml4(bean.getContent())  )	;
			}
		}
	}
	private String _getUsrImgUrl(FsUsr usr,Map<Long,JSONObject> usrIdCacheWxInfoMap){
		JSONObject  cacheWxInfo =usrIdCacheWxInfoMap.get(usr.getId());
		if(cacheWxInfo == null && !usrIdCacheWxInfoMap.containsKey(usr.getId()) ){
			cacheWxInfo = UsrAidUtil.getCacheWeiXinInfo(usr);
			usrIdCacheWxInfoMap.put(usr.getId(), cacheWxInfo);
		}
		return UsrAidUtil.getUsrHeadImgUrl2(usr, cacheWxInfo, null);
	}
	/**  异步设置 read 时间 read 状态**/
	private void asynSetReadTime(final List<FsChatRecordDto> chatList ,final long loginUsrId  ){
		if(CollectionUtils.isNotEmpty(chatList)){
			Callable<Boolean> t = new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					Date now = new Date();
					asynSetReadTimeDo(chatList, loginUsrId, now);
					return null;
				}
			};
			FsExecutorUtil.getSchExecutor().schedule(t, 0, TimeUnit.SECONDS);	
		}
	}
	private void asynSetReadTimeDo(List<FsChatRecordDto> chatList ,long loginUsrId ,Date now){
		for(FsChatRecordDto bean : chatList){
			try{
				if((bean.getReadTime()==null || "N".equals( bean.getIsRead()) )&& (bean.getReceUsrId().equals(loginUsrId) || bean.getReceUsrId() == loginUsrId) ){
					bean.setReadTime(now).setIsRead("Y");
					final FsChatRecord beanForUpdate = new FsChatRecord();
					beanForUpdate.setId(bean.getId());
					beanForUpdate.setIsRead("Y").setReadTime(now).setUpdateTime(now);
					this.fsChatRecordDao.update(beanForUpdate);
				}
			}catch(Exception e){
				logger.error("FsChatRecordDto:"+bean+", loginUsrId" ,e);
			}
		}
	}
	/** if not found return null **/
	private List<com.lmy.core.model.dto.FsChatRecordDto> findChatRecord(Long gtChatId ,Long ltChatId ,List<Long>  excludeChatIds,Long orderId , String chatSessionNo,int currentPage,int perPageNum){
		return fsChatRecordDao.findChatRecord(gtChatId, ltChatId,excludeChatIds, orderId, chatSessionNo, currentPage, perPageNum, "effect");
	}
	private FsMasterInfo queryForChatIndex_getMaster(long masterUsrId){
		List<FsMasterInfo> list = fsMasterInfoDao.findBtCondition1(null, masterUsrId, null,  Arrays.asList( "approved"), null);
		return list.get(0);
	}
	/**
	 * @param chatSessionNo if null 统计所有未读数;否则 统计某一次回话未读数
	 * @param isMaster 当前用户是否为master 角色登录 Y|N
	 * @return
	 */
	public JSONObject queryUnReadNum(String chatSessionNo , String isMaster,  long receUsrId){
		try{
			Long unReadNum =  this.fsChatRecordDao.statUnReadNum1(chatSessionNo, isMaster,receUsrId);	
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "unReadNum", unReadNum);
			return result;
		}catch(Exception e){
			logger.error("chatSessionNo:"+chatSessionNo+", receUsrId:"+receUsrId+", isMaster:"+isMaster,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
}
