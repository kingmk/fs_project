package com.lmy.core.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.lmy.common.component.CommonUtils;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsUsr;

@Service
public class UsrQueryImpl {
	private static final Logger logger = Logger.getLogger(UsrQueryImpl.class); 
	@Autowired
	private FsUsrDao fsUsrDao;
	public boolean isMobileRegister(String mobile){
		if(logger.isDebugEnabled()){
			logger.debug( "mobile:"+mobile );
		}	
		Assert.isTrue( CommonUtils.checkForMobile(mobile) );
		Long num = this.fsUsrDao.statNumByMobile(mobile);
		return num>0;
	}
	
	public FsUsr getUserById(long usrId) {
		return fsUsrDao.findById(usrId);
	}
}
