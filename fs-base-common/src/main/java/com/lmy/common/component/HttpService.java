package com.lmy.common.component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.log4j.Logger;
/**
 * 基于 commons-httpclient-3.1
 * @author fidel.zhou
 */
public class HttpService {
	private static Logger logger = Logger.getLogger(HttpService.class);
	private HttpClient httpClient;
	private MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager;
	public HttpService() {
		//logger.info("\n HttpServiceImpl init");
		multiThreadedHttpConnectionManager = new MultiThreadedHttpConnectionManager();
		//连接超时
		multiThreadedHttpConnectionManager.getParams().setConnectionTimeout(10 * 1000);
		// 数据等待超时
		multiThreadedHttpConnectionManager.getParams().setSoTimeout(5*60*1000);
		// super.getParams().setStaleCheckingEnabled(true);
		// 最大连接数 ##每个主机的最大并行链接数 默认是2
		multiThreadedHttpConnectionManager.getParams().setDefaultMaxConnectionsPerHost(10);
		// 最大活动连接数，##客户端总并行链接最大数，默认为20
		multiThreadedHttpConnectionManager.getParams().setMaxTotalConnections(20);
		httpClient = new HttpClient(multiThreadedHttpConnectionManager);
	}
	public String doGet(String url,String ...responseEncode) throws IOException {
		logger.debug(url);
		StringBuffer responseBody = new StringBuffer();
		InputStream in = null;
		GetMethod getMethod = null;
		try {
			 String charset = (responseEncode!=null && responseEncode.length==1) ? responseEncode[0]:"UTF-8";
			getMethod = new GetMethod(url);
			getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			// 执行getMethod
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != HttpStatus.SC_OK) {
				logger.error("Method failed: " + getMethod.getStatusLine());
				logger.error("HttpServiceImpl#doGet(String url)"
						+ "  Please check your provided http address! 发生致命的异常，可能是协议不对或者返回的内容有问题");
				throw new IOException(getMethod.getStatusLine().toString());
			}
			in = getMethod.getResponseBodyAsStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			byte [] _b = new byte[1024*10];
			int length = 0;
			while((length = bin.read(_b))>0){
				responseBody.append(new String(_b,0,length,charset));
			}
		} finally {
			// 释放连接
			try {
				if(getMethod!=null){
					getMethod.releaseConnection();
				}
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return responseBody.toString();
	}
	
	public String doPost(String url,Map<String, String> map,String ...responseEncode) throws IOException {
		PostMethod postMethod = null;
		InputStream in = null;
		StringBuffer responseBody = new StringBuffer();
		try{
			 String charset = (responseEncode!=null && responseEncode.length==1) ? responseEncode[0]:"UTF-8";
			 postMethod = new PostMethod(url);
			 postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			 if(map!=null && map.size()>0){
					NameValuePair[] data = new NameValuePair[map.size()];
			        @SuppressWarnings("rawtypes")
					Iterator it = map.entrySet().iterator();
			        int i = 0;
			        while (it.hasNext()) {
			            @SuppressWarnings("rawtypes")
						Map.Entry entry = (Map.Entry) it.next();
			            Object key = entry.getKey();
			            Object value = entry.getValue();
			            if(value!=null){
			            	data[i] = new NameValuePair(key.toString(), value.toString());
			            }else{
			            	data[i] = new NameValuePair(key.toString(), "");
			            }
			            i++;
			        }
			        // 将表单的 值放入postMethod中
			        postMethod.setRequestBody(data);				 
			 }
	     // 执行postMethod
            int statusCode = httpClient.executeMethod(postMethod);// httpclient对于要求接受后继服务的请求，等待返回
            // 象post和put等不能自动处理转发
            // 301或者302
            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                // 从 头中取出转向的地址
                Header locationHeader = postMethod .getResponseHeader("location");
                String location = null;
                if (locationHeader != null) {
                    location = locationHeader.getValue();
                    System.out .println("The page was redirected to:" + location);
                } else {
                    System.out.println("Location field value is null");
                }
            }
			in = postMethod.getResponseBodyAsStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			byte [] _b = new byte[1024*10];
			int length = 0;
			while((length = bin.read(_b))>0){
				responseBody.append(new String(_b,0,length,charset));
			}
		}catch(Exception e){
			logger.error("url="+url+",map="+map,e);
			throw e;
		}finally{
			// 释放连接
			try {
				if(postMethod!=null){
					postMethod.releaseConnection();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return responseBody.toString();
	}
	
	
	/**
	 * use StringRequestEntity(data,null,null)
	 * @param url
	 * @param data
	 * @param useLocalSockey  javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
	 *   HTTPS 本地配置的证书 需要设置为 true ，default is false
	 * @param responseEncode
	 * @return
	 * @throws IOException
	 */
	public String doPostRequestEntity(String url,String data, Boolean useLocalSockey, String ...responseEncode) throws IOException {
		PostMethod postMethod = null;
		StringBuffer responseBody = new StringBuffer();
		if(useLocalSockey==null){
			useLocalSockey = false;
		}
		try{
			logger.debug("url="+url+",data="+data);
			InputStream in = null;
			String charset = (responseEncode!=null && responseEncode.length==1) ? responseEncode[0]:"UTF-8";
			postMethod = new PostMethod(url);
			postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, charset);
			RequestEntity re = new StringRequestEntity(data,null,null);
	        postMethod.setRequestEntity(re);
	        if(useLocalSockey){
		        Protocol https = new Protocol("https", new HTTPSSecureProtocolSocketFactory(), 443);
		        Protocol.registerProtocol("https", https);	        	
	        }
	     // 执行postMethod
            int statusCode = httpClient.executeMethod(postMethod);// httpclient对于要求接受后继服务的请求，等待返回
            // 象post和put等不能自动处理转发
            // 301或者302
            if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                // 从 头中取出转向的地址
                Header locationHeader = postMethod .getResponseHeader("location");
                String location = null;
                if (locationHeader != null) {
                    location = locationHeader.getValue();
                    logger.debug("The page was redirected to:" + location);
                } else {
                	logger.debug("Location field value is null");
                }
            }
			in = postMethod.getResponseBodyAsStream();
			BufferedInputStream bin = new BufferedInputStream(in);
			byte [] _b = new byte[1024*10];
			int length = 0;
			while((length = bin.read(_b))>0){
				responseBody.append(new String(_b,0,length,charset));
			}
		}catch(Exception e){
			logger.error("url="+url+",data="+data,e);
			throw e;
		}finally{
			// 释放连接
			try {
				if(postMethod!=null){
					postMethod.releaseConnection();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		  if(useLocalSockey){
				Protocol.unregisterProtocol("https");			  
		  }
		return responseBody.toString();
	}
}
