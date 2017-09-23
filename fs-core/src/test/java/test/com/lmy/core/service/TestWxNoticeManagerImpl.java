package test.com.lmy.core.service;
import org.unitils.spring.annotation.SpringBean;

import com.lmy.core.dao.FsOrderDao;
import com.lmy.core.manage.impl.WxNoticeManagerImpl;
import com.lmy.core.model.FsOrder;

import test.com.lmy.core.base.BaseTestCase;

public class TestWxNoticeManagerImpl extends BaseTestCase {
	
	@SpringBean("wxNoticeManagerImpl")
	WxNoticeManagerImpl wxNoticeManagerImpl;
	@SpringBean("fsOrderDao")
	FsOrderDao	 fsOrderDao;
	
	public void test_masterNewOrderMsg() throws InterruptedException{
		FsOrder order = fsOrderDao.findById(100053l);
		//wxNoticeManagerImpl.masterNewOrderMsg(order);
		
		Thread.sleep(10 * 6000);
		
	}
	
	
}
