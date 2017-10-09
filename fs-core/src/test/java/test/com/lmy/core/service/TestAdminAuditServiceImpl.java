package test.com.lmy.core.service;

import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.core.beanstalkd.job.QueueNameConstant;
import com.lmy.core.service.impl.AdminAuditServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestAdminAuditServiceImpl extends BaseTestCase {
	@SpringBean("adminAuditServiceImpl")
	private AdminAuditServiceImpl adminAuditServiceImpl;
	
	public void test_refundApplyAudit() throws InterruptedException{
		long orderId = 100120;
		String isAgree = "Y";
		String refundAuditWord = "Test refundAuditWord";
		JSONObject result = adminAuditServiceImpl.refundApplyAudit(orderId, isAgree, refundAuditWord);
		System.out.println( result);
		Thread.sleep( 10 * 1000 );
	}
	
	public void test_2(){
		JSONObject waitConfrimRefundData = new JSONObject();
		waitConfrimRefundData.put("payRecordId", 100181);
		BeanstalkClient.put(QueueNameConstant.QUEUE_WXPAY_CONFIRM, null, 3, null, waitConfrimRefundData);	
	}
	
}
