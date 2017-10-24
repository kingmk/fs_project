package com.lmy.core.beanstalkd.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.queue.beanstalkd.QueueHandler;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.model.FsMasterInfo;

@Service
public class AdminQueueManagerServiceImpl extends QueueHandler {
	private static Logger logger = LoggerFactory.getLogger(AdminQueueManagerServiceImpl.class);
	@Autowired
	private FsMasterInfoDao fsMasterInfoDao;

	@Override
	public String getQueueName() {
		return QueueNameConstant.QUEUE_ADMIN;
	}

	@Override
	public Object handle(JSONObject data) throws Exception {
		try{
			if(data == null || data.isEmpty() || !data.containsKey("orderId") || !data.containsKey("msgType")){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			String msgType = data.getString("msgType");
			if(msgType == null){
				logger.warn("参数格式错误data:{}", data);
				return null;
			}
			if (msgType.equals(QueueNameConstant.MSG_ADMIN_UNFORBID_MASTER)) {
				unforbidMaster(data);
			}
			
		}catch(Exception e){
			logger.error("处理出错 data:{}", data,e);
		}
		return null;
	}
	
	private void unforbidMaster(JSONObject data) {
		Long masterInfoId = data.getLong("masterInfoId");
		if (masterInfoId == null){
			logger.warn("参数格式错误data:{}", data);
		}
		FsMasterInfo master = fsMasterInfoDao.findById(masterInfoId);
		if (master == null) {
			logger.warn("找不到该老师data:{}", data);
		}
		if (master.getServiceStatus().equals("FORBID") && (master.getForbidTime().getTime() < (new Date()).getTime()+2)) {
			FsMasterInfo masterUpdate = new FsMasterInfo();
			masterUpdate.setId(masterInfoId);
			masterUpdate.setUpdateTime(new Date()).setServiceStatus("NOTING").setForbidTime(null);
			fsMasterInfoDao.update(masterUpdate);
		}
	}

}
