package com.lmy.web.controller;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.HttpService;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.utils.CookieUtil;
import com.lmy.common.utils.ResourceUtils;
import com.lmy.core.model.FsPeriodStatistics;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.FsWeiXinUrlServiceImpl;
import com.lmy.core.service.impl.MasterStatisticsServiceImpl;
import com.lmy.core.service.impl.OrderServiceImpl;
import com.lmy.core.service.impl.OrderSettlementServiceImpl;
import com.lmy.core.service.impl.StatisticsServiceImpl;
import com.lmy.core.service.impl.UsrServiceImpl;
import com.lmy.core.service.impl.ZDataFixImpl;
import com.lmy.core.utils.FsEnvUtil;
import com.lmy.web.common.SessionUtil;
import com.lmy.web.common.WebUtil;
@Controller
public class EnterBaseController {
	private static final String defGoTo = "/usr/common/my";
	private static final String defUsrHeadImg = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.service.basehost") + "/static/images/def_headimg.png";

	private static final Logger logger = Logger.getLogger(EnterBaseController.class);	
	private HttpService httpService = new HttpService();
	@Autowired
	private UsrServiceImpl usrServiceImpl;
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	@Autowired
	private OrderSettlementServiceImpl orderSettlementServiceImpl;
	@Autowired
	private MasterStatisticsServiceImpl masterStatisticsServiceImpl;
	@Autowired
	private StatisticsServiceImpl statisticsServiceImpl;
	@Autowired
	private ZDataFixImpl zDataFixImpl;
	
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
		if (StringUtils.isEmpty(weiXinAuthCode)) {
			return null;
		}
		String baseUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+
				ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appId") 
				+"&secret="+ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.wechat.appsecret")+"&code={0}&grant_type=authorization_code";
		String resp = httpService.doGet( MessageFormat.format(baseUrl, weiXinAuthCode) , "utf-8");
		JSONObject respJson = JSON.parseObject(resp);
		logger.info("get openid: "+respJson.toJSONString());
		return respJson.getString("openid");
	}

	@RequestMapping(value="/enter/manual_settlement")
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String manual_settlement(HttpServletRequest request,HttpServletResponse response) throws Exception{
		orderSettlementServiceImpl.autoSettlement(null);
		logger.info("=====manual settlement terminate=====");
		return null;
	}

	@RequestMapping(value="/enter/manual_master_stat")
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String manual_master_stat(HttpServletRequest request,HttpServletResponse response) throws Exception{
		masterStatisticsServiceImpl.calculateStatistics();
		logger.info("=====manual master stat terminate=====");
		return null;
	}

	@RequestMapping(value="/enter/test_search_master")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String test_search_master(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "filterCateId" , required = false) Long filterCateId
			,@RequestParam(value = "orderBy" , required = false) String orderBy
			,@RequestParam(value = "page" , required = true) int page
			,@RequestParam(value = "pageSize" , required = true) int pageSize
			) throws Exception{
		JSONObject result = this.masterStatisticsServiceImpl.searchMasters(filterCateId, orderBy, page, pageSize);
		return result.toJSONString();
	}


	@RequestMapping(value="/enter/test_order_succ")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String test_order_succ(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "out_trade_no" , required = true) String out_trade_no
			) throws Exception{
		if (!FsEnvUtil.isDev()) {
			return "only for test environment";
		}
		JSONObject result = this.orderServiceImpl.zxOrderHandWeiXinNotify(out_trade_no, "CFT", out_trade_no, true);
		return result.toJSONString();
	}
	

	@RequestMapping(value="/enter/test_period_statistics")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String test_period_statistics(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "month" , required = true) String month
			) throws Exception{
//		if (!FsEnvUtil.isDev()) {
//			return "only for test environment";
//		}
//		String[] monthes = {"2017-08","2017-09","2017-10","2017-11","2017-12",
//				"2018-01","2018-02","2018-03","2018-04","2018-05","2018-06","2018-07"};
//		for (int i = 0; i < monthes.length; i++) {
//			statisticsServiceImpl.monthlyStatistics(monthes[i]);
//		}
		
		statisticsServiceImpl.monthlyStatistics(month);
		
		return "";
	}

	@RequestMapping(value="/enter/test_update_respseconds")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String test_update_respseconds(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
//		JSONObject jrlt = zDataFixImpl.fixOrderRespSeconds();
//		return jrlt.toJSONString();
		return "not supported";
	}

	@RequestMapping(value="/test/sort_statistics")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String sort_statistics(HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		statisticsServiceImpl.sortStatistics();
		JSONObject jrlt = JsonUtils.commonJsonReturn();
		return jrlt.toJSONString();
	}

	@RequestMapping(value="/test/search_sorted_masters")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String search_sorted_masters(HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "cateId" , required = true) Long cateId
			,@RequestParam(value = "order" , required = true) String order) throws Exception{
		
		statisticsServiceImpl.clearSearchCache();
		JSONObject jrlt = statisticsServiceImpl.searchSortedMasters(cateId, order, false);
		return jrlt.toJSONString();
	}
}
