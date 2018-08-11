package com.lmy.web.controller;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.core.model.FsZxCate;
import com.lmy.core.service.impl.AdvertisementServiceImpl;
import com.lmy.core.service.impl.FsZxCateQueryServiceImpl;
import com.lmy.core.service.impl.MasterQueryServiceImpl;
import com.lmy.core.service.impl.SearchMasterServiceImpl;

@Controller
public class ZxCateIntroController {
	
	@Autowired
	private SearchMasterServiceImpl searchMasterServiceImpl;
	@Autowired
	private FsZxCateQueryServiceImpl fsZxCateQueryServiceImpl;
	@Autowired
	private MasterQueryServiceImpl masterQueryServiceImpl;
	@Autowired
	private AdvertisementServiceImpl advertisementServiceImpl;
	/**
	 * 风水大类介绍页  ， 需要读取平台推荐大师
	 * @return
	 */
	@RequestMapping(value="/cate/introduce_index")
	public String category_index(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "zxCateId" , required = true) long zxCateId  														//afterSupplySubmit ; afterServiceConfigSubmit
			){
		//查询平台推荐大师列表
		List<FsZxCate>  zxCateList = fsZxCateQueryServiceImpl.findZxCate1(zxCateId, null, null, null, null);
		JSONObject result = searchMasterServiceImpl.search("Y", zxCateId, "priceAsc", 0, 10);
		modelMap.put("result", result);
		modelMap.put("zxCateBean", zxCateList.get(0));
		modelMap.put("resultStr", JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		return "/cate/introduce_index";
	}
	/**
	 * 普通用户首页
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/cate/introduce_nav")
	public String category_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){

		JSONArray banners = advertisementServiceImpl.getIndexBanner();
		modelMap.put("banners", banners);
		modelMap.put("bannerStr", JSON.toJSONString(banners,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue));
		modelMap.put("zxCateList", fsZxCateQueryServiceImpl.findZxCate1(null, null, 2l, "N", "EFFECT"));
		return "/cate/introduce_nav";
	}
	
}
