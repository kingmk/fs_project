package com.lmy.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, List<FsZxCate>> getSortedCates() {
		List<FsZxCate> allCates = fsZxCateDao.findZxCate1(null, null, 2l, null, "EFFECT");
		
		Map<String, List<FsZxCate>> rlt = new HashMap<String, List<FsZxCate>>();
		
		for (FsZxCate cate: allCates) {
			String pid = cate.getParentId().toString();
			List<FsZxCate> tmpList = rlt.get(pid);
			if (tmpList == null || tmpList.isEmpty()) {
				tmpList = new ArrayList<>();
			}
			tmpList.add(cate);
			rlt.put(pid, tmpList);
		}
		
		return rlt;
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
