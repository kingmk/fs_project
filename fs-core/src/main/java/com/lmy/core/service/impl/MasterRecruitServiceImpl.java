package com.lmy.core.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.Sleep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsMasterServiceCateDao;
import com.lmy.core.dao.FsReserveDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsMasterServiceCate;
import com.lmy.core.model.FsReserve;
import com.lmy.core.model.FsUsr;
import com.lmy.core.model.FsZxCate;
import com.lmy.core.utils.FsExecutorUtil;

@Service
public class MasterRecruitServiceImpl {
	private static final Logger logger = Logger.getLogger(MasterRecruitServiceImpl.class);
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;
	@Autowired
	private FsReserveDao  fsReserveDao;
	@Autowired
	private FsUsrDao fsUsrDao;
	@Autowired
	private FsMasterServiceCateDao fsMasterServiceCateDao;
	@Autowired
	private FsZxCateDao  fsZxCateDao;
	@Autowired
	private org.springframework.transaction.support.TransactionTemplate fsTransactionTemplate;
	/**
	 * 申请成为大师
	 * @param masterInfo
	 * @return
	 */
	public JSONObject applyMaster(FsMasterInfo masterInfo){
		try{
			if(masterInfo == null || masterInfo.getUsrId() ==null){
				return JsonUtils.commonJsonReturn("0001","数据错误");
			}
			List list = fsMasterInfoDao.findBtCondition1(null, masterInfo.getUsrId(), null, Arrays.asList("ing","approved"), null);
			if(CollectionUtils.isNotEmpty(list)){
				return JsonUtils.commonJsonReturn("0002","不可重复申请");
			}
			fsMasterInfoDao.insert(masterInfo);
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("masterInfo:"+masterInfo,e);
			return JsonUtils.commonJsonReturn("9999","系统异常");
		}
	}
	
	/**
	 * 申请大师 后台审批
	 * @param masterInfoId
	 * @param auditStatus  审批/申请状态 ing 审批中;approved 审批通过;refuse 审批拒绝(不通过)
	 * @param auditWord  审批语
	 * @return
	 */
	public JSONObject applyMasterAduit(Long masterInfoId , String auditStatus , String auditWord){
		return JsonUtils.commonJsonReturn();
	}
	
	/**
	 * 补充 资料
	 * @param masterInfo
	 * @return
	 */
	public JSONObject supplyInfo(final FsMasterInfo masterInfoForUpdate,final Long usrId){
		try{
			fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					fsMasterInfoDao.update(masterInfoForUpdate);
					if(StringUtils.isNotEmpty(masterInfoForUpdate.getHeadImgUrl())){
						FsUsr usrForUpdate = new FsUsr();
						usrForUpdate.setId(usrId);
						usrForUpdate.setUsrHeadImgUrl(masterInfoForUpdate.getHeadImgUrl());
						fsUsrDao.update(usrForUpdate);
					}
					return true;
				}
			});			
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("usrId:"+usrId+",masterInfoForUpdate:"+masterInfoForUpdate, e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
	}
	/**
	 * 设置 服务类别(咨询类目)
	 * @param data  [{"id":"","amt":"","status":"ON","fsZxCateId":"100002","fsMasterInfoId":"100001"},{"id":"","amt":"","status":"ON","fsZxCateId":"100003","fsMasterInfoId":"100001"}]
	 * @return
	 */
	public JSONObject configServiceInfo(final JSONArray data ,final  Long usrId){
		if(CollectionUtils.isEmpty(data) || usrId == null){
			return JsonUtils.commonJsonReturn("0001","非法请求");
		}
		try{
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					do_configServiceInfo(data, usrId);
					return true;
				}
			});
			return JsonUtils.commonJsonReturn();
		}catch(Exception e){
			logger.error("usrId="+usrId+", data="+data, e);
			return JsonUtils.commonJsonReturn("9999" , "系统错误");
		}
	}
	
	private void do_configServiceInfo(final JSONArray data ,final  Long usrId){
		Date now = new Date();
		Map<Long,FsZxCate> idFsZxCateMap = new HashMap<Long,FsZxCate>();
		
		logger.info("====== ##update master service## "+data.toJSONString()+"======");
		for(int i=0; i< data.size() ; i++){
			JSONObject dataOne = data.getJSONObject(i);
			long zxCateId = dataOne.getLong( "fsZxCateId" );
			long masterInfoId = dataOne.getLong("fsMasterInfoId");
			if(dataOne.getLong( "id" ) !=null){
				Long id  = dataOne.getLongValue("id");
				FsMasterServiceCate beanForUpdate = new FsMasterServiceCate();
				beanForUpdate.setId(id);
				beanForUpdate.setUpdateTime(now);
				beanForUpdate.setStatus( dataOne.getString("status") );
				Long  _fsZxCateId =    dataOne.getLong( "fsZxCateId" )    ;
				if(_fsZxCateId !=null){
					beanForUpdate.setName( this.getZxCateBean(_fsZxCateId, idFsZxCateMap).getName() );
				}
				if( StringUtils.isNotEmpty( dataOne.getString("amt") )){
					beanForUpdate.setAmt( dataOne.getLongValue( "amt" ) ) ;
				}
				int effectNum = this.fsMasterServiceCateDao.update(beanForUpdate);
				Assert.isTrue( effectNum ==1 );
			}else{
				this.fsMasterServiceCateDao.offlineMasterCateService(masterInfoId, zxCateId);
				FsMasterServiceCate beanForInsert = new FsMasterServiceCate();
				beanForInsert.setAmt( dataOne.getLongValue( "amt" ) );
				beanForInsert.setCreateTime(now);
				beanForInsert.setFsMasterInfoId(  dataOne.getLong( "fsMasterInfoId" )  ) ;
				beanForInsert.setFsZxCateId(    dataOne.getLong( "fsZxCateId" )    );
				beanForInsert.setIsPlatRecomm("N");
				beanForInsert.setName( this.getZxCateBean(beanForInsert.getFsZxCateId(), idFsZxCateMap) .getName()   ) ; 
				beanForInsert.setStatus("ON");
				beanForInsert.setUpdateTime(now);
				beanForInsert.setUsrId(usrId);
				this.fsMasterServiceCateDao.insert(beanForInsert);
			}
		}// end for
	}	
	
	private FsZxCate getZxCateBean(Long zxCateId,Map<Long,FsZxCate> idFsZxCateMap){
		if(idFsZxCateMap.containsKey(zxCateId)){
			return  idFsZxCateMap.get(zxCateId);
		}else{
			FsZxCate bean =  this.fsZxCateDao.findById(zxCateId) ;   
			idFsZxCateMap.put(zxCateId, bean);
			return bean;
		}
	}
	
	
	/** 设置接单状态 **/
	public JSONObject configOderTaking(final Long fsMasterInfoId ,final  Long usrId , final String serviceStatus){
		//当前服务状态 ING 服务中;NOTING 非服务状态
		if( !StringUtils.equals("ING", serviceStatus) && !StringUtils.equals("NOTING", serviceStatus)	) {
			logger.warn("serviceStatus 参数错误 serviceStatus:"+serviceStatus+", fsMasterInfoId:"+fsMasterInfoId+", usrId="+usrId);
			return JsonUtils.commonJsonReturn("0001","参数错误");
		}
		if( fsMasterInfoId == null && usrId == null ){
			logger.warn("参数错误 serviceStatus:"+serviceStatus+", fsMasterInfoId:"+fsMasterInfoId+", usrId="+usrId);
			return JsonUtils.commonJsonReturn("0001","参数错误");
		}
		FsMasterInfo masterInfo = fsMasterInfoDao.findById(fsMasterInfoId);
		if (masterInfo.getServiceStatus().equals("FORBID")) {
			logger.warn("current status is forbid, for "+fsMasterInfoId+", usrId="+usrId);
			return JsonUtils.commonJsonReturn("0001","您已经被强制下线，请联系客服");
		}
		final long masterUsrId = masterInfo.getUsrId();
		final String masterNickName = masterInfo.getNickName();
		try{
			this.fsTransactionTemplate.execute(new TransactionCallback<Boolean>() {
				@Override
				public Boolean doInTransaction(TransactionStatus status) {
					int effectNum = fsMasterInfoDao.updateServiceStatus(fsMasterInfoId, usrId, serviceStatus);
					
					if(serviceStatus.equals("ING")) {
						Date now = new Date();
						List<FsReserve> reserveList = fsReserveDao.findByMasterUsrId(masterUsrId);
						fsReserveDao.noticeReserveUsrByMaster(masterUsrId, now);
						asynReserveNote(masterUsrId, masterNickName, reserveList);
					}
					
					Assert.isTrue( effectNum>0 );
					return true;
				}
			});
		}catch(Exception e){
			logger.error("系统错误 serviceStatus:"+serviceStatus+", fsMasterInfoId:"+fsMasterInfoId+", usrId="+usrId,e);
			return JsonUtils.commonJsonReturn("9999","系统错误");
		}
		return JsonUtils.commonJsonReturn();
	}

	private void asynReserveNote(final Long masterUsrId, final String masterNickName, final List<FsReserve> reserveList){
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try{
					JSONArray params = new JSONArray();
					params.add(masterNickName);
					for (FsReserve reserve: reserveList) {
						String mobile = reserve.getMobile();
						if (mobile != null && mobile.length() == 11) {
							logger.info("======向用户发送上线通知短信："+mobile+"======");
							TencentSmsFacadeImpl.sendSms(52087, params, mobile);
						}
					}
				}catch(Exception  e){
					logger.error("======向用户发送老师上线提醒短信失败======", e);
				}				
			}
		};
		FsExecutorUtil.getExecutor().execute(r);
	}
}
