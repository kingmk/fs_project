package com.lmy.common.queue.beanstalkd;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
public class ProtocolCommand {
	private static Logger logger  =Logger.getLogger(ProtocolCommand.class);
	
	public Object exe(AbstractStartJob startJob , Socket socket){
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try{
			InetSocketAddress socketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
			if(!socketAddress.getHostName().equals("127.0.0.1") && !socketAddress.getHostName().equals("localhost")){
				logger.info(socketAddress.getHostName()+"不在白名单 force close socket connection");
				socket.close();
				return null;
			}
			reader   = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			  String readLine = null;
			 while( (readLine = reader.readLine()) !=null){
				 String handlResult = null;
				 readLine = readLine.trim();
				 logger.info("readLine="+readLine);
				 try{
					 if(readLine.startsWith("info")){
						 handlResult =  info(startJob, readLine);
					 }else if(readLine.startsWith("start")){
						 handlResult = start(startJob, readLine);
					 }
					 else if(readLine.startsWith("stop")){
						 handlResult =	 stop(startJob, readLine);
					 }
					 else if(readLine.startsWith("help") || readLine.startsWith("?")){
						 handlResult = help(startJob);
					 }
					 else if(readLine.startsWith("shutdown") || readLine.startsWith("?")){
						 handlResult = shutdown(startJob,readLine);
					 }
					 else{
						 handlResult ="unkonw command '"+readLine+"'";
					 }					 
				 }catch(Exception e){
					 logger.error("readLine="+readLine,e);
					 handlResult =e.getClass().toString()+" "+ e.getMessage();
				 }
				 writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				 writer.write(handlResult);
				 writer.write("\n ");
				 writer.flush();
			 }
			 return null;
		}catch(Exception e){
			logger.error(e);
			return e.getMessage();
		}finally{
			try {
				if(writer!=null){
					writer.close();
				}
				if(reader!=null){
					reader.close();
				}
				socket.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}
	
	private String shutdown(AbstractStartJob startJob , String command){
		 List<QueueBoss> lists = startJob.getQueueBoss();
		if(CollectionUtils.isNotEmpty(lists)){
			for(QueueBoss queue : lists) {
				queue.setRunAble(false);
			} 
		}
		logger.info("command="+command);
		 System.exit(-1);
		return "should downd command send success";
	}
	
	private String info(AbstractStartJob startJob , String command){
		return JSON.toJSONString(startJob.getQueueBoss());
	}
	private String start(AbstractStartJob startJob, String command){
		String str[] = command.split(" ");
		return startJob.start(str[1]);
	}
	private String stop(AbstractStartJob startJob, String command){
		String str[] = command.split(" ");
		return startJob.stop(str[1]);
	}
	private String help(AbstractStartJob startJob){
		StringBuffer sb = new StringBuffer();
		sb.append("start key| ")
		.append("stop key| ")
		.append("info| ")
		.append("help| ")
		.append("?");
		return sb.toString();
	}
}
