package com.lmy.web.common;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.utils.CookieUtil;
import com.lmy.core.model.dto.LoginCookieDto;
public class WebUtil {
	private static Logger logger = Logger.getLogger(WebUtil.class);
	
	public static String LOGIN_PREFIX = "fs_identification" ;
	/**
	 * @param request
	 * @return 不会返回空的对象
	 */
	public static LoginCookieDto getLoginDto(HttpServletRequest request){
		LoginCookieDto  lcd = 	(LoginCookieDto) request.getAttribute("_elife_loginCookie_20151029");
		if(lcd != null){
			return lcd;
		}
		//_elife_loginCookie_20151029 在 LoginInterceptor#getLoginCookieDto 中设置
		String cookieValue   =  CookieUtil.getCookieValueByName(request, LOGIN_PREFIX);
		if(StringUtils.isEmpty(cookieValue)){
			lcd = 	(LoginCookieDto) request.getAttribute("_elife_loginCookie_20151029");
			logger.debug("cookieValue="+cookieValue+"从 request.getAttribute('_elife_loginCookie_20151029')获得值:lcd="+lcd);
			request.setAttribute("_elife_loginCookie_20151029", lcd);
		}else{
			lcd = LoginCookieDto.buildDto(cookieValue);
			request.setAttribute("_elife_loginCookie_20151029", lcd);
		}
		if(lcd == null ){
			logger.debug("*********************************lcd is null");
			lcd = new LoginCookieDto().setCreateTime(System.currentTimeMillis()).setEnterTimes(1).setEnterType( LoginCookieDto.USER_CHANNEL_5 ).setLoginLevel(LoginCookieDto.loginLevel0);
			request.setAttribute("_elife_loginCookie_20151029", lcd);
		}
		return lcd;
	}
	
	public static Long getUserId(HttpServletRequest request){
		return getLoginDto(request).getUserId();
	}
	/**
     * 该方法不会返回 null
     * @param request
     * @return
     */
    public static JSONObject getRequestParams(HttpServletRequest request){
    	JSONObject params = new JSONObject();
    	if(null != request){
            Set<String> paramsKey = request.getParameterMap().keySet();
            for(String key : paramsKey){
                params.put(key, request.getParameter(key));
            }
        }
        return params;
    }
    /**
     * 该方法不会返回 null
     * @param request
     * @return
     */
    public static Map<String,String> getRequestParamsMap(HttpServletRequest request){
    	Map<String,String> params = new HashMap<String,String>();
        if(null != request){
            Set<String> paramsKey = request.getParameterMap().keySet();
            for(String key : paramsKey){
                params.put(key, request.getParameter(key));
            }
        }
        return params;
    }
    public static NameValuePair [] getNameValuePairs(HttpServletRequest request){
    	Map<String,String> map = getRequestParamsMap(request);
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
            	data[i] = new NameValuePair(key.toString().trim(), value.toString());
            }else{
            	data[i] = new NameValuePair(key.toString().trim(), "");
            }
            i++;
        }
    	return data;
    }
    
	public static String getReqData(HttpServletRequest request) {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			logger.error(e);
		}finally{
			if(reader!=null){
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		if(logger.isDebugEnabled())	logger.debug(sb.toString());
		return sb.toString();
	}
    public static  String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }
    
	public static String failedResponse(HttpServletResponse response,String resp){
		PrintWriter writer = null;
		try {
			response.setContentType("text/html; charset=utf-8");
			writer	 = response.getWriter();
			writer.print( StringEscapeUtils.escapeHtml4(resp));
			writer.flush();
		} catch (Exception e) {
			logger.error("响应空白页面", e);
		}finally{
			if(writer!=null){
				writer.close();
			}
		}
		return null;
	}
	
	/**
	 * eg: /order/confirm_nav?masterServiceCateId=100003&usrId=1211213<br>
	 * %2Forder%2Fconfirm_nav%3FusrName%3D%25E5%2591%25A8sss%26masterServiceCateId%3D100009
	 * @param request
	 * @return 已对中文?做转义
	 * @throws UnsupportedEncodingException 
	 */
	public static String getBackUrl(HttpServletRequest request) throws UnsupportedEncodingException{
		String backUrl =  getBackUrl(request.getRequestURI() ,  getRequestParams(request));
		//logger.info("backUrl"+backUrl);
		return backUrl;
	}
	/**
	 * eg: /order/confirm_nav?masterServiceCateId=100003&usrId=1211213<br>
	 * %2Forder%2Fconfirm_nav%3FusrName%3D%25E5%2591%25A8sss%26masterServiceCateId%3D100009
	 * @param url backUrl
	 * @param map  参数部分
	 * @return 已对中文?做转义
	 * @throws UnsupportedEncodingException 
	 */
	public static String getBackUrl(String url ,JSONObject map) throws UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer();
		if (map != null && map.size() > 0) {
			for (Entry<String, Object> enty : map.entrySet()) {
				 sb.append("&" + enty.getKey() + "=" + URLEncoder.encode((String)enty.getValue(),"utf-8"));
				//sb.append("&" + enty.getKey() + "=" +enty.getValue());
			}
			sb.replace(0, 1, "?");
		}	
		return 	URLEncoder.encode(	url+ sb.toString() , "utf-8");
	}
	
	public static String getFullUrlString(HttpServletRequest request) {
		String url = "";
		url = request.getRequestURL().toString();
		if (request.getQueryString() != null){
		    url += "?" + request.getQueryString();
		}
		return url;
	}
}
