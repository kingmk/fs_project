package com.lmy.core.service.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.HttpService;
import com.lmy.common.utils.ResourceUtils;

// 微信小程序交互类
public class WXAppletServiceImpl {
	private static final Logger logger = Logger.getLogger(WXAppletServiceImpl.class);

	private static HttpService httpService = new HttpService();
	public static JSONObject code2Session(String code) throws IOException {

		String appId = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.applet.appId");
		String secret = ResourceUtils.getValue(ResourceUtils.LMYCORE, "fs.applet.appsecret");
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid="
				+appId+"&secret="+secret+"&js_code="+code+"&grant_type=authorization_code";
		String resp = httpService.doGet(url, "utf-8");
		//logger.info("获取微信access_token响应:resp="+resp);
		JSONObject respJson = JSON.parseObject(resp);
		logger.info("========获取微信小程序数据: request="+url+"; response="+respJson+"========");
		return respJson;
	}
}
