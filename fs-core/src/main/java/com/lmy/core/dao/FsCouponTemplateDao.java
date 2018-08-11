package com.lmy.core.dao;

import org.springframework.stereotype.Component;

import com.lmy.core.model.FsCouponTemplate;

@Component
public class FsCouponTemplateDao extends GenericDAOImpl<FsCouponTemplate> {
	@Override
	public String getNameSpace() {
		return "fs_coupon_template";
	}

}
