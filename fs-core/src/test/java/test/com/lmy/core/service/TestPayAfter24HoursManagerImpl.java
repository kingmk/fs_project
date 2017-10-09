package test.com.lmy.core.service;

import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.dao.FsChatRecordDao;
import com.lmy.core.model.FsPayRecord;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestPayAfter24HoursManagerImpl extends BaseTestCase{
	
	@SpringBean("payAfter24HoursManagerImpl")
	com.lmy.core.beanstalkd.job.PayAfter24HoursManagerImpl payAfter24HoursManagerImpl;
	@SpringBean("fsChatRecordDao")
	FsChatRecordDao fsChatRecordDao;
	@Test
	public void test_hander() throws Exception{
		JSONObject data = new JSONObject();
		data.put("orderId", 100039);
		data.put("_errorTimes", 4);
		payAfter24HoursManagerImpl.handle(data);
	}
	
	@Test
	public void test_weixinrefund(){
		FsPayRecord orgPaySuccBean = new FsPayRecord();
		orgPaySuccBean.setRespTradeNo("4000852001201705222080911385").setFeeType("CNY");
		
		//FsPayRecord orgPaySuccBean = new FsPayRecord();
		
		JSONObject result1 = WeiXinInterServiceImpl.refundQuery1("201704242300000133");
		System.out.println(  result1  );
		
	}
	
	
	
	
	@Test
	public void test_weixinrefundQuery1(){
		//JSONObject result = WeiXinInterServiceImpl.refundQuery1("201704261800000137");
		//System.out.println(  result  );
		
		JSONObject result1 = WeiXinInterServiceImpl.refundQuery1("201704242300000133");
		System.out.println(  result1  );
		
	}
	
	
	
	
}
