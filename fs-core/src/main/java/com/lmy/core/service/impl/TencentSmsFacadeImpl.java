package com.lmy.core.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TencentSmsFacadeImpl {
	private static final Logger logger = Logger.getLogger(TencentSmsFacadeImpl.class);
	
	private static int appId = 1400047241;
	private static String appKey = "0ef955167859428e56958a700fd61817";
	private static String url = "https://yun.tim.qq.com/v5/tlssmssvr/sendsms";
	
	private static String strToHash(String str) {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		    byte[] inputByteArray = str.getBytes();
		    messageDigest.update(inputByteArray);
		    byte[] resultByteArray = messageDigest.digest();
		    return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }    
	
    private static HttpURLConnection getPostHttpConn(String url) throws Exception {
        URL object = new URL(url);
        HttpURLConnection conn;
        conn = (HttpURLConnection) object.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        return conn;
	}
	
	public static boolean sendSms(Integer templId, JSONArray params, String phoneNumber) {
		try {
			JSONObject data = new JSONObject();
	        JSONObject tel = new JSONObject();
	        
	        long random = (new Random()).nextInt(999999)%900000+100000;
	        long curTime = System.currentTimeMillis()/1000;
	        
	        String sigOrg = String.format(
	        		"appkey=%s&random=%d&time=%d&mobile=%s",
	        		appKey, random, curTime, phoneNumber);
	        tel.put("nationcode", "86");
	        tel.put("mobile", phoneNumber);
	        
	        data.put("tel", tel);
	        data.put("sig", strToHash(sigOrg));
	        data.put("tpl_id", templId);
	        data.put("params", params);
	        data.put("sign", "雷门易");
	        data.put("time", curTime);
	        data.put("extend", "");
	        data.put("ext", "");
	        
	
	        // 与上面的 random 必须一致
			String wholeUrl = String.format("%s?sdkappid=%d&random=%d", url, appId, random);
	        HttpURLConnection conn = getPostHttpConn(wholeUrl);
	
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
	        wr.write(data.toString());
	        wr.flush();
	
	        // 显示 POST 请求返回的内容
	        StringBuilder sb = new StringBuilder();
	        int httpRspCode = conn.getResponseCode();
	        if (httpRspCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
	            String line = null;
	            while ((line = br.readLine()) != null) {
	                sb.append(line);
	            }
	            br.close();
	            String rltStr = sb.toString();
	            JSONObject resp = (JSONObject) JSONObject.parse(rltStr);
	            
	            String rltCode = resp.getString("result");
	            if (rltCode.equals("0")) {
					return true;
				} else {
					logger.info("=====发送消息失败，内容："+data.toJSONString()+"，返回："+resp.toJSONString()+"=====");
					return false;
				}
	        } else {
	        	return false;
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	public static void main(String[] args){

    	try {

            JSONArray params = new JSONArray();
            params.add("12345");
            params.add("雷门易");
            
    		boolean rlt = sendSms(51564, params, "18858194592");
    		System.out.println(rlt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
