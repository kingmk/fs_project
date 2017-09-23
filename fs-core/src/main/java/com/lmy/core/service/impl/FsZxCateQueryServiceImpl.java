package com.lmy.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lmy.core.dao.FsMasterServiceCateDao;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsMasterServiceCate;
import com.lmy.core.model.FsZxCate;
@Service
public class FsZxCateQueryServiceImpl {

	@Autowired
	private FsZxCateDao fsZxCateDao;
	@Autowired 
	private FsMasterServiceCateDao fsMasterServiceCateDao;
	
	public List<FsZxCate> findZxCate1(Long id , Long parentId , Long level , String usrDefined, String status){
		return fsZxCateDao.findZxCate1(id, parentId, level, usrDefined, status);
	}
	
	public FsMasterServiceCate findByMasterInfoIdAndId(long masterInfoId , long id	){
		FsMasterServiceCate bean = 	fsMasterServiceCateDao.findById(id);
		if(bean!=null && bean.getFsMasterInfoId().equals(masterInfoId)){
			return bean;
		}else{
			return null;
		}
	}
	
}
