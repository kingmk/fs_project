package com.lmy.web.common;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.exception.BizException;

public class ExceptionHandler implements HandlerExceptionResolver {
	private static Logger logger = Logger.getLogger(ExceptionHandler.class);
	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		logger.error(getRequestParam(request));
		try {
			response.setContentType("text/html; charset=utf-8");
			PrintWriter writer = response.getWriter();
			if(ex instanceof BizException){
				BizException bex = (BizException)ex;
				writer.write( JsonUtils.commonJsonReturn(bex.getErrorCode(), bex.getMsg()).toJSONString() );
				writer.flush();
				writer.close();
				logger.error("errorCode="+bex.getErrorCode()+",msg="+bex.getMsg()+",errorMsg="+bex.getErrorMsg());	
			}else{
				logger.error("",ex);
				//未处理异常 邮件通知...
				sendMailNotic(request, ex);
				//writer.write( JsonUtils.commonJsonReturn("9999", "系统繁忙").toJSONString() );
				writer.write(""); //空白页
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	private void sendMailNotic(HttpServletRequest request,Exception ex){
		//TODO
	}
	
	private String getRequestParam(HttpServletRequest request){
		StringBuffer sb = new StringBuffer();
		sb.append("Exception Request:");
		sb.append("{\"RequestURL\":"+request.getRequestURL());
		sb.append(",\"QueryString\":"+getRequestParams(request));
		sb.append(",\"Method\":"+request.getMethod());
		sb.append("}");
		return sb.toString();
	}
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
	
}
