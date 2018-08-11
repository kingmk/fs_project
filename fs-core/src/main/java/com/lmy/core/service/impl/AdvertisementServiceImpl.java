package com.lmy.core.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.core.dao.FsBannerDao;
import com.lmy.core.model.FsBanner;

@Service
public class AdvertisementServiceImpl {
	private static final Logger logger = LoggerFactory.getLogger(AdvertisementServiceImpl.class);
	
	@Autowired
	private FsBannerDao fsBannerDao;
	
	public JSONArray getIndexBanner() {
		List<FsBanner> bannerList = fsBannerDao.getAllActive();
		JSONArray result = new JSONArray();
		for (FsBanner banner: bannerList) {
			result.add(JSONObject.parse(banner.toString()));
		}
		logger.info("====="+result.toJSONString()+"=====");
		return result;
	}
}
