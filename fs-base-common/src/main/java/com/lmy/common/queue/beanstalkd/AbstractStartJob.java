package com.lmy.common.queue.beanstalkd;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * eg:<br>
#!/bin/sh
export CLASSPATH=$CLASSPATH:
currpath=/home/fidel/workspace/test/jobtest
for jarz in "$currpath"/lib/*.jar
do CLASSPATH=$CLASSPATH:$jarz
done

tomcat_servlet=/home/fidel/soft/apache-tomcat-jy/
for jarz in "$tomcat_servlet"/lib/*.jar
do CLASSPATH=$CLASSPATH:$jarz
done

echo $CLASSPATH
java -classpath $CLASSPATH com.ebuy.elife.job.ElifeCoreJob
#echo $! > /home/webadmin/script/jy_beanstalk/jy_beanstalk.pid
 */
public abstract class AbstractStartJob {
	private static Logger logger  = LoggerFactory.getLogger(AbstractStartJob.class);
	private int port = 4700;
	private Executor  executor = null;
	private  List<QueueBoss> queueBoss = new ArrayList<QueueBoss>();
	public List<QueueBoss> getQueueBoss() {
		return queueBoss;
	}
	public void setQueueBoss(List<QueueBoss> queueBoss) {
		this.queueBoss = queueBoss;
	}
	
	/**
	 * @param timeout 阻塞时间(下一个 job ready 前遍 历等待/阻塞) 单位秒  ,default 60 
	 * @param handler
	 */
	public void setHandler(Integer timeout ,QueueHandler handler){
		if(timeout==null || timeout<60 ){
			timeout = 60;
		}
		QueueBoss qb = new QueueBoss(timeout, handler);
		this.getQueueBoss().add(qb);
	}
	
	
	private void startAll(){
		if(CollectionUtils.isNotEmpty(getQueueBoss())){
			for(QueueBoss queue : getQueueBoss()){
				executor.execute(queue);
			}
		}
	}
	public String stop(String key){
		logger.info("stop key="+key);
		QueueBoss queueNeedStart = null;
		if(CollectionUtils.isNotEmpty(getQueueBoss())){
			for(QueueBoss queue : getQueueBoss()){
				if(queue.getHandler().getQueueName().equals(key)){
					queueNeedStart = queue;
					break;
				}
			}
		} // end if
		if(queueNeedStart==null){
			return "not found handler instance key="+key;
		}
		if(queueNeedStart!=null){
			if(queueNeedStart.isRunAble()){
				queueNeedStart.setRunAble(false);
				return "stop key'"+key+"'" +" command send success";	
			}
		}
		return "ok";
	}
	
	public String start(String key){
		logger.info("start key="+key);
		QueueBoss queueNeedStart = null;
		if(CollectionUtils.isNotEmpty(getQueueBoss())){
			for(QueueBoss queue : getQueueBoss()){
				if(queue.getHandler().getQueueName().equals(key)){
					queueNeedStart = queue;
					break;
				}
			}
		} // end if
		if(queueNeedStart==null){
			return "not found handler instance key="+key;
		}
		if(queueNeedStart!=null){
			if(queueNeedStart.isRunAble()){
				return "key="+key+",handler instance is runing";	
			}else{
				executor.execute(queueNeedStart);
			}
		}
		return "start key="+key;
	}
	
	private void init(Integer port , List<QueueBoss> queueBoss) throws Exception{
		if(port!=null && port>0){
			logger.info("port="+port);
			this.port = port;
		}
		logger.info("init~~~~~~~~~~~~port="+port);
		int size = 1;
		if(CollectionUtils.isNotEmpty(queueBoss)){
			size = queueBoss.size()+1;
		}else{
			throw new Exception("queueBoss is empty");
		}
		this.executor = Executors.newFixedThreadPool(size);
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public Executor getExecutor() {
		return executor;
	}
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	
	public void begin(String[] args) throws Exception{
		ServerSocket server  = null;
		try{
			if(args!=null && args.length>0){
				try{
					port = Integer.valueOf(args[0]);
				}catch(Exception e){
					logger.error(args[0],e);
				}
			}
			init(port, queueBoss);
			startAll();
			server=new ServerSocket(port);
			Socket socket  = null;
			while( (socket=  server.accept()) != null){
				acceptCommand(socket);
			}			
		}catch(Exception e){
			logger.error("未知错误",e);
		}finally{
			if(server!=null){
				server.close();
			}
		}
	}
	public  void acceptCommand(Socket socket){
		ProtocolCommand pc = new ProtocolCommand();
		pc.exe(this, socket);
	}
	
}	
