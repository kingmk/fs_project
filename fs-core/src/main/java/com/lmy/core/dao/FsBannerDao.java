package com.lmy.core.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lmy.core.model.FsBanner;

@Component
public class FsBannerDao extends GenericDAOImpl<FsBanner> {

	@Override
	public String getNameSpace() {
		return "fs_banner";
	}
	
	public List<FsBanner> getAllActive() {
		return this.getSqlSession().selectList(this.getNameSpace()+".getAllActive");
	}

}
