package com.lmy.web.common;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.model.base.BaseObject;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.model.FsUsr;

public class SessionUtil {
	public static void mainSession(HttpServletRequest request , String key , Object value){
		String cacheKey = getUserInfoKey(request);
		JSONObject cacheValue = (JSONObject)RedisClient.get(cacheKey);
		if(cacheValue == null){
			cacheValue = new JSONObject();
		}
		if(value instanceof BaseObject){
			cacheValue.put(key,  value.toString());
		}else{
			cacheValue.put(key,  value);
		}
		RedisClient.set(cacheKey, cacheValue, 60 * 60  * 2) ;  //
	}
	public static FsUsr getFsUsr(HttpServletRequest request ){
		String cacheKey = getUserInfoKey(request);
		JSONObject cacheValue = (JSONObject)RedisClient.get(cacheKey);
		if(  cacheValue != null ){
			//TODO 
			// mainSession(request, cacheKey, cacheValue);
		}
		return  cacheValue != null ? JSON.parseObject(cacheValue.getString("usr"), FsUsr.class)   :null;
	}	
	
	
	private static String getUserInfoKey(HttpServletRequest request){
		String key = CacheConstant.FS_LOGIN+"_"+WebUtil.getLoginDto(request).wxOpenId();
		//logger.info("LSZH_LOGIN key:"+key);
		return key;
	}
	
	
	
}
