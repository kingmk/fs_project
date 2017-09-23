package com.lmy.common.utils;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    /**
     * 添加cookie
     * @param response
     * @param name cookie的名称
     * @param value cookie的值
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {        
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (maxAge>0) cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
    
	/**
	 * 删除cookie
	 * 
	 * @param cookie
	 * @param response
	 *            - response
	 */
	public static void removeCookie(Cookie cookie, HttpServletResponse response) {
		if(cookie!=null && response!=null){
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}
	}
	
    /**
     * 获取cookie的值
     * @param request
     * @param name cookie的名称
     * @return
     */
    public static String getCookieValueByName(HttpServletRequest request, String name) {
     Map<String, Cookie> cookieMap = CookieUtil.readCookieMap(request);
        if(cookieMap!=null && cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie!=null?cookie.getValue():null;
        }else{
            return null;
        }
    }
    
    /**
     * 获取cookie
     * @param request
     * @param name cookie的名称
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
     Map<String, Cookie> cookieMap = CookieUtil.readCookieMap(request);
        if(cookieMap!=null && cookieMap.containsKey(name)){
            Cookie cookie = (Cookie)cookieMap.get(name);
            return cookie!=null?cookie:null;
        }else{
            return null;
        }
    }
    
    
    protected static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (int i = 0; i < cookies.length; i++) {
            	if(cookies[i].getMaxAge()!=0){
            		 cookieMap.put(cookies[i].getName(), cookies[i]);	
            	}
            }
        }
        return cookieMap;
    }

}
