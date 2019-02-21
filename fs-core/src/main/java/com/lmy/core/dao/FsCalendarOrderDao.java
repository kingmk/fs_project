package com.lmy.core.dao;

import org.springframework.stereotype.Component;

import com.lmy.core.model.FsCalendarOrder;

@Component
public class FsCalendarOrderDao extends GenericDAOImpl<FsCalendarOrder> {

	@Override
	public String getNameSpace() {
		// TODO Auto-generated method stub
		return "fs_calendar_order";
	}

}
