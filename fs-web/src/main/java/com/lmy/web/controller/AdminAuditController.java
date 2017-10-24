package com.lmy.web.controller;
import java.io.IOException;

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
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.queue.beanstalkd.BeanstalkClient;
import com.lmy.common.utils.RSAHelper;
import com.lmy.core.service.impl.AdminAuditServiceImpl;
import com.lmy.core.service.impl.WeiXinInterServiceImpl;
import com.lmy.web.common.WebUtil;
/**
 * @author fidel
 * @since 2017/05/10
 */
@Controller
public class AdminAuditController {
	private static final Logger logger = LoggerFactory.getLogger(AdminAuditController.class);
//	private static final String priRsaKey = "MIIEowIBAAKCAQEApvtjv3+QtpKN2L5RVMCFTPPEOlVzz6bhJc7qrnXksOqaueLN4AvBQzH6a8KptC5Qzm4u2ZYmCUJGWTTcR3D/CD0/noblUZI8IcM1gSBJybKaysR/9iQReMftubHIzNmnL+WU/bWu7X96nXemO547pPDZo+FR3qwmXaVall8nX2oe4BUeyUwomqVNjfkyT/L6czlk42aKQhY4iC5LKrRR/JbTH3+pqQYKBGi3cHy5uMxZIE77iW4IWUbeUK6q8upAy4fcgWVZfYjg5KjrRTrHQhrVuw0mjO7CoHw00y6Bi+Y6m6TnvsDWLj3R2W0u9t1/M7pHh3DlHBE2MDwnKIS0zwIDAQABAoIBAE7hbJJ3AXatrqJX7XW66H2cnfLskSPj8nH42DYH7TuB6Autz5FqHCbRAjpLn/1E79lEhY819cvj6fWUGgcJowBSZq5NjSDGku9kFu9jwFeurp7gyo4NJc/Hqk5RZMkYfHkM3CAp5nO9B8BBWF7LdeOJ/1YCR7mH2yvbw/pLAv94D0EIQE9UFc2KdXebW/k7h9hFZPy+FO0ZFzJEu3ZwvhisZ1QiUEoIVAbbBQ6v0GbSgSEBMXaHFRisq0wHLQ0ZSDgOnu0E/GJOy8qnW7eA+N5KeBB216zVQBkNtqNn0+uTEmlLU/eMI5BxMn396ZlnTS/nJJtuIATF+406Dit74LkCgYEA3dCgfYl8pYdCQG8WKkmqhPQTfiUdZ+fqmVUGecZOEso6EYZegCFUgT3FZIgtL4LKl9IbrRzFrnXxGPtx8M712Ykq0jx/lpR+2iBB11xAHTJNVGE22ZLj4h+AHFBRwPh2YJ3Kmr3zVKy7Jw60bKhLZXhu8iR80h5k+r43+khh1WMCgYEAwLdpTom1F2qbLmDGkqp+MtVohcWHXHT+sac8glsLo5Q2q1Eou66JQWeX/N71o3nMAcqMB2NEjj/VgrZNmGA3Z4meZgAIs+yo1O86ABlDsWtBT0hdkFjSiFZVtZ8H7AjS33iPAEjM0ZHgcknJwVQcnBL+GMt5zhYO3LbGu7wO5KUCgYB72TDC5zEhexQSxfOqSNdcxKrdk/uHOIZUdpTXCVIuS5uAjXXWNMXqI4NDToVsMarMehPZ5Ga7sj6bpElKG0RJ4bx3BjB28xKg18HrbSafJqbmpiDp+ooih1QQewgqpo/yAihNniFu9cLJW/yBbrYqQcQwjw7igvX+ypaWuDNn4QKBgQCChTXbLUTq4RbMqFqUxsfF4edygj+VDN/aris7FjKhzSXhjYwK4MCw/VpgtxiiaUEUDXwdHMw3k2AlsRaIltGvW/NdnbRHN4d4/7TGkFXaeYu4CIjl1GJyWBagdV7GK4u2Mk6XoLanUSc0AGmWeEMQKYTSgiyyuRnDvj6Gs21ayQKBgG7vgKDfKysWfwHk4WazqaGIms81y3m8NeIlxfB5OM9NsZDedxEuAfsAPaPrbrFM1Gi57kFXko3xCzcRhNu9tFEYjf5tDsLweycTzKYs988/3V1dX2mpcNRrmATT2bcn1ZjQagZCs61IJRkejs4CHHwm+PlSGsj/vpBKI+c4LNZZ";
			
	private static final String priRsaKey= "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCm+2O/f5C2ko3YvlFUwIVM88Q6VXPPpuElzuqudeSw6pq54s3gC8FDMfprwqm0LlDObi7ZliYJQkZZNNxHcP8IPT+ehuVRkjwhwzWBIEnJsprKxH/2JBF4x+25scjM2acv5ZT9ta7tf3qdd6Y7njuk8Nmj4VHerCZdpVqWXydfah7gFR7JTCiapU2N+TJP8vpzOWTjZopCFjiILksqtFH8ltMff6mpBgoEaLdwfLm4zFkgTvuJbghZRt5Qrqry6kDLh9yBZVl9iODkqOtFOsdCGtW7DSaM7sKgfDTTLoGL5jqbpOe+wNYuPdHZbS723X8zukeHcOUcETYwPCcohLTPAgMBAAECggEATuFskncBdq2uolftdbrofZyd8uyRI+PycfjYNgftO4HoC63PkWocJtECOkuf/UTv2USFjzX1y+Pp9ZQaBwmjAFJmrk2NIMaS72QW72PAV66unuDKjg0lz8eqTlFkyRh8eQzcICnmc70HwEFYXst144n/VgJHuYfbK9vD+ksC/3gPQQhAT1QVzYp1d5tb+TuH2EVk/L4U7RkXMkS7dnC+GKxnVCJQSghUBtsFDq/QZtKBIQExdocVGKyrTActDRlIOA6e7QT8Yk7Lyqdbt4D43kp4EHbXrNVAGQ22o2fT65MSaUtT94wjkHEyff3pmWdNL+ckm24gBMX7jToOK3vguQKBgQDd0KB9iXylh0JAbxYqSaqE9BN+JR1n5+qZVQZ5xk4SyjoRhl6AIVSBPcVkiC0vgsqX0hutHMWudfEY+3HwzvXZiSrSPH+WlH7aIEHXXEAdMk1UYTbZkuPiH4AcUFHA+HZgncqavfNUrLsnDrRsqEtleG7yJHzSHmT6vjf6SGHVYwKBgQDAt2lOibUXapsuYMaSqn4y1WiFxYdcdP6xpzyCWwujlDarUSi7rolBZ5f83vWjecwByowHY0SOP9WCtk2YYDdniZ5mAAiz7KjU7zoAGUOxa0FPSF2QWNKIVlW1nwfsCNLfeI8ASMzRkeByScnBVBycEv4Yy3nOFg7ctsa7vA7kpQKBgHvZMMLnMSF7FBLF86pI11zEqt2T+4c4hlR2lNcJUi5Lm4CNddY0xeojg0NOhWwxqsx6E9nkZruyPpukSUobREnhvHcGMHbzEqDXwettJp8mpuamIOn6iiKHVBB7CCqmj/ICKE2eIW71wslb/IFutipBxDCPDuKC9f7Klpa4M2fhAoGBAIKFNdstROrhFsyoWpTGx8Xh53KCP5UM39quKzsWMqHNJeGNjArgwLD9WmC3GKJpQRQNfB0czDeTYCWxFoiW0a9b812dtEc3h3j/tMaQVdp5i7gIiOXUYnJYFqB1XsYri7YyTpegtqdRJzQAaZZ4QxAphNKCLLK5GcO+PoazbVrJAoGAbu+AoN8rKxZ/AeThZrOpoYiazzXLebw14iXF8Hk4z02xkN53ES4B+wA9o+tusUzUaLnuQVeSjfELNxGE2720URiN/m0OwvB7JxPMpiz3zz/dXV1faalw1GuYBNPZtyfVmNBqBkKzrUglGR6OzgIcfCb4+VIayP++kEoj5zgs1lk=" ;
			//ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.admin.api.mer.10000.rsa.private.key");
	@Autowired
	private AdminAuditServiceImpl adminAuditServiceImpl;
	
	/**
	 * 临时api
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/admin/audit/api2")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String api2(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response			) throws IOException {
		logger.info("AccessToken is:"+WeiXinInterServiceImpl.getAccessToken());
		return "";
	}
	
	@RequestMapping(value="/admin/audit/api3")
	@ResponseBody
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	public String api3(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response
						,@RequestParam(value = "queueName" , required = true) String queueName
						,@RequestParam(value = "data" , required = true) String data
						,@RequestParam(value = "token" , required = true) String token
					) 
					throws Exception {
		if(!"18221360028".equals(token)){
			logger.info("token  错误");
			return "";
		}
		BeanstalkClient.put(queueName, null, 60, 2, JSONObject.parse(data));
		return "";
	}
	
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/admin/audit/api" , method={RequestMethod.POST})
	public String adminAuditApi(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response			) {
		String data = WebUtil.getReqData(request);
		logger.info("解密前数据:"+data);
		// ip 限定 TODO
		RSAHelper rsa = new RSAHelper(priRsaKey, null, 2048);
		//解密
		String dataPlaint =  rsa.decrypt(data ,"utf-8" );
		logger.info("解密后明文dataPlaint:"+dataPlaint+",data:"+data);
		JSONObject dataJson = JSON.parseObject(dataPlaint);
		request.setAttribute("dataJson", dataJson);
		//请求时间check'
		long diffSec = CommonUtils.calculateDiffSeconds(System.currentTimeMillis(), dataJson.getLong("reqTime"));
		if(diffSec>60){
			logger.warn("请求已经过期  dataPlaint:{},diffSec:{}", dataPlaint,diffSec);
			return WebUtil.failedResponse(response,  JsonUtils.commonJsonReturn("9999", "请求已经过期").toJSONString() );
		}
		//forward
		return "forward:/admin/audit/api/"+dataJson.getString("method");
	}
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/admin/audit/api/master_apply_audit" , method={RequestMethod.POST})
	@ResponseBody
	public String master_apply_audit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response) {
		JSONObject dataJson = (JSONObject)request.getAttribute("dataJson");
		return adminAuditServiceImpl.masterApplyAudit(dataJson.getLongValue("masterInfoId"), dataJson.getString("auditStatus"), dataJson.getString("auditWord")).toJSONString();
	}
	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/admin/audit/api/refund_apply_audit" , method={RequestMethod.POST})
	@ResponseBody
	public String refund_apply_audit(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response) {
		JSONObject dataJson = (JSONObject)request.getAttribute("dataJson");
		return adminAuditServiceImpl.refundApplyAudit(dataJson.getLongValue("orderId"), dataJson.getString("isAgree"), dataJson.getString("refundAuditWord")).toJSONString();
	}

	@com.lmy.common.annotation.ExcludeSpringInterceptor(excludeClass={com.lmy.web.common.OpenIdInterceptor.class})
	@RequestMapping(value="/admin/audit/api/forbid_master" , method={RequestMethod.POST})
	@ResponseBody
	public String forbid_master(ModelMap modelMap , HttpServletRequest request,HttpServletResponse response) {
		JSONObject dataJson = (JSONObject)request.getAttribute("dataJson");
		return adminAuditServiceImpl.forbidMasterNotify(dataJson.getLongValue("masterInfoId"), 
				dataJson.getLongValue("forbidStartTime"), dataJson.getLongValue("forbidEndTime"), 
				dataJson.getString("reason")).toJSONString();
	}
}
