package test.com.lmy.core.dao;

import java.util.Date;

import org.unitils.spring.annotation.SpringBean;

import test.com.lmy.core.base.BaseTestCase;

import com.lmy.core.dao.FsMasterCardDao;
import com.lmy.core.model.FsMasterCard;

public class TestFsMasterCardDao extends BaseTestCase {
	@SpringBean("fsMasterCardDao")
	FsMasterCardDao fsMasterCardDao;

	public void test_insert(){
		FsMasterCard bean = new FsMasterCard();
		bean.setMasterUsrId(10000L);
		bean.setHolderName("test");
		bean.setBankName("cmb");
		bean.setBankNo("23999102993344");
		bean.setProvince("Shanghai");
		bean.setCity("Shanghai");
		bean.setCreateTime(new Date());
		fsMasterCardDao.insert(bean);
	}

}
