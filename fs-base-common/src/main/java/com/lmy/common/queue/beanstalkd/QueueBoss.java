package com.lmy.common.queue.beanstalkd;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.redis.ByteToObjectUtils;
import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClient.Job;
public class QueueBoss implements Runnable{
	private static Logger logger  = LoggerFactory.getLogger(QueueBoss.class);
	/**
	 * 等待超时时间 单位秒
	 */
	private Integer timeout;
	/**
	 * 消息处理者
	 */
	private QueueHandler handler ;
	
	/**
	 * 跳出循环判断,暂时没有用到 
	 */
	private boolean runAble  = false;
	/**
	 * @param timeout 获取一个元素的等待(阻塞)时间 单位秒 default 10,先进先出
	 * @param key   队里名称
	 * @param handler  队里元素,消费者。
	 */
	public QueueBoss(Integer timeout , QueueHandler handler){
		if(timeout==null || timeout<10){
			timeout = 10;
		}
		this.timeout = timeout;
		this.handler = handler;
	}
	@Override
	public void run() {
		if(runAble){
			logger.warn("key="+handler.getQueueName()+",仍然在运行状态runAble="+runAble+",本次启动失败...");
			return;
		}
		try{
			logger.info("begin key="+handler.getQueueName()+",handler is "+this.handler);
			if(this.handler==null){
				logger.warn("key="+handler.getQueueName()+",handler is null");
				runAble = false;
			}
			this.setRunAble(true);
			while(this.isRunAble()){
				logger.debug("key="+handler.getQueueName()+",开始读队列");
				Client client = null;
				Job job = null;
				try{
					client = getFitClient(handler.getQueueName(),0);
					client.watch(handler.getQueueName());
					job = client.reserve(timeout);
					Object obj = null;
					if(job!=null){
						logger.debug("job.getJobId()="+job.getJobId());
						obj = ByteToObjectUtils.ByteToObject(job.getData());
						client.delete(job.getJobId());
					}
					this.handler(handler.getQueueName(), (JSONObject)obj);
				}
				catch(com.surftools.BeanstalkClient.BeanstalkException e){
					logger.error(handler.getQueueName(),e);
					clientMap.remove(handler.getQueueName());
					getFitClient(handler.getQueueName(), 0);
				}
				catch(Exception e){
					logger.error("key="+handler.getQueueName()+",encounter one unknown error , Program will exit",e);
					logger.error("key="+handler.getQueueName()+",~~~~~~~~ exit~~~~~~~~~~~~~~~");
					runAble = false;
				}
			} // while end
			logger.info("key="+handler.getQueueName()+",~~~~~~~~ exit~~~~~~~~~~~~~~~");
		}finally {
			this.setRunAble(false);
			Collection<Client>  clients = clientMap.values();
			if(clients!=null && clients.size()>0){
				for(Client _client :  clients){
					_client.close();
				}
			}
			clientMap.clear();
		}
	}
	
	
	
	private static Map<String,Client> clientMap = new HashMap<String,Client>();
	private static Client getFitClient(String _tube  , int timeout){
		try{
			Client client = clientMap.get(_tube);
			if(client==null){
				try{
					client =BeanstalkClient.getClient();
					clientMap.put(_tube, client);					
				}catch(Exception e){
					if(timeout>0){
						logger.warn(timeout+" 秒后重新获取连接失败");
					}
					java.util.concurrent.TimeUnit.SECONDS.sleep(timeout>0?timeout:10);
					logger.error("_tube="+_tube+",timeout="+timeout,e);
					getFitClient(_tube, timeout>0?timeout:10);
				}
			}
			return client;			
		}catch(Exception e){
			logger.error("",e);
			return null;
		}
	}
	
	/**
	 * 不会往外抛异常 
	 * @param key
	 * @param obj
	 */
	private void handler(String key ,JSONObject obj){
		try{
			logger.debug("key="+key+",message obj="+obj);
			if(obj !=null){
				try {
					this.handler.hander(obj);
				} catch (Exception e) {
					logger.error("key="+key+",obj="+obj+",hand encouter one error",e);
				}
			}			
		}catch(Exception e){
			logger.error("key="+key+",obj="+obj,e);
		}
	}
	
	public Integer getTimeout() {
		return timeout;
	}
	public QueueBoss setTimeout(Integer timeout) {
		this.timeout = timeout;
		return this;
	}
	public QueueHandler getHandler() {
		return handler;
	}
	public QueueBoss setHandler(QueueHandler handler) {
		this.handler = handler;
		return this;
	}
	public boolean isRunAble() {
		return runAble;
	}
	public QueueBoss setRunAble(boolean runAble) {
		this.runAble = runAble;
		return this;
	}
	
	public static void main(String[] args) {
		
	}
}
