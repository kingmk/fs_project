package com.lmy.web.controller;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.model.dto.LoginCookieDto;
import com.lmy.core.service.impl.MasterQueryServiceImpl;
import com.lmy.core.service.impl.OrderEvaluateServiceImpl;
import com.lmy.core.service.impl.SearchMasterServiceImpl;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;
import com.lmy.web.common.WebUtil;
/**
 * @author fidel
 * @since 2017/4/30
 */
@Controller
public class SearchMasterController {
	private static final Logger logger = LoggerFactory.getLogger(SearchMasterController.class);
	@Autowired
	private MasterQueryServiceImpl masterQueryServiceImpl;
	@Autowired
	private OrderEvaluateServiceImpl orderEvaluateServiceImpl;
	@Autowired
	private SearchMasterServiceImpl searchMasterServiceImpl;
	@RequestMapping(value="/usr/search/master_nav" )
	public String search_master_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "isPlatRecomm" , required = false) String isPlatRecomm  	  //Y:N 是否查询平台推荐						
			,@RequestParam(value = "zxCateId" , required = false) Long zxCateId			               //筛选出类别id
			,@RequestParam(value = "orderBy" , required = false) String orderBy  					    //排序方式 orderNumDesc,orderNumAsc; priceDesc,priceAsc, evaluateScoreDesc,evaluateScoreAsc
			,@RequestParam(value = "currentPage" , required = false) Integer currentPage   				//当前页 从 0 开始 
			,@RequestParam(value = "perPageNum" , required = false) Integer perPageNum   			//每页显示条数
			){
			modelMap.put("isPlatRecomm", isPlatRecomm);
			modelMap.put("zxCateId", zxCateId);
			modelMap.put("orderBy", orderBy);
			modelMap.put("currentPage", currentPage !=null ? currentPage : 0);
			modelMap.put("perPageNum", perPageNum !=null ? perPageNum : 10);
			return "/usr/search/master_nav";
	}
	@RequestMapping(value="/usr/search/master_ajax_query" )
	@ResponseBody
	public String search_master_ajax(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "isPlatRecomm" , required = false) String isPlatRecomm  	  //Y:N 是否查询平台推荐						
			,@RequestParam(value = "zxCateId" , required = false) Long zxCateId			               //筛选出类别id
			,@RequestParam(value = "orderBy" , required = false) String orderBy  					    //排序方式 orderNumDesc,orderNumAsc; priceDesc,priceAsc, evaluateScoreDesc,evaluateScoreAsc
			,@RequestParam(value = "currentPage" , required = true) int currentPage   				//当前页 从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum   			//每页显示条数
			){
		JSONObject result =  searchMasterServiceImpl.search(isPlatRecomm, zxCateId, orderBy, currentPage, perPageNum);
		 return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue) ;
	}
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/usr/search/master_detail" )
	public String master_detail(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "enterType" , required = false) String enterType,  							//sourceSearchList,,sourceCateIntro,sourceFollowList
			@RequestParam(value = "masterInfoId" , required = true) long masterInfoId,			
			@RequestParam(value = "zxCateId" , required = false) Long zxCateId  						
			){
		LoginCookieDto loginDto = WebUtil.getLoginDto(request);
		JSONObject result = masterQueryServiceImpl.commonIntro(enterType,masterInfoId, zxCateId, loginDto.getUserId() != null ? loginDto.getUserId():-9999l);
		if(!JsonUtils.equalDefSuccCode(result)){
			logger.warn("查询错误,响应空白页面");
			return WebUtil.failedResponse(response,"");
		}
		modelMap.put("enterType", enterType);
		modelMap.put("zxCateId", zxCateId);
		modelMap.put("result", result);
		modelMap.put("resultStr", JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		//logger.info("普通用户视角 result"+result);

		try {
			JSONObject wxconfig = WeiXinInterServiceImpl.getJSSign(WebUtil.getFullUrlString(request));
			modelMap.put("wxconfig", JSON.toJSONString(wxconfig,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		
		return "/usr/search/master_detail";
	}
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/usr/search/master_detail_service_ajax_html_fragment",method={RequestMethod.POST})
	public String master_detail_service(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "masterUsrId" , required = true) long masterUsrId
			,@RequestParam(value = "zxCateId" , required = false) Long zxCateId){
		JSONObject result =  this.masterQueryServiceImpl.findMasterServiceCateInfo(masterUsrId);
		modelMap.put("result", result);
		modelMap.put("zxCateId", zxCateId);
		return "/usr/search/master_detail_service_ajax_html_fragment";
	}
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/usr/search/master_detail_evaluate_list_ajax_html_fragment",method={RequestMethod.POST})
	public String master_evaluate_list(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value = "masterUsrId" , required = true) long masterUsrId
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //当前页 从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum   //每页显示条数
			){
		JSONObject result =  orderEvaluateServiceImpl.masterEvaluateList(masterUsrId, currentPage, perPageNum);
		modelMap.put("result", result);
		return "/usr/search/master_detail_evaluate_list_ajax_html_fragment";
	}
	
}
