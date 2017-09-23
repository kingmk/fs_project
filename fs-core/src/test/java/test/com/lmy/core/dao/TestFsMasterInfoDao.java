package test.com.lmy.core.dao;
import java.util.Date;

import org.unitils.spring.annotation.SpringBean;

import test.com.lmy.core.base.BaseTestCase;

import com.lmy.common.component.CommonUtils;
import com.lmy.core.dao.FsMasterInfoDao;
import com.lmy.core.dao.FsUsrDao;
import com.lmy.core.model.FsMasterInfo;
import com.lmy.core.model.FsUsr;

public class TestFsMasterInfoDao	extends BaseTestCase {

	
	@SpringBean("fsMasterInfoDao")
	FsMasterInfoDao fsMasterInfoDao;
	
	
	@SpringBean("fsUsrDao")
	FsUsrDao fsUsrDao;
	
	public void test_insert(){
		
		FsUsr usr = fsUsrDao.findByWxOpenIdOrId("test_weixin_openid_30001", null);
		FsMasterInfo bean = new FsMasterInfo();
		bean.setAchievement("取得主要成就");
		bean.setAuditStatus("approved");
		bean.setAuditTime( new Date());
		bean.setAuditWord("审批词ddddd");
		bean.setBirthDate("0112");
		bean.setBirthYear(1986);
		bean.setCertType("0");
		bean.setCertNo("362329198601120614");
		bean.setContactMobile("18221360028");
		bean.setContactQq("342876036");
		bean.setContactWeixin("18221360028");
		bean.setExperience("相关学历描述1212112");
		bean.setGoodAt("擅长领域 1212121212jfjfklfjk");
		bean.setIsFullTime("Y");
		bean.setIsSignOther("Y");
		bean.setLiveAddress("上海张江");
		bean.setName("张三");
		bean.setProfession("现职业");
		bean.setSchool("所学门派");
		bean.setServiceStatus("ING");
		bean.setSex("M");
		bean.setUsrId(usr.getId());
		bean.setWorkDate(CommonUtils.stringToDate("2015-01-01", CommonUtils.dateFormat1));
		bean.setWxOpenId(usr.getWxOpenId());
		bean.setUpdateTime(new Date()).setCreateTime( new Date());
		fsMasterInfoDao.insert(bean);
		
	}
	
}
