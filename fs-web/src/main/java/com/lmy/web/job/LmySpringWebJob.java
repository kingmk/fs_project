package com.lmy.web.job;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.model.FsPeriodStatistics;
import com.lmy.core.service.impl.AdminAuditServiceImpl;
import com.lmy.core.service.impl.MasterStatisticsServiceImpl;
import com.lmy.core.service.impl.OrderSettlementServiceImpl;
import com.lmy.core.service.impl.StatisticsServiceImpl;

/**
 * 所有的job 已经添加了 分布式处理
 * @author fidel
 * @since 2017/05/31 16:00
 */
public class LmySpringWebJob {
	private static final Logger logger  = LoggerFactory.getLogger(LmySpringWebJob.class);
	@Autowired
	private OrderSettlementServiceImpl orderSettlementServiceImpl;
	@Autowired
	private MasterStatisticsServiceImpl masterStatisticsServiceImpl;
	@Autowired
	private AdminAuditServiceImpl adminAuditServiceImpl;
	@Autowired
	private StatisticsServiceImpl statisticsServiceImpl;
	//就每月7日晚上22点结算上个月的全部订单
	@Scheduled(cron="0 0 22 */7 * ?")
	public void lmyAutoSettle(){
		if(!com.lmy.core.utils.FsEnvUtil.isPro() ){
			logger.info("非生产不执行"+ this.getClass()+"#"+Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		orderSettlementServiceImpl.autoSettlement(null);
	}
	
	//就每月1日凌晨4点生成上月统计数据
	@Scheduled(cron="0 0 4 1 * ?")
	public void lmyAutoStatistics(){
		if(!com.lmy.core.utils.FsEnvUtil.isPro() ){
			logger.info("非生产不执行"+ this.getClass()+"#"+Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		logger.info("========== auto monthly statistics ==========");
		long secsRand = (long)(Math.random()*10000);
		try {
			Thread.sleep(secsRand);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String cacheKey = CacheConstant.AUTO_JOB+"_master_monthly_statistics";
		if (!RedisClient.exists(cacheKey) ) {
			RedisClient.set(cacheKey, Boolean.TRUE, 3600);
			logger.info("=====start calcuate master monthly statistics=====");
			statisticsServiceImpl.monthlyStatistics();
		} else {
			logger.info("=====other server is calculating master monthly statistics, skip on current server=====");
		}
	}

	@Scheduled(cron="0 0 2 * * ?")
	public void autoCalculateStatistics() {
		logger.info("==== auto calculate statistics ====");
		long secsRand = (long)(Math.random()*10000);
		try {
			Thread.sleep(secsRand);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String cacheKey = CacheConstant.AUTO_JOB+"_calculate_statistics";
		if (!RedisClient.exists(cacheKey) ) {
			RedisClient.set(cacheKey, Boolean.TRUE, 3600);
			logger.info("=====start calcuate statistics=====");
			masterStatisticsServiceImpl.calculateStatistics();
		} else {
			logger.info("=====other server is calculating statistics, skip on current server=====");
		}
	}


	/**
	 * @since 2017/06/05 19:03 防止 部分订单到点没有执行 (pay_succ|refund_fail)) -->completed , 这里进行补救一次
	 */
	@Scheduled(fixedRate = 3600 * 1000)
	public void autoPaySuccToCompleted(){
		logger.info("======auto make the order completed in case that the queue is missing======");
		orderSettlementServiceImpl.autoPaySuccToCompleted(new Date());
	}
	
	@Scheduled(fixedRate = 3600 * 1000)
	public void autoUnforbidMasters() {
		logger.info("======auto unforbid masters in case that the queue is missing======");
		adminAuditServiceImpl.unforbidMasterAuto();
	}
}
