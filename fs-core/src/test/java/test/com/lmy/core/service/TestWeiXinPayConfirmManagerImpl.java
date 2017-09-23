package test.com.lmy.core.service;

import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.beanstalkd.job.WeiXinPayConfirmManagerImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestWeiXinPayConfirmManagerImpl extends BaseTestCase {

	@SpringBean("weiXinPayConfirmManagerImpl")
	WeiXinPayConfirmManagerImpl weiXinPayConfirmManagerImpl;
	
	@Test
	public void test_hand() throws Exception{
		JSONObject data = new JSONObject();
		data.put("payRecordId", 100042l);
		data.put("_errorTimes", 4);
		weiXinPayConfirmManagerImpl.hander(data);
	}
	
	
}
