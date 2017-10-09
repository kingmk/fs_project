package com.lmy.core.beanstalkd.job;
import com.lmy.common.component.SpringContextUtil;
import com.lmy.common.queue.beanstalkd.AbstractStartJob;

public class LmyCoreQueueJob extends AbstractStartJob {
	public static void main(String[] args) throws Exception {
		LmyCoreQueueJob startJob = new LmyCoreQueueJob();
		String res = "classpath:applicationContext-core.xml";
		OrderChatReadConfirmManagerServiceImpl orderChatReadConfirmManagerServiceImpl =(OrderChatReadConfirmManagerServiceImpl)SpringContextUtil.getBean(res,"orderChatReadConfirmManagerServiceImpl");
		OrderQueueManagerServiceImpl orderQueueManagerServiceImpl = (OrderQueueManagerServiceImpl)SpringContextUtil.getBean(res,"orderQueueManagerServiceImpl");
		PayAfter24HoursManagerImpl payAfter24HoursManagerImpl =(PayAfter24HoursManagerImpl)SpringContextUtil.getBean(res,"payAfter24HoursManagerImpl");
		WeiXinPayConfirmManagerImpl weiXinPayConfirmManagerImpl = (WeiXinPayConfirmManagerImpl)SpringContextUtil.getBean(res,"weiXinPayConfirmManagerImpl");
		startJob.setHandler(1800 , orderChatReadConfirmManagerServiceImpl);
		startJob.setHandler(1800 +2, payAfter24HoursManagerImpl);
		startJob.setHandler(1800 +4, weiXinPayConfirmManagerImpl);
		startJob.setHandler(1800 +8, orderQueueManagerServiceImpl);
		startJob.begin(args);
	}
}
