package com.lmy.core.service.impl;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.FsUsr;
@Service
public class AdminAuditServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(AdminAuditServiceImpl.class);
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsOrderDao fsOrderDao;
	@Autowired
	private OrderRefundServiceImpl orderRefundServiceImpl;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	
	/** 老师申请 后台审核 **/
	public JSONObject masterApplyAudit(final long masterInfoId,final String auditStatus ,final  String auditWord){
		try{
			if(!StringUtils.equals("approved", auditStatus) && !StringUtils.equals("refuse", auditStatus)){
				logger.warn("参数错误 masterInfoId:{} , auditStatus:{} auditWord:{}", masterInfoId , auditStatus , auditWord );
				return JsonUtils.commonJsonReturn("0001", "参数错误");
			}
			final FsMasterInfo  masterInfo = fsMasterInfoDao.findById(masterInfoId);
			if(masterInfo == null  ){
				logger.warn("查无错误 masterInfoId:{} , auditStatus:{} auditWord:{}", masterInfoId , auditStatus , auditWord );
				return JsonUtils.commonJsonReturn("0002", "查无错误");
			}
			if(  !StringUtils.equals("ing", masterInfo.getAuditStatus()) ){
				logger.warn("数据状态错误  masterInfoId:{} , auditStatus:{} auditWord:{} , 当前状态:{} ,期待状态 ing", masterInfoId , auditStatus , auditWord , masterInfo.getAuditStatus() );
				return JsonUtils.commonJsonReturn("0002", "数据状态错误");
			}
			fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					Date now = new Date();
					FsMasterInfo masterInfoForUpdate = new FsMasterInfo();
					masterInfoForUpdate.setId(masterInfoId  );
					masterInfoForUpdate.setAuditStatus(auditStatus).setAuditWord(auditWord).setAuditTime(now).setUpdateTime(now).setServiceStatus("NOTING");
					fsMasterInfoDao.update(masterInfoForUpdate);
					FsUsr usrForUpdate = new FsUsr();
					usrForUpdate.setId( masterInfo.getUsrId() );
					if(StringUtils.equals("approved", auditStatus) ){
						usrForUpdate.setUsrCate("master");		
					}
					if(StringUtils.isNotEmpty(masterInfo.getEnglishName())){
						usrForUpdate.setEnglishName(masterInfo.getEnglishName());
					}
					if(StringUtils.isNotEmpty(masterInfo.getNickName())){
						usrForUpdate.setNickName(masterInfo.getNickName());
					}
					if(StringUtils.isNotEmpty(masterInfo.getName())){
						usrForUpdate.setRealName(masterInfo.getName());
					}
					if(StringUtils.isNotEmpty(masterInfo.getHeadImgUrl())){
						usrForUpdate.setUsrHeadImgUrl(masterInfo.getHeadImgUrl());
					}
					if(StringUtils.isNotEmpty(masterInfo.getSex())){
						usrForUpdate.setSex(masterInfo.getSex());
					}
					if(StringUtils.isNotEmpty(masterInfo.getBirthDate())){
						usrForUpdate.setBirthDate(masterInfo.getBirthDate());
					}
					if(masterInfo.getBirthYear() !=null ){
						usrForUpdate.setBirthYear(masterInfo.getBirthYear());
					}
					if(masterInfo.getBirthYear() !=null ){
						usrForUpdate.setBirthYear(masterInfo.getBirthYear());
					}
					fsUsrDao.update(usrForUpdate);
					return true;
				}
			});
			masterApplyAuditSendSms(masterInfo, auditStatus, auditWord);
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("老师申请审批系统错误  masterInfoId:{} , auditStatus:{} auditWord:{} ", masterInfoId , auditStatus , auditWord  );
			logger.error("老师申请审批系统错误", e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	
	
	private void masterApplyAuditSendSms(FsMasterInfo  masterInfo , String auditStatus, String auditWord){
		if(StringUtils.equals("approved", auditStatus) ){
			AlidayuSmsFacadeImpl.alidayuSmsSend(null, masterInfo.getContactMobile(), "SMS_69740105" , null);
		}
		else if( StringUtils.equals("refuse", auditStatus)){
			//给老师发送短信
			JSONObject smsParamJson = new JSONObject();
			smsParamJson.put("reason",auditWord);
			smsParamJson.put("product","雷门易");
			AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, masterInfo.getContactMobile(), "SMS_69925055" , null);
		}
	}
	
	
	public JSONObject refundApplyAudit(long orderId,String isAgree,String refundAuditWord){
		try{
			if( !StringUtils.equals(isAgree, "Y") && !StringUtils.equals(isAgree, "N") ){
				logger.error("退款申请审批 参数错误  orderId:{} , isAgree:{} refundAuditWord:{} ", orderId , isAgree , refundAuditWord  );
				return JsonUtils.commonJsonReturn("0001", "参数错误");
			}
			final FsOrder order = 	fsOrderDao.findById(orderId);
			return orderRefundServiceImpl.refundAudit(order, StringUtils.equals(isAgree, "Y"), false, order.getRefundReason(), refundAuditWord);
		}catch(Exception e){
			logger.error("退款申请审批系统错误  orderId:{} , isAgree:{} refundAuditWord:{} ", orderId , isAgree , refundAuditWord  );
			logger.error("退款申请审批系统错误", e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
}
