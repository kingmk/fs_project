package test.com.lmy.core.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.unitils.spring.annotation.SpringBean;

import test.com.lmy.core.base.BaseTestCase;

import com.lmy.core.dao.FsPeriodStatisticsDao;
import com.lmy.core.model.FsPeriodStatistics;

public class TestFsPeriodStatistics extends BaseTestCase {
	@SpringBean("fsPeriodStatisticsDao")
	FsPeriodStatisticsDao fsPeriodStatisticsDao;
	
	public void test_batchinsert(){
		
		List<FsPeriodStatistics> beanList = new ArrayList<>();
		
		for (int i = 0; i < 3; i++) {
			FsPeriodStatistics bean = new FsPeriodStatistics();
			bean.setPeriodName("Testing");
			bean.setSellerUsrId(Long.valueOf(i));
			bean.setCountBuyer(Long.valueOf((i+1)*30));
			bean.setCountOrder(Long.valueOf((i+1)*45));
			bean.setAvgRespTime(Long.valueOf((i+1)*25));
			bean.setStartTime(new Date());
			bean.setEndTime(new Date());
			bean.setCreateTime(new Date());
			beanList.add(bean);
		}
		
		fsPeriodStatisticsDao.batchInsert(beanList);
	}
}
