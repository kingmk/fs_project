package test.com.lmy.core.service;

import org.junit.Test;
import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.OrderQueryServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestOrderQueryServiceImpl extends BaseTestCase {

	@SpringBean("orderQueryServiceImpl")
	OrderQueryServiceImpl orderQueryServiceImpl;
	
	@Test
	public void test_masterIncomeDetail(){
		Long masterUsrId = 100005l;
		JSONObject result = orderQueryServiceImpl.masterIncomeDetail(masterUsrId, 0, 20, null);
		System.out.println( result );
	}
	
	public void test_findCommonUsrOrderList(){
		JSONObject result = orderQueryServiceImpl.findCommonUsrOrderList(100009l, 3, 1, 0, null);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) );
	}
	
	public void test_findMasterUsrBillList(){
		JSONObject result = orderQueryServiceImpl.findBillList(100142l, 0, 10);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) );
	}
	
	public void test_findMasterUsrBillDetailList(){
		JSONObject result = orderQueryServiceImpl.findMasterUsrBillDetailList(100142l, 100026l, 0, 10);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) );
	}
	
	
	
}
