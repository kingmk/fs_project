package test.com.lmy.core.service;

import java.util.Date;

import org.unitils.spring.annotation.SpringBean;

import com.lmy.common.component.CommonUtils;
import com.lmy.core.service.impl.OrderSettlementServiceImpl;

import test.com.lmy.core.base.BaseTestCase;

public class TestOrderSettlementServiceImpl extends BaseTestCase {
	
	@SpringBean("orderSettlementServiceImpl")
	OrderSettlementServiceImpl orderSettlementServiceImpl;
	
	public void test_autoSettlement(){
		Date date = CommonUtils.stringToDate("2017-06-06 22:00:00", CommonUtils.dateFormat2);
		orderSettlementServiceImpl.autoSettlement(date);
	}
	
	public void test_settlementOne(){
		Date date = CommonUtils.stringToDate("2017-06-06 22:00:00", CommonUtils.dateFormat2);
		Date settlementCycleBeginTime = orderSettlementServiceImpl.getSettlementCycleBeginTime(date);
		Date settlementCycleEndTime =orderSettlementServiceImpl.getSettlementCycleEndTime(date);
		orderSettlementServiceImpl.settlementOne(100141l, settlementCycleBeginTime, settlementCycleEndTime);
	}
	
	public void test_settlementOneAgain(){
		orderSettlementServiceImpl.settlementOneAgain(100142l, 100026l);
		orderSettlementServiceImpl.settlementOneAgain(100146l, 100027l);
	}
	
}
