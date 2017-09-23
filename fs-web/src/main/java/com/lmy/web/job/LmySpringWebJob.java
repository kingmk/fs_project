package com.lmy.web.job;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmy.core.service.impl.OrderSettlementServiceImpl;

/**
 * 所有的job 已经添加了 分布式处理
 * @author fidel
 * @since 2017/05/31 16:00
 */
public class LmySpringWebJob {
	private static final Logger logger  = LoggerFactory.getLogger(LmySpringWebJob.class);
	@Autowired
	private OrderSettlementServiceImpl orderSettlementServiceImpl;
	//就每个月7号晚上22点结算上个月的全部订单
	 @Scheduled(cron="0 0 22 */7 * ?")
	public void lmyAutoSettle(){
		if(!com.lmy.core.utils.FsEnvUtil.isPro() ){
			logger.info("非生产不执行"+ this.getClass()+"#"+Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		 orderSettlementServiceImpl.autoSettlement(null);
	 }
	 
	 /**
	  * @since 2017/06/05 19:03 防止 部分订单到点没有执行 (pay_succ|refund_fail)) -->completed , 这里进行补救一次
	  */
	 @Scheduled(fixedRate = 3600*8 * 1000)
	 public void autoPaySuccToCompleted(){
			 orderSettlementServiceImpl.autoPaySuccToCompleted(new Date());
		 }
}
