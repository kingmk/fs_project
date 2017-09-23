package com.lmy.web.common;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.lmy.common.annotation.ExcludeSpringInterceptor;
import com.lmy.core.model.dto.LoginCookieDto;
import com.lmy.core.service.impl.FsWeiXinUrlServiceImpl;
import com.lmy.core.utils.FsEnvUtil;
/**
 * 如果没有微信openId，会在一次重定向到微信网关， 再一次获取openId,注意 死循环问题 
 * @author Administrator
 */
public class OpenIdInterceptor extends HandlerInterceptorAdapter {
	private static Logger logger = Logger.getLogger(OpenIdInterceptor.class);
	 /**
	  * 判断是否不对该请求进行处理
	  * @param hm
	  * @return
	  */
	 private boolean exclude(HandlerMethod hm){
		 ExcludeSpringInterceptor excludeSpringInterceptor = hm.getMethodAnnotation(ExcludeSpringInterceptor.class);
		 if(excludeSpringInterceptor==null ||  ( excludeSpringInterceptor.excludeClass()==null || excludeSpringInterceptor.excludeClass().length==0 )){
			 return false;
		 }else{
			 for(Class _excludeClass : excludeSpringInterceptor.excludeClass()){
				 if( this.getClass() .equals( _excludeClass)){
					 return true;
				 }
			 }
		 }
		 return false;
	 }
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		if(handler instanceof  HandlerMethod){
			if(!FsEnvUtil.isPro()){
				logger.info(request.getRequestURI()+" ,请求参数:"+WebUtil.getRequestParams(request));
			}
			HandlerMethod hm = (HandlerMethod)handler;	
			boolean needExclude = exclude(hm);
			if(needExclude){
				//logger.debug("needExclude="+needExclude+",hm="+hm);
				return needExclude;
			}
			LoginCookieDto lcd = 	WebUtil.getLoginDto(request);
			//TODO 强验证 2017/05/22
/*			if(YcEnvUtil.isDev()   ){
				return true;
			}*/
			if(   (StringUtils.isEmpty( lcd.getOpenId() )  && LoginCookieDto.USER_CHANNEL_5.equals( lcd.getEnterType()) ) ||  SessionUtil.getFsUsr(request)==null){
				//String url = request.getRequestURI();
/*				if(YcEnvUtil.isDev()){
					if(StringUtils.isNotEmpty(url) &&  url.indexOf("wx1b99f22989f7b28d_dev")>-1){
						url = url.replaceFirst("wx1b99f22989f7b28d_dev/", "");
					}
				}*/
				String lastUrl =  FsWeiXinUrlServiceImpl.createMenuClickUrl(WebUtil.getBackUrl(request), false);
				//logger.warn("url="+url+",begin 重定向到微信再一次获得opendId过程 lastUrl=" + lastUrl);
				response.sendRedirect(lastUrl);
				return false;
			}
		}
		return true;
	}
	
}
