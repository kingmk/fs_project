package com.lmy.core.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.model.FsOrder;

@Service
public class ZDataFixImpl {
	private static final Logger logger = Logger.getLogger(ZDataFixImpl.class);

	@Autowired
	private FsOrderDao fsOrderDao;
	
	public JSONObject fixOrderRespSeconds() {
		
		List<FsOrder> orderList = null;
		int page = 0;
		int pagecount = 50;
		int countall = 0;
		int countupdate = 0;
		
		while(true) {
			orderList = fsOrderDao.findOrder1(null, null, null, null, page, pagecount, 20, OrderAidUtil.getCommAllOrderStatus());
			if (orderList.size() == 0) {
				break;
			}
			logger.info("prepare to update "+orderList.size()+" orders in page "+page);
			for (FsOrder order: orderList) {
				countall++;
				if (order.getSellerFirstReplyTime() == null) {
					continue;
				}
				order.setRespSeconds(OrderAidUtil.calcOrderRespSeconds(order.getCreateTime(), order.getSellerFirstReplyTime()));
				fsOrderDao.update(order);
				countupdate++;
			}
			page++;
			
			if (orderList.size() < pagecount) {
				break;
			}
		}
		return JsonUtils.commonJsonReturn("0000", "处理完成，更新："+countupdate+"/"+countall);
	}

}
