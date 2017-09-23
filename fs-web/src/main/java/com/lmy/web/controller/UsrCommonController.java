package com.lmy.web.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.core.model.FsUsr;
import com.lmy.core.service.impl.FollowServiceImpl;
import com.lmy.core.service.impl.FsZxCateQueryServiceImpl;
import com.lmy.core.service.impl.OrderQueryServiceImpl;
import com.lmy.core.service.impl.UsrAidUtil;
import com.lmy.core.service.impl.UsrServiceImpl;
import com.lmy.web.common.SessionUtil;
/**
 * 普通用户
 * @author 
 */
@Controller
public class UsrCommonController {
	private static Logger logger = Logger.getLogger(UsrCommonController.class);
	@Autowired
	private FsZxCateQueryServiceImpl fsZxCateQueryServiceImpl;	
	@Autowired
	private OrderQueryServiceImpl orderQueryServiceImpl;
	@Autowired
	private FollowServiceImpl followServiceImpl;
	@Autowired
	private UsrServiceImpl usrServiceImpl;
	/**
	 * 普通用户首页
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/usr/common/my")
	public String my(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		//用户头像 自定义头像>微信头像
		//用户昵称 自定义昵称>微信昵称/姓名 
		JSONObject cacheWeiXinUsrInfo = UsrAidUtil.getCacheWeiXinInfo(usr);
		modelMap.put("usrHeadImgUrl", 	UsrAidUtil.getUsrHeadImgUrl2(usr, cacheWeiXinUsrInfo, "")	);
		modelMap.put("usrNickName",  UsrAidUtil.getNickName2(usr, cacheWeiXinUsrInfo, "") );
		modelMap.put("englishName", usr!=null ? usr.getNickName() :"");
		modelMap.put("registerMobile", usr!=null ?  usr.getRegisterMobile():"" );
		return "/usr/common/my";
	}
	
	/**
	 * 个人资料 填写/展示页
	 * @return
	 */
	@RequestMapping(value="/usr/common/personal_data")
	public String personal_data(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		modelMap.put("loginUsr", usr);
		if(usr!=null && StringUtils.isNotEmpty(usr.getBirthDate()) && usr.getBirthYear()!=null ){
			modelMap.put("birthDate" ,  CommonUtils.dateToString(CommonUtils.stringToDate(usr.getBirthYear()+usr.getBirthDate(), "yyyyMMdd"), "yyyy-MM-dd", ""));
		}
		
		return "/usr/common/personal_data";
	}
	
	/**
	 * 个人资料 填写 提交页
	 * @return
	 */
	@RequestMapping(value="/usr/common/personal_data_submit")
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String personal_data_submit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
						,@RequestParam(value = "realName" , required = false) String realName   				    //真实姓名
						,@RequestParam(value = "nickName" , required = false) String nickName  		 			//昵称
						,@RequestParam(value = "englishName" , required = false) String englishName  		//英文名称
						,@RequestParam(value = "birthYear" , required = false) Integer birthYear   				//阳历出生年份  eg :1986
						,@RequestParam(value = "birthDate" , required = false) String birthDate   				//阳历生日 eg: 0220
						,@RequestParam(value = "birthTimeType" , required = false) String birthTimeType     //出生时刻类型  rank 区间; min 精确到分钟
						,@RequestParam(value = "birthTime" , required = false) String birthTime   				   //出生时间取值方式  09:30~11:20; 00:00~23:59 未填写; 21:30 精确到分
						,@RequestParam(value = "birthAddress" , required = false) String birthAddress  
						,@RequestParam(value = "sex" , required = false) String sex  										 //性别 M男人;F女人;O其他
						,@RequestParam(value = "marriageStatus" , required = false) String marriageStatus  	 //婚姻状态  single 单身;celibate 独身;married 已婚;divorce 离异;widowed 丧偶;remarriage 在婚
						,@RequestParam(value = "familyRank" , required = false) String familyRank  	 			//家中排行 ：eg 老大,长子;次女
			){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		JSONObject result =  usrServiceImpl.supplementUsrInfo(usr.getId(), realName, nickName, englishName,birthYear, birthDate, birthAddress,sex, marriageStatus, familyRank,birthTimeType,birthTime);
		if(JsonUtils.equalDefSuccCode(result)){
			logger.info("普通用户完善个人成功....更新session中usr对象信息");
			usr.setRealName(realName).setNickName(nickName).setEnglishName(englishName).setBirthYear(birthYear).setBirthDate(birthDate).setBirthTimeType(birthTimeType).setBirthTime(birthTime)
			.setBirthAddress(birthAddress).setSex(sex).setMarriageStatus(marriageStatus).setFamilyRank(familyRank)	;
			SessionUtil.mainSession(request, "usr", usr);
		}
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	@RequestMapping(value="/usr/common/order_list_nav")
	public String order_list_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response){
		return "/usr/common/order_list_nav";
	}
	
	
	@RequestMapping(value="/usr/common/order_list_ajax_query",method={RequestMethod.POST})
	@ResponseBody
	public String order_list_ajax_query(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum //每页显示条数 默认 20
			){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		JSONObject result = orderQueryServiceImpl.findCommonUsrOrderList(usr.getId(), currentPage, perPageNum, 0, null);
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	/** 我的关注列表 **/
	@RequestMapping(value="/usr/common/follow_list_nav")
	public String follow_nav(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = false) Integer currentPage   //从 0 开始
			,@RequestParam(value = "perPageNum" , required = false) Integer perPageNum //每页显示条数 默认 20
			){
		modelMap.put("currentPage", currentPage!=null ? currentPage : 0);
		modelMap.put("currentPage", perPageNum!=null ? perPageNum : 10);
		return "/usr/common/follow_list_nav";
	}
	
	
	/** 我的关注列表 **/
	@RequestMapping(value="/usr/common/follow_list_ajax_query",method={RequestMethod.POST})
	@ResponseBody
	public String follow_list(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "currentPage" , required = true) int currentPage   //从 0 开始
			,@RequestParam(value = "perPageNum" , required = true) int perPageNum //每页显示条数 默认 20
			){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		JSONObject result = followServiceImpl.findMyFollow(usr.getId(), currentPage, perPageNum);
		return JSON.toJSONString(result,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
	
	/** (添加)关注 **/
	@RequestMapping(value="/usr/common/follow",method={RequestMethod.POST})
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String follow(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "id" , required = false) Long id   
			,@RequestParam(value = "focusUsrId" , required = true) Long focusUsrId   
			){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		return followServiceImpl.followl(id, usr.getId(), focusUsrId).toJSONString();
	}
	
	/** 取消关注 **/
	@RequestMapping(value="/usr/common/follow_cancel" , method={RequestMethod.POST} )
	@ResponseBody
	@com.lmy.common.annotation.PreventDoubleClickAnnotation
	public String follow_cancel(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
			,@RequestParam(value = "masterUsrId" , required = true) long masterUsrId   
			){
		FsUsr usr = 	SessionUtil.getFsUsr(request);
		return this.followServiceImpl.followCancel(usr.getId(), masterUsrId).toJSONString();
	}
	
}
