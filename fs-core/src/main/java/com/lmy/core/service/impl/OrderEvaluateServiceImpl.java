package com.lmy.core.service.impl;
import java.util.ArrayList;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsOrderEvaluateDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsOrderEvaluate;
import com.lmy.core.model.FsUsr;
import com.lmy.core.utils.FsExecutorUtil;
/**
 * @author fidel
 * @since 2017/04/27
 */
@Service
public class OrderEvaluateServiceImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderEvaluateServiceImpl.class);
	@Autowired
	private FsMasterInfoDao  fsMasterInfoDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private FsOrderEvaluateDao fsOrderEvaluateDao;
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	@Autowired
	private WxNoticeManagerImpl wxNoticeManagerImpl;
	public JSONObject masterViewSingleEvaluateDetail(long sellerUsrId, long orderId){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null || !order.getSellerUsrId().equals(sellerUsrId)){
				logger.warn("sellerUsrId:"+sellerUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			List<FsMasterInfo> listMasterInfo = fsMasterInfoDao.findByUsrIds2( Arrays.asList( order.getSellerUsrId() ) , "approved",null);
			if(CollectionUtils.isEmpty(listMasterInfo)){
				logger.warn("sellerUsrId:"+sellerUsrId+",orderId:"+orderId+",数据错误 listMasterInfo.size="+CommonUtils.getListSize(listMasterInfo));
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			FsMasterInfo masterInfo = listMasterInfo.get(0);
			if(masterInfo == null){
				logger.warn("sellerUsrId:"+sellerUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			List<FsOrderEvaluate> list= this.fsOrderEvaluateDao.findByContion1(order.getId(), null, null);
			if(CollectionUtils.isEmpty(list)){
				logger.warn("sellerUsrId:"+sellerUsrId+",orderId:"+orderId+",数据错误 用户未评价");
				return JsonUtils.commonJsonReturn("0002", "数据错误");
			}
			FsOrderEvaluate evaluateBean = list.get(0);
			FsUsr evaluateUsr = fsUsrDao.findById( order.getBuyUsrId() );
			JSONObject result = JsonUtils.commonJsonReturn();
			if( evaluateUsr !=null){
				JSONObject cacheWeiXinUsrInfo = UsrAidUtil.getCacheWeiXinInfo(evaluateUsr);
				JsonUtils.setBody(result, "buyUsrId", evaluateUsr.getId());
				JsonUtils.setBody(result, "buyUsrName", UsrAidUtil.getNickName2(evaluateUsr, cacheWeiXinUsrInfo, "") );
				JsonUtils.setBody(result, "buyUsrHeadImgUrl",UsrAidUtil.getUsrHeadImgUrl2(evaluateUsr, cacheWeiXinUsrInfo, ""));					
			}else{
				if("Y".equals( evaluateBean.getIsAutoEvaluate() )){
					//
					JsonUtils.setBody(result, "buyUsrName", "系统评价");
					JsonUtils.setBody(result, "buyUsrHeadImgUrl", "");
				}
			}
			JsonUtils.setBody(result, "goodsName", order.getGoodsName());  	
			JsonUtils.setBody(result, "isAutoRefund", order.getIsAutoRefund());  	
			JsonUtils.setBody(result, "payRmbAmt", order.getPayRmbAmt());  	 //单位分
			JsonUtils.setBody(result, "payRmbAmtDesc", CommonUtils.numberFormat( order.getPayRmbAmt()/100d, "###,##0.00", "") );  	//单位元
			JsonUtils.setBody(result, "evaluateWord", evaluateBean.getEvaluateWord()  );  	
			JsonUtils.setBody(result, "majorLevel", evaluateBean.getMajorLevel() * 2 );  	
			JsonUtils.setBody(result, "respSpeed", evaluateBean.getRespSpeed() * 2 );  	
			JsonUtils.setBody(result, "serviceAttitude", evaluateBean.getServiceAttitude() * 2 );  	
			JsonUtils.setBody(result, "evaluateTime",  CommonUtils.dateToString(evaluateBean.getCreateTime(), CommonUtils.dateFormat4, "")  );  	
			JsonUtils.setBody(result, "isAutoEvaluate",  evaluateBean.getIsAutoEvaluate() );
			JsonUtils.setBody(result, "masterReplyWord",  evaluateBean.getMasterReplyWord() );
			JsonUtils.setBody(result, "orderId",  orderId  );  	
			JsonUtils.setBody(result, "chatSessionNo",  order.getChatSessionNo()  );  	
			return result;
		}catch(Exception e){
			logger.error("sellerUsrId:"+sellerUsrId+",orderId:"+orderId+",系统错误",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	public JSONObject commonUsrView(long buyUsrId, long orderId){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null || !order.getBuyUsrId().equals(buyUsrId)){
				logger.warn("loginUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			// this.fsMasterInfoDao.findByUsrIdsAndAuditStatusNotIng( Arrays.asList( order.getSellerUsrId() ) ) ;
			List<FsMasterInfo> list =  fsMasterInfoDao.findByUsrIds2( Arrays.asList( order.getSellerUsrId() ) , "approved",null);
			if(CollectionUtils.isEmpty(list)){
				logger.warn("loginUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			FsMasterInfo masterInfo  = list .get(0);
			List<FsOrderEvaluate> evaluateList =  this.fsOrderEvaluateDao.findByContion1(orderId, order.getSellerUsrId(), order.getBuyUsrId());
			boolean hadEvaluate =  CollectionUtils.isNotEmpty(evaluateList) || order.getEvaluateTime() !=null ;   ;
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "orderId", orderId);
			JsonUtils.setBody(result, "chatSessionNo", order.getChatSessionNo());
			JsonUtils.setBody(result, "masterName",  UsrAidUtil.getMasterNickName(masterInfo, null, ""));
			JsonUtils.setBody(result, "masterHeadImgUrl", masterInfo.getHeadImgUrl());
			JsonUtils.setBody(result, "hadEvaluate", hadEvaluate ? 'Y':'N');
			JsonUtils.setBody(result, "isAutoRefund", StringUtils.isNotEmpty(order.getIsAutoRefund()) ? order.getIsAutoRefund() :"N");  	
			if(hadEvaluate){
				FsOrderEvaluate evalOne = null;
				if(CollectionUtils.isEmpty(evaluateList)){
					logger.warn("订单已评价，但未能查询到评价记录 数据错误 ? new 出一个  orderId:{} , buyUsrId:{}", orderId , buyUsrId);
					evalOne = new FsOrderEvaluate();
					evalOne.setMajorLevel(0l).setRespSpeed(0l).setServiceAttitude(0l);
				}else{
					evalOne = evaluateList.get(0);
				}
				JsonUtils.setBody(result, "evaluateInfo", evalOne);
			}
			return result;
		}catch(Exception e){
			logger.error("loginUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误 ");
		}
	}
	/** 底层的评价方法  会给老师推送微信信息  
	 * @param respSpeed 想速度 星数
	 * @param majorLevel 专业水平 星数
	 * @param serviceAttitude 服务态度 星数
	 * **/
	
	public void evaluateOrder(final Long buyUsrId, final Long sellerUsrId,final long orderId, final Long goodsId,  final long respSpeed, final long majorLevel,final long serviceAttitude, final String commentWord,final String isAutoEvaluate, final int isAnonymous) {
		fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				Date now = new Date();
				FsOrderEvaluate beanForInsert = new FsOrderEvaluate();
				beanForInsert.setBuyUsrId(buyUsrId).setEvaluateWord(commentWord)
					.setOrderId(orderId).setGoodsId(goodsId).setStatus("effect").setIsAnonymous(isAnonymous)
					.setMajorLevel(majorLevel).setRespSpeed(respSpeed).setServiceAttitude(serviceAttitude)
					.setSellerUsrId(sellerUsrId).setIsAutoEvaluate(isAutoEvaluate).setCreateTime(now);
				fsOrderEvaluateDao.insert(beanForInsert);
				FsOrder orderForUpdate = new FsOrder();
				orderForUpdate.setId(orderId);
				orderForUpdate.setEvaluateTime(now).setUpdateTime(now);
				fsOrderDao.update(orderForUpdate);
				return true;
			}
		});
		//异步推送微信信息
		doAsynPushWxEvaluateMsg(orderId, respSpeed, majorLevel, serviceAttitude);
	}
	
	private void doAsynPushWxEvaluateMsg(final long orderId,final long respSpeed, final long majorLevel,final long serviceAttitude){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				_pushWxEvaluateMsg(orderId, respSpeed, majorLevel, serviceAttitude);
			}
		};
		FsExecutorUtil.getExecutor().execute(r);
	}
	
	private void _pushWxEvaluateMsg(final long orderId,final long respSpeed, final long majorLevel,final long serviceAttitude){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			Map<Long,FsUsr> idUsrMap = this.fsUsrDao.findByUsrIdsAndConvert( Arrays.asList( order.getSellerUsrId(),order.getBuyUsrId() ) );
			String buyUsrName =UsrAidUtil.getNickName2(idUsrMap.get( order.getBuyUsrId() ), "匿名");
			wxNoticeManagerImpl.orderEvaluateMasterWxMsg( idUsrMap.get(order.getSellerUsrId()).getWxOpenId() , order.getSellerUsrId(), 
					orderId, order.getChatSessionNo(),order.getGoodsName(), 
					buyUsrName, respSpeed * 2 , majorLevel * 2 , serviceAttitude * 2, (respSpeed +majorLevel +serviceAttitude ) *2/3 );			
		}catch(Exception e){
			logger.error("orderId:{},respSpeed:{},majorLevel:{},serviceAttitude:{}", orderId ,respSpeed  ,majorLevel ,serviceAttitude );
			logger.error("用户评价后 给老师推送消息发生错误", e );
		}
	}
	
	
	public JSONObject buyUsrCommentOrder(long buyUsrId , long orderId , long respSpeed , long majorLevel , long serviceAttitude , String commentWord, int isAnonymous){
		try{
			FsOrder order = this.fsOrderDao.findById(orderId);
			if(order == null || !order.getBuyUsrId().equals(buyUsrId)){
				logger.warn("loginUsrId:"+buyUsrId+",orderId:"+orderId+",数据错误");
				return JsonUtils.commonJsonReturn("0001", "数据错误");
			}
			List<FsOrderEvaluate> list= this.fsOrderEvaluateDao.findByContion1(order.getId(), order.getSellerUsrId(), order.getBuyUsrId()) ;
			if(CollectionUtils.isNotEmpty(list)){
				logger.warn("loginUsrId:"+buyUsrId+",orderId:"+orderId+",重复点评,点评记录数:"+CommonUtils.getListSize(list));
				return JsonUtils.commonJsonReturn("0002", "已点评");
			}
			evaluateOrder(buyUsrId, order.getSellerUsrId(), orderId,  order.getGoodsId(),respSpeed, majorLevel, serviceAttitude, commentWord,"N", isAnonymous);
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.warn("loginUsrId:"+buyUsrId+",orderId:"+orderId,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}

	
	public JSONObject masterReplyEvaluate(final long masterId, final long orderId, final String replyWord) {
		FsOrder order = this.fsOrderDao.findById(orderId);
		if(order == null || !order.getSellerUsrId().equals(masterId)){
			logger.warn("loginUsrId:"+masterId+",orderId:"+orderId+",数据错误");
			return JsonUtils.commonJsonReturn("0001", "当前用户无权回复该评价");
		}
		List<FsOrderEvaluate> list= this.fsOrderEvaluateDao.findByContion1(order.getId(), order.getSellerUsrId(), order.getBuyUsrId()) ;
		if(CollectionUtils.isEmpty(list)){
			logger.warn("loginUsrId:"+masterId+",orderId:"+orderId+", 用户还未评价");
			return JsonUtils.commonJsonReturn("0002", "用户还未评价");
		}
		
		FsOrderEvaluate evaluate = list.get(0);
		if (evaluate.getMasterReplyWord() != null) {
			logger.warn("loginUsrId:"+masterId+",orderId:"+orderId+", 已回复过本评价");
			return JsonUtils.commonJsonReturn("0002", "您已回复过，只能回复一次");
		}
		final long evaluateId = evaluate.getId();
		
		boolean rltDb = fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				Date now = new Date();
				FsOrderEvaluate beanForUpdate = new FsOrderEvaluate();
				beanForUpdate.setId(evaluateId);
				beanForUpdate.setMasterReplyWord(replyWord).setMasterReplyTime(now);
				fsOrderEvaluateDao.update(beanForUpdate);
				return true;
			}
		});
		if (!rltDb) {
			logger.warn("loginUsrId:"+masterId+",orderId:"+orderId+", 数据库保存异常");
			return JsonUtils.commonJsonReturn("0002", "系统异常");
		}
		return JsonUtils.commonJsonReturn();
	}
	
	public JSONObject masterEvaluateSummary(long masterUsrId){
		try{
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "evaluateTotal",  this.fsOrderEvaluateDao.statEvaluateNum(null, masterUsrId, null) ); //评价总数
			Map  avgScore = 	this.fsOrderEvaluateDao.statMasterAvgScore(null, masterUsrId, null);
			Double respSpeed = UsrAidUtil.getEvaluateFromMap(avgScore , "resp_speed",0d)	;	
			Double majorLevel = UsrAidUtil.getEvaluateFromMap(avgScore , "major_level",0d)	;	
			Double serviceAttitude = UsrAidUtil.getEvaluateFromMap(avgScore , "service_attitude",0d)	;	
			JsonUtils.setBody(result, "respSpeedAvgScore",  respSpeed );   //响应速度平均分
			JsonUtils.setBody(result, "majorLevelAvgScore", majorLevel 	);  //专业水平平均分
			JsonUtils.setBody(result, "serviceAttitudeAvgScore", serviceAttitude );  //服务态度平均分
			JsonUtils.setBody(result, "score",  (respSpeed+majorLevel +serviceAttitude)/3d );  //总评分
			JsonUtils.setBody(result, "scoreDesc", CommonUtils.numberFormat( (respSpeed+majorLevel +serviceAttitude)/3d  , "###,##0.00", "0.00")  );  //总评分

			return result;
		}catch(Exception e){
			logger.error("masterUsrId:"+masterUsrId, e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
	private List<Long> getBuyUsrIds(List<Map>  list){
		List<Long> buyUsrIds = new ArrayList<Long>();
		Long _usrId = null;
		for(Map map :  list){
			_usrId = Long.valueOf(map.get("buy_usr_id").toString());
			if(!buyUsrIds.contains(_usrId)){
				buyUsrIds.add(_usrId);
			}
		}
		return buyUsrIds;
	}
	
	public JSONObject masterEvaluateList(long masterUsrId , int currentPage,int perPageNum){
		try{
			List<Map>  list = this.fsOrderEvaluateDao.findMasterEvaluateList(masterUsrId, currentPage, perPageNum);
			if(CollectionUtils.isEmpty(list)){
				return JsonUtils.commonJsonReturn("1000", "查无数据");
			}
			List<Long> buyUsrIds = this.getBuyUsrIds(list);
			Map<Long, FsUsr> idUsrMap = this.fsUsrDao.findShortInfo1ByUsrIds(buyUsrIds);
			JSONArray dataList = new JSONArray();
			FsUsr _usr = null;
			Long _usrId = null;
			for(Map map :  list){
				_usrId =  Long.valueOf(map.get("buy_usr_id").toString());
				_usr = idUsrMap.get( _usrId ) ;
				JSONObject cacheWeiXinUsrInfo = UsrAidUtil.getCacheWeiXinInfo(_usr);
				JSONObject dataOne = new JSONObject(true);
				String buyUsrHeadImgUrl = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.service.basehost") + "/static/images/def_headimg.png";
				Integer isAnonymous = (Integer)map.get("is_anonymous");
				String userName = "匿名评价";
				if (isAnonymous == 0) {
					buyUsrHeadImgUrl = UsrAidUtil.getUsrHeadImgUrl2(_usr, cacheWeiXinUsrInfo, "");
					userName = UsrAidUtil.getNickName2(_usr, cacheWeiXinUsrInfo, "");
				}
				dataOne.put("buyUsrName", userName);
				dataOne.put("buyUsrHeadImgUrl", buyUsrHeadImgUrl);		
				dataOne.put("goodsName", (String)map.get("goods_name"));
				dataOne.put("evaluateTime", CommonUtils.dateToString((Date) map.get("create_time"), CommonUtils.dateFormat4, "")  );  //评价时间
				dataOne.put("evaluateWord", (String)map.get("evaluate_word") );
				dataOne.put("isAnonymous", isAnonymous);
				dataOne.put("masterReplyWord", (String)map.get("master_reply_word"));
				dataOne.put("masterReplyTime", CommonUtils.dateToString((Date) map.get("master_reply_time"), CommonUtils.dateFormat4, ""));
				dataList.add(dataOne);
			}
			JSONObject result = JsonUtils.commonJsonReturn();
			JsonUtils.setBody(result, "data", dataList);
			JsonUtils.setBody(result, "size", dataList.size());
			return result;
		}catch(Exception e){
			logger.error("masterUsrId:{};currentPage:{};perPageNum:{}", masterUsrId , currentPage  , perPageNum );
			logger.error("error",e);
			return JsonUtils.commonJsonReturn("9999", "系统错误");
		}
	}
	
}

