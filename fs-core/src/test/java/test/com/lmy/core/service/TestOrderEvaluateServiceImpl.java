package test.com.lmy.core.service;

import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.FollowServiceImpl;
import com.lmy.core.service.impl.OrderEvaluateServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestOrderEvaluateServiceImpl extends BaseTestCase {
	@SpringBean("orderEvaluateServiceImpl")
	OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	
	@SpringBean("followServiceImpl")
	FollowServiceImpl followServiceImpl;
	
	public void test_masterEvaluateSummary(){

		JSONObject result = orderEvaluateServiceImpl.masterEvaluateSummary(10005l);
		System.out.println( result  );
	}
	
	public void test_masterEvaluateList(){

		JSONObject result = orderEvaluateServiceImpl.masterEvaluateList(10005l, 0 , 10);
		System.out.println( result  );
	}
	
	public void test_commonUsrView(){
		JSONObject result = orderEvaluateServiceImpl.commonUsrView(100009l, 100041l);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue)   );
	}
	
	public void test_findMyFollow(){
		JSONObject result  = followServiceImpl.findMyFollow(100009l, 0, 10);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue)   );
	}
	
}
