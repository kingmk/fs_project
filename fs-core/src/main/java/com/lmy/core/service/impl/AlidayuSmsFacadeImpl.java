package com.lmy.core.service.impl;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.JsonUtils;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

public class AlidayuSmsFacadeImpl {
	private static final Logger logger = Logger.getLogger(AlidayuSmsFacadeImpl.class);
		//dayu.sms.serverUrl=http://gw.api.taobao.com/router/rest
		//dayu.sms.appKey=23497124
		//dayu.sms.appSecret=b7ead3c860ef7f3719bbc49d693374d6
	private static String serverUrl = "http://gw.api.taobao.com/router/rest";
	private static String appKey = "23862243";
	private static String appSecret = "c7b681dc9e57a72cc94ff8d1fb12640d";
	
	public static boolean alidayuSmsSend(JSONObject smsParamJson, String sendMobile,String smsTemplateCode , String extend) {
		TaobaoClient client = new DefaultTaobaoClient(serverUrl, appKey, appSecret);
		AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();
		request.setExtend(extend);
		request.setSmsType("normal");
		request.setSmsFreeSignName("雷门易");
		request.setSmsParamString(smsParamJson!=null ? smsParamJson.toJSONString():"");
		request.setRecNum(sendMobile);
		request.setSmsTemplateCode(smsTemplateCode);
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(request);
		} catch (Exception e) {
			logger.error("alidayuSmsSend error smsParamJson="+smsParamJson+",sendMobile="+sendMobile+",smsTemplateCode="+smsTemplateCode , e);
		}
		if(rsp == null || !rsp.isSuccess()){
			logger.warn("alidayuSmsSend error smsParamJson="+smsParamJson+",sendMobile="+sendMobile+",smsTemplateCode="+smsTemplateCode+",rsp="+JsonUtils.modelToJsonString(rsp));
			return false;
		}
		return true;
	}
	
	public static void main(String[] args){
//		JSONObject smsParamJson = new JSONObject();
//		smsParamJson.put("time","11:25");
//		smsParamJson.put("category","测试分类");
//		AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, "18667193683", "SMS_101155069" , null);
		

		JSONObject smsParamJson = new JSONObject();
//		smsParamJson.put("time","11:25");
		smsParamJson.put("category","测试分类");
		AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, "18667193683", "SMS_101030073" , null);
	}
}
