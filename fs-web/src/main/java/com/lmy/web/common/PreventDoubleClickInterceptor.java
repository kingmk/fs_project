package com.lmy.web.common;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.lmy.common.annotation.PreventDoubleClickAnnotation;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;
import com.lmy.core.model.dto.LoginCookieDto;
/**
 * 防双(暴)击,重复提交 拦截器
 * @author fidel
 * @since 2017/06/02 23:22
 */
public class PreventDoubleClickInterceptor extends HandlerInterceptorAdapter {
	private static Logger logger = Logger.getLogger(PreventDoubleClickInterceptor.class);
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		if(needPreventDoubleClick(request, handler)){
			boolean hadGetlock = getLock(request,getLockKey(request), 5);
			return hadGetlock;
		}
		return true;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex)throws Exception {
		if(needPreventDoubleClick(request, handler)){
			this.delLock(request);
		}
	}
	
	private boolean needPreventDoubleClick(HttpServletRequest request,Object handler){
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			PreventDoubleClickAnnotation preDclick = hm.getMethodAnnotation(PreventDoubleClickAnnotation.class);
			if (preDclick != null) {
				LoginCookieDto lcd = WebUtil.getLoginDto(request);
				if (lcd.getUserId() != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String getLockKey(HttpServletRequest request){
		return   CacheConstant.WEBCONTROLLER_PREVENT_DOUBLECLICK+"_"+ request.getRequestURI()+"#"+WebUtil.getLoginDto(request).getUserId();
	}
	private void delLock(HttpServletRequest request){
		RedisClient.delete( getLockKey(request) );
	}
	 private boolean getLock(HttpServletRequest request,String key , int timeoutSec){
		 redis.clients.jedis.Jedis jedis = null;
		 redis.clients.jedis.Transaction trans = null;
		 try{
			 Assert.isTrue(  StringUtils.isNotEmpty( key));
			 jedis = RedisClient.getJedis();
			boolean keyExists = jedis.exists(key) ;
			if( ! keyExists  ){
					jedis.watch(key);// make sure below operation is particle
					trans = jedis.multi();
					trans.set(key, key+"_"+ CommonUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss:SSS", ""));
					trans.expire(key, timeoutSec);
					List<Object> list = trans.exec();
					trans = null;
					Assert.isTrue( list !=null);
					return true;
			}else{
				 	logger.warn("双(暴)击,重复提交 本次需要拦住,key="+key+",value="+jedis.get(key)+",timeoutSec="+timeoutSec);
					return false;
			}
		 }catch(Exception e){
			 logger.error("key="+key+",timeoutSec="+timeoutSec,e);
			 return false;
		 }finally{
			 RedisClient.closeJedis(jedis);
		 }
	 }
	
}
