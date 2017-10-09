package test.com.lmy.core.service;
import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.beanstalkd.job.OrderChatReadConfirmManagerServiceImpl;

import test.com.lmy.core.base.BaseTestCase;
public class TestOrderChatReadConfirmManagerServiceImpl extends BaseTestCase {

	@SpringBean("orderChatReadConfirmManagerServiceImpl")
	OrderChatReadConfirmManagerServiceImpl orderChatReadConfirmManagerServiceImpl;
	@Test
	public void test_hand() throws Exception{
		JSONObject data = new JSONObject();
		data.put("chatRecordId", 100117);
		orderChatReadConfirmManagerServiceImpl.handle(data);
	}
}
