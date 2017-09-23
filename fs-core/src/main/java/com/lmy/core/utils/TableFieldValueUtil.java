package com.lmy.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class TableFieldValueUtil {
	private static Logger logger = Logger.getLogger(TableFieldValueUtil.class);
	private static Map<String, String> props = new HashMap<String,String>();
	static {
		String name="biz/persistence/youcai/table/tableFieldValue.properties";
		try {
			InputStream is = TableFieldValueUtil.class.getClassLoader().getResourceAsStream(name);
			Properties pro = new Properties();
			pro.load(is);
			Enumeration<?> e = pro.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = pro.getProperty(key);
				props.put(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Map<String, String> getLike(String keyLike) {
		
		Map<String, String> result=new HashMap<String,String>();
		for (String key: props.keySet()) {
			if (key.indexOf(keyLike)!=-1) {
				result.put(key, props.get(key));
			}
		}
		return result;
	}
	public static String get(String key) {
		return get(key,"");
	}
	public static String get(String key,String defaultValue) {
		String value = props.get(key);
		if (StringUtils.isBlank(value)) {
			logger.warn("key="+key+"  value is empty");
			value=defaultValue;
		}
		return value;
	}
	public static void main(String[] args) {
		System.out.println( get("ddd") );
		Map<String, String> map = getLike("cd_account_histroya2.action");
		for (String key : map.keySet()) {
			System.out.println(key+":"+map.get(key));
		}
	}
	
}
