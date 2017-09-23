package com.lmy.common.utils;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;


public class ResourceUtils {
	public static final String LMYCORE = "lmy-core";
	private static Logger logger  = Logger.getLogger(ResourceUtils.class);
	private static Map<String,ResourceBundle> bundleMap= new HashMap<String,ResourceBundle>();
	
	/**
	 * 
	 * @param domain 配置文件名前缀
	 * @param key
	 * @return
	 */
	public static String getValue(String domain, String key,Object ...params) {
		ResourceBundle bundle = null;
		if(bundleMap.get(domain)!=null){
			bundle  = bundleMap.get(domain);
		}else{
			bundle  = ResourceBundle.getBundle(domain);
			bundleMap.put(domain, bundle);
		}
		try {
			String str = bundle.getString(key);
			String newStr = new String(str.getBytes("ISO-8859-1"),"utf-8");
			if(params!=null && params.length==0){
				return newStr;
			}else{
				return  MessageFormat.format( newStr, params);
			}
		} catch(Exception e){
			logger.warn("Can't find resource for bundle java.util.PropertyResourceBundle, key '"+key+"' , and method will return null");
			return null;
		}
	}
	
	/**
	 * @param domain 配置文件名前缀
	 * @param key
	 * @return
	 *//*
	public static String getValue(String key,Object ...params) {
		if(allPro==null || allPro.size() ==0){
			initAllPro(null);
		}
		try {
			String str = allPro.getProperty(key);
			String newStr = (StringUtils.isEmpty(str) ? str: new String(str.getBytes("ISO-8859-1"),"utf-8") );
			if(  params!=null && params.length==0){
				return newStr;
			}else{
				return  MessageFormat.format( newStr, params);
			}
		} catch (IOException e) {
			logger.error("key="+key+",params="+params, e);
		}
		return null;
	}*/
	
/*	*//**
	 * @param pattern default is classpath*:*.properties
	 *//*
	static void initAllPro(String pattern){
		if(StringUtils.isEmpty(pattern)){
			pattern = "classpath*:*.properties";
		}
		PathMatchingResourcePatternResolver prp = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try {
			resources = prp.getResources(pattern);
			logger.info("resources.length="+(resources!=null ? resources.length:0)+"|resources="+resources);
			if(resources!=null && resources.length>0){
				for(Resource resource : resources){
					PropertiesLoaderUtils.fillProperties(allPro, resource);
				}
			}
		} catch (IOException e) {
			logger.error("pattern="+pattern,e);
		}
	}*/
}
