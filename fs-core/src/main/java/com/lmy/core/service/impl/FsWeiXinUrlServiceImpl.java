package com.lmy.core.service.impl;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import com.lmy.common.component.HttpService;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.utils.FsEnvUtil;

public class FsWeiXinUrlServiceImpl {
	private static Logger logger = Logger.getLogger(FsWeiXinUrlServiceImpl.class);
	/**
	 * 
	 * @param goTo  should startsWith / ,已经被转义了 eg: %2Forder%2Fconfirm_nav%3FusrName%3D%25E5%2591%25A8sss%26masterServiceCateId%3D100009   (/order/confirm_nav?masterServiceCateId=100003&usrId=1211213)
	 * @param _useRedirect
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public  static String createMenuClickUrl(String goTo,boolean _useRedirect) throws UnsupportedEncodingException{
		//http://app.ucaibank.com/loan/detail?loanId=354
		String baseUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId") 
				+"&redirect_uri={0}&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
		String redirectUrl = null;
		//forward --> redirect 在tomcat 上有丢失域名问题，jetty 上没有 2016-05-04 add by fidel 
		if(_useRedirect == true){
			if(StringUtils.isEmpty(goTo) ){
				goTo = "";
			}else{
				if(!goTo.startsWith("/")){
					logger.warn("goTo="+goTo +", not startsWith / ");
					goTo = "/"+goTo;
				}
			}
			if( StringUtils.isNotEmpty(goTo) && !(goTo.indexOf("?")>-1)){
				goTo = goTo + "?_useRedirect="+_useRedirect;
			}else if( StringUtils.isNotEmpty(goTo) && (goTo.indexOf("?")>-1)){
				goTo = goTo + "&_useRedirect="+_useRedirect;
			}
		}
		if(logger.isDebugEnabled())  logger.debug("goTo="+goTo+",当前环境:"+FsEnvUtil.getEnv());
		if(FsEnvUtil.isDev()){
			redirectUrl = "http://haozhao.xicp.net/enter/weixin?_goTo="+goTo;	
		}else if(FsEnvUtil.isTest()){
			redirectUrl = "http://haozhao.xicp.net/enter/weixin?_goTo="+goTo;	
		}
		else if(FsEnvUtil.isPro()){
			redirectUrl = "http://leimenyi.com.cn/enter/weixin?_goTo="+goTo;	
		}else{
			//TODO
			 redirectUrl = "http://app.ucaibank.com/yc/weixin/third_redirect/wx1b99f22989f7b28d?_goTo="+goTo;	
		}
		if(logger.isDebugEnabled()) 	logger.debug("redirectUrl="+redirectUrl);
		String lastUrl =  MessageFormat.format( baseUrl,  URLEncoder.encode(redirectUrl,"utf-8") );
		if(logger.isDebugEnabled())  logger.debug("clickUrl="+lastUrl);
		return lastUrl;
	}
	
	public static String createMenu(String menuStr) {
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + "TODO";
			HttpService hs = new HttpService();
			return hs.doPostRequestEntity(url, menuStr,false, "UTF-8");
		} catch (Exception e) {
			logger.error("openId="+menuStr,e);
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		//String menuStr ="{\"button\":[{\"key\":\"jqhk\",\"name\":\"借钱还卡\",\"type\":\"view\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b99f22989f7b28d&redirect_uri=http%3A%2F%2Fapp.youcai.life%2Fwx1b99f22989f7b28d_test%2Fyc%2Fweixin%2Fthird_redirect%2Fwx1b99f22989f7b28d%3F_goTo%3D%2Fwx1b99f22989f7b28d_test%2Findex&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"},{\"key\":\"jkjl\",\"name\":\"借款记录\",\"type\":\"view\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b99f22989f7b28d&redirect_uri=http%3A%2F%2Fapp.youcai.life%2Fwx1b99f22989f7b28d_test%2Fyc%2Fweixin%2Fthird_redirect%2Fwx1b99f22989f7b28d%3F_goTo%3D%2Fwx1b99f22989f7b28d_test%2Floan%2Fmyloans&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"},{\"key\":\"cjwt\",\"name\":\"常见问题\",\"type\":\"view\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx1b99f22989f7b28d&redirect_uri=http%3A%2F%2Fapp.youcai.life%2Fwx1b99f22989f7b28d_test%2Fyc%2Fweixin%2Fthird_redirect%2Fwx1b99f22989f7b28d%3F_goTo%3D%2Fwx1b99f22989f7b28d_test%2Fyc%2Fhelp&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"}]}";
		//String respXml = createMenu(menuStr);
		//System.out.println(respXml);
	}
}
