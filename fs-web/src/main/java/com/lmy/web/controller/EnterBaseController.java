package com.lmy.web.controller;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.HttpService;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.FsWeiXinUrlServiceImpl;
import com.lmy.core.service.impl.UsrServiceImpl;
import com.lmy.web.common.SessionUtil;
import com.lmy.web.common.WebUtil;
import com.lmy.common.utils.CookieUtil;
import com.lmy.common.utils.ResourceUtils;
@Controller
public class EnterBaseController {
	private static final Logger logger = Logger.getLogger(EnterBaseController.class);	
	private HttpService httpService = new HttpService();
	@Autowired
	private UsrServiceImpl usrServiceImpl;
	private static final String defGoTo = "/usr/common/my";
	private static final String defUsrHeadImg = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.service.basehost") + "/static/images/def_headimg.png";
	/**
	 * 
	 * @param request  参数 _goTo 相对路劲 eg:/usr/index	(优先使用); redirect_url 绝对路劲 eg: http://news.qq.com/a/20170404/016550.htm	
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@RequestMapping(value="/enter/weixin")
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String enter_weixin(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, String> params = WebUtil.getRequestParamsMap(request);
		if(logger.isDebugEnabled()){
			logger.debug("params:"+params);
		}
		try{
			String wxOpenId  = getWxOpenIdWithPaddle(params.get("code"), params.get("testOpenId") );
			if(StringUtils.isEmpty(wxOpenId)){
				String _goTo =getGoTo(params.get("_goTo") , null);
				String lastUrl =  FsWeiXinUrlServiceImpl.createMenuClickUrl(WebUtil.getBackUrl(_goTo,null), false);
				//logger.warn("begin 重定向到微信再一次获得opendId过程 lastUrl=" + lastUrl);
				response.sendRedirect(lastUrl);
				return null;
			}
			FsUsr usr = usrServiceImpl.findByOpenIdIfNotExistCreate(wxOpenId,defUsrHeadImg);
			String goTo =getGoTo(params.get("_goTo") , usr);
			mainSession(request , response ,usr);
			//使用redirect
			return "redirect:" + goTo;
		}catch(Exception e){
			logger.error("params:"+params, e);
			String lastUrl =  FsWeiXinUrlServiceImpl.createMenuClickUrl(WebUtil.getBackUrl(request), false);
			logger.warn(",begin 重定向到微信再一次获得opendId过程 lastUrl=" + lastUrl);
			response.sendRedirect(lastUrl);
			return null;
		}
	}
	
	private void mainSession(HttpServletRequest request,HttpServletResponse response,FsUsr usrFromDb){
		Assert.isTrue(usrFromDb!=null &&  StringUtils.isNotEmpty( usrFromDb.getWxOpenId() ));
		com.lmy.core.model.dto.LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		//if(!StringUtils.equals(loginDto.wxOpenId(), usrFromDb.getWxOpenId())	){
			loginDto.setEnterType(com.lmy.core.model.dto.LoginCookieDto.USER_CHANNEL_0);
			loginDto.setOpenId(usrFromDb.getWxOpenId());
			loginDto.setLoginLevel(com.lmy.core.model.dto.LoginCookieDto.loginLevel2).setUserId( usrFromDb.getId() ).setEnterTimes(1).setCreateTime(System.currentTimeMillis());
			CookieUtil.addCookie(response, WebUtil.LOGIN_PREFIX, loginDto.buildCookieValue(), 0);	
			request.getAttribute("");
			request.setAttribute("_elife_loginCookie_20151029"	, loginDto);
		//}
		//redis session 信息 
		SessionUtil.mainSession(request, "usr", usrFromDb);
	}
	
	private String getGoTo(String _goTo , FsUsr usr){
		if(StringUtils.isEmpty(_goTo)){
			if(usr == null || StringUtils.isEmpty(usr.getUsrCate()) || "common".equals( usr.getUsrCate() )){
				return defGoTo;
			}else{
				return "/usr/master/account";
			}
		}else{
			return _goTo;
		}
	}
	private String getWxOpenIdWithPaddle(String weiXinAuthCode , String testOpenId) throws Exception{
		if(com.lmy.core.utils.FsEnvUtil.isDev()){
			logger.info("dev 环境: 模拟用户 "+(StringUtils.isNotEmpty(testOpenId) ? testOpenId : "test_weixin_openid_10000"));
			return StringUtils.isNotEmpty(testOpenId) ? testOpenId : "test_weixin_openid_01";
		}
		String baseUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+
				ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId") 
				+"&secret="+ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appsecret")+"&code={0}&grant_type=authorization_code";
		String resp = httpService.doGet( MessageFormat.format(baseUrl, weiXinAuthCode) , "utf-8");
		JSONObject respJson = JSON.parseObject(resp);
		logger.info("get openid: "+respJson.toJSONString());
		return respJson.getString("openid");
	}
	
	
}
