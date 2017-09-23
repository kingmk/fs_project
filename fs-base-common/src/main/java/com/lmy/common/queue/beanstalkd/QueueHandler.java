package com.lmy.common.queue.beanstalkd;
import com.alibaba.fastjson.JSONObject;

public abstract class QueueHandler{
	/**
	 * Producer命令,this method never throw exception
	 * @param priority 优先级,可选,默认1024
	 * @param delaySeconds 推迟时间,可选,默认0秒
	 * @param timeToRun ，表示允许一个worker执行该job的秒数。这个时间将从一个worker 获取一个job开始计算。	如果该worker没能在<timeToRun> 秒内删除、释放或休眠该job，
	 * 										这个job就会超时，服务端会主动释放该job。最小ttr为1。如果客户端设置了0，服务端会默认将其增加到1。默认60秒
	 * @param jsonFormatStr 没有定义成 JSONObject 怕 升级json jar 包 导致 无法解析
	 * @return 产生的jobid;-1系统错误;
	 */
	public  Long put(Long priority, Integer delaySeconds, Integer timeToRun, JSONObject data){
		if(priority == null || priority<1){
			priority = 1024l;
		}
		if(delaySeconds == null || delaySeconds<0){
			delaySeconds = 0;
		}
		if(timeToRun == null || timeToRun<1){
			timeToRun = 60;
		}
		return BeanstalkClient.put(getQueueName(), priority, delaySeconds, timeToRun, data);
	}
	
	
	public abstract String getQueueName();
	
	/**
	 * 程序错误尽量不要往外抛
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public abstract Object hander(JSONObject data) throws Exception;
	
}
