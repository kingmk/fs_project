package test.com.lmy.core.service;
import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.service.impl.SearchMasterServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestSearchMasterServiceImpl extends BaseTestCase {

	@SpringBean("searchMasterServiceImpl")
	SearchMasterServiceImpl searchMasterServiceImpl;
	//orderNumDesc,orderNumAsc; priceDesc,priceAsc, evaluateScoreDesc,evaluateScoreAsc
	
	public  void test_searchPlatRec(){
		String orderBy = "evaluateScoreDesc";
		JSONObject result =   searchMasterServiceImpl.search(null, null, orderBy, 0, 10);
		System.out.println( result );
	}
	
	public  void test_search2(){
		String orderBy = "evaluateScoreAsc";
		JSONObject result =   searchMasterServiceImpl.search("Y", null, orderBy, 0, 10);
		System.out.println( result );
	}
	
	public  void test_search3(){
		String orderBy = "";
		Long cateId = 100001l;
		JSONObject result =   searchMasterServiceImpl.search(null, cateId, orderBy, 0, 10);
		System.out.println( result );
	}
	
	public  void test_searchDoubleContion(){
		String orderBy = "orderNumDesc";
		Long cateId = 100001l;
		JSONObject result =   searchMasterServiceImpl.search("Y", cateId, orderBy, 0, 10);
		System.out.println( JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) );
	}
	
}
