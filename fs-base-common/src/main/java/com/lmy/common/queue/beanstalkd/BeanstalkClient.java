package com.lmy.common.queue.beanstalkd;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.lmy.common.redis.ByteToObjectUtils;
import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClient.Job;
import com.surftools.BeanstalkClientImpl.ClientImpl;
public class BeanstalkClient {
	private static Logger logger = Logger.getLogger(BeanstalkClient.class);
	private static String HOST= "beanstalk-server";
	private static int PORT=11300;
	/**
	 * Producer命令,this method never throw exception
	 * @param tubeName 管(通)道名,可选
	 * @param priority 优先级,可选,默认1024
	 * @param delaySeconds 推迟时间,可选,默认0秒
	 * @param timeToRun ，表示允许一个worker执行该job的秒数。这个时间将从一个worker 获取一个job开始计算。	如果该worker没能在<timeToRun> 秒内删除、释放或休眠该job，
	 * 										这个job就会超时，服务端会主动释放该job。最小ttr为1。如果客户端设置了0，服务端会默认将其增加到1。默认60秒
	 * @param obj
	 * @return 产生的jobid;-1系统错误;
	 */
	public static synchronized Long put(String tubeName,Long priority, Integer delaySeconds, Integer timeToRun, Object obj){
		Client client = null;
		try{
			client = getClient();
			byte [] _obj = ByteToObjectUtils.ObjectToByte(obj);
			if(priority==null || priority<1){
				priority = 1024l;
			}
			if(delaySeconds==null || delaySeconds<0){
				delaySeconds = 0;
			}
			if(timeToRun==null || timeToRun<0){
				timeToRun = 60;
			}
			if(!StringUtils.isEmpty(tubeName)){
				logger.debug("used tubeName is'"+tubeName +"'");
				client.useTube(tubeName);
			}
			long jobId = client.put(priority, delaySeconds, timeToRun, _obj);
			logger.debug("入queue  tubeName is "+tubeName+",obj="+obj+",and jobId="+jobId);
			return jobId;
		}
		catch(Exception e){
			logger.error("HOST="+HOST+",PORT="+PORT+"tubeName="+tubeName+",delaySeconds="+delaySeconds+",obj="+obj,e);
			return -1l;
		}finally{
			close(client);
		}
	}
	
	/**
	 * Worker命令
	 * @param client
	 * @param tubeName  管(通)道名,可选
	 * @param timeoutSeconds 超时时间，可选，默认60秒
	 * @return
	 */
	public static synchronized Job reserve(Client client ,String tubeName,Integer timeoutSeconds){
		if(!StringUtils.isEmpty(tubeName)){
			client.watch(tubeName);
		}
		if(timeoutSeconds==null){
			timeoutSeconds = 60;
		}
		return client.reserve(timeoutSeconds);
	}
	
	public static synchronized boolean delete(Client client,Long jobId){
		try{
			if(client==null){
				logger.warn("BeanstalkClient#delete(...) client is null and will construction a new client");;
				client = getClient();	
			}
			client.delete(jobId);
			return true;
		}catch(Exception e ){
			logger.error("HOST="+HOST+",PORT="+PORT+"BeanstalkClient#delete(...) delete failure , and method will return false!",e);
			return false;
		}finally{
			close(client);
		}
	}
	/**
	 * 
	 * @param client
	 * @param jobId
	 * @param priority 默认 1024
	 *  @param delaySeconds 默认 0
	 */
	public static  synchronized boolean release(Client client,long jobId,Long priority,Integer delaySeconds ){
		try{
			if(priority==null ||priority<0){
				priority = 1024l;
			}
			if(delaySeconds== null || delaySeconds<0){
				delaySeconds = 0;
			}
			return client.release(jobId, priority, delaySeconds);
		}catch(Exception e){
			logger.error("HOST="+HOST+",PORT="+PORT+"BeanstalkClient#release(...) release failure , and method will return false!",e);
			return false;
		}
	}
	
	
	
	
	public static List<String >pushWatchedTubes(Client client) {
		if(client==null){
			logger.warn("BeanstalkClient#delete(...) client is null and will construction a new client");;
			client = getClient();	
		}
		List<String> list = client.listTubesWatched();
		return list;
	}
	
	
	public static synchronized void close(Client client){
		try{
			if(client!=null){
				client.close();
			}
		}catch(Exception e){
			logger.warn("HOST="+HOST+",PORT="+PORT+"BeanstalkClient#close(...) when close had encounted a error",e);
		}
	}
	
	public static Client getClient(){
		Client client = null;
		try{
			client =     new ClientImpl(HOST, PORT);
		}catch(com.surftools.BeanstalkClient.BeanstalkException e){
			logger.error("getClient encounter error HOST="+HOST+",PORT="+PORT ,e);
			if("Connection timed out: connect".equals(e.getMessage()) || "Connection refused".equals(e.getMessage())){
				try{
					restart();
					java.util.concurrent.TimeUnit.MILLISECONDS.sleep(500);
				}catch(Exception e2){
					logger.error("尝试启动 beanstalk 失败",e2);
				}
			}
		}
		catch(Exception e){
			logger.error("HOST="+HOST+",PORT="+PORT+"getClient encounter error" ,e);
		}
		if(client==null){
			client =  new ClientImpl(HOST, PORT);
		}
		return client;
	}
	/**
	 * 尝试重启,不保证一定成功
	 */
	public static  void restart(){
		try{
			logger.warn("尝试启动 beanstalk begin... 不保证成功");
			Runtime rt = Runtime.getRuntime();
			rt.exec("/opt/beanstalkd/beanstalkd-start.sh");
			logger.warn("尝试启动 beanstalk end... 不保证成功");
			//java.util.concurrent.TimeUnit.SECONDS.sleep(1);
		}catch(Exception e2){
			logger.error("HOST="+HOST+",PORT="+PORT+"尝试启动 beanstalk 失败",e2);
		}
	}
	
	public static void main(String[] args) {
		put("test1", null, null, null, "hello");
		put("test2", null, null, null, "hello2");
	}
	
}
