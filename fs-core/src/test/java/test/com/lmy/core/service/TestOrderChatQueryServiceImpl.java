 package test.com.lmy.core.service;

import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.OrderChatQueryServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestOrderChatQueryServiceImpl extends BaseTestCase {

	@SpringBean("orderChatQueryServiceImpl")
	OrderChatQueryServiceImpl orderChatQueryServiceImpl;
	
	
	public void test_queryForChatIndex(){
		
		JSONObject result = orderChatQueryServiceImpl.queryForChatIndex(100041l, 100005l, "201704240100000126",false);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) );
		
	}
	
	
public void test_queryAjax(){
		
		JSONObject result = orderChatQueryServiceImpl.queryAjax(null, null, null, 100009l, 100041l, "201704240100000126", 0, 10);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) );
	}
	
	
	
	
}
