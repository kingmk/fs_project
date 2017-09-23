package test.com.lmy.core.service;

import java.util.List;

import org.unitils.spring.annotation.SpringBean;

import com.lmy.core.service.impl.MasterQueryServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestMasterQueryServiceImpl extends BaseTestCase {

	@SpringBean("masterQueryServiceImpl")
	private MasterQueryServiceImpl masterQueryServiceImpl;
	
	public void test_findBtCondition1(){
		
	}
	
	public void test_findForConfigServerStep1(){
		Long usrId = 100005l;
		List list = masterQueryServiceImpl.findForConfigServerStep1(usrId);
		System.out.println(  list );
	}
}
