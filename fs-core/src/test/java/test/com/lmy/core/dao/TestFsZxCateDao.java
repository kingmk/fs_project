package test.com.lmy.core.dao;
import java.util.List;

import org.unitils.spring.annotation.SpringBean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.core.dao.FsZxCateDao;
import com.lmy.core.model.FsZxCate;

import test.com.lmy.core.base.BaseTestCase;

public class TestFsZxCateDao extends BaseTestCase {

	@SpringBean("fsZxCateDao")
	FsZxCateDao fsZxCateDao;
	
	public void test_buildCateJsData(){
		 //预测
		List<FsZxCate> list1 = 	fsZxCateDao.findZxCate1(null, 10000l, null, null, null);
		//择吉
		List<FsZxCate> list2 = 	fsZxCateDao.findZxCate1(null, 10001l, null, null, null);
		//起名
		List<FsZxCate> list3 = 	fsZxCateDao.findZxCate1(null, 10003l, null, null, null);
		//堪舆
		List<FsZxCate> list4 = 	fsZxCateDao.findZxCate1(null, 10004l, null, null, null);
		JSONArray dataList  =new JSONArray();
		
		dataList.add(  buildOne("预测", 10000l, list1) );
		dataList.add(  buildOne("择吉", 10001l,  list2) );
		dataList.add(  buildOne("起名", 10003l, list3) );
		dataList.add(  buildOne("堪舆",  10004l,list4) );
		
		System.out.println( dataList );
	}
	
	private JSONObject buildOne( String group,Long groupId,  List<FsZxCate> list1   ){
		JSONObject result = new JSONObject(true);
		result.put("group", group);
		result.put("groupId", groupId);
		JSONArray dataList  =new JSONArray();
		for(FsZxCate bean :  list1){
			JSONObject dataOne = new JSONObject(true);
			dataOne.put("id", bean.getId());
			dataOne.put("text", bean.getName());
			dataList.add(dataOne);
		}
		result.put("subItem", dataList);
		return result;
	}
	
	
}
