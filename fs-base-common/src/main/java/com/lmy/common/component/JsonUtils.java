package com.lmy.common.component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonUtils {
	/**
	 * model 转 jsonArray
	 * @param object
	 * @return
	 */
	public static JSONArray modelToJsonArray(Object object){
		return JSONArray.parseArray((JSON.toJSONString(object,SerializerFeature.DisableCircularReferenceDetect)));
	}
	/**
	 * model 转 json
	 * @param object
	 * @return
	 */
	public static JSONObject modelToJson(Object object){
		return JSON.parseObject(JSON.toJSONString(object,SerializerFeature.DisableCircularReferenceDetect));
	}
	/**
	 * mode 转 json String
	 * @param object
	 * @return
	 */
	public static String modelToJsonString(Object object){
		return JSON.toJSONString(object,SerializerFeature.DisableCircularReferenceDetect);
	}
	/**
	 * 默认的返回结果/格式
	 * @return {"head":{"code":"0000","msg":"操作成功"},"body":{}}
	 */
	public static JSONObject commonJsonReturn(){
			JSONObject result = new JSONObject();
			JSONObject head = new JSONObject();
			JSONObject body = new JSONObject();
			head.put("code", "0000");
			head.put("msg", "操作成功");
			result.put("head", head);
			result.put("body", body);
		return result;
	}
	
	/**
	 * 默认的返回结果/格式
	 * @return {"head":{"code":"0000","msg":"操作成功"},"body":{}}
	 */
	public static JSONObject commonJsonReturn(String headCode,String headMsg){
			JSONObject result = new JSONObject();
			JSONObject head = new JSONObject();
			JSONObject body = new JSONObject();
			head.put("code", headCode);
			head.put("msg", headMsg);
			result.put("head", head);
			result.put("body", body);
		return result;
	}
	public static Boolean codeEqual(JSONObject result, String code){
		if(result==null || result.getJSONObject("head") == null){
			return false;
		}
		String _code = result.getJSONObject("head").getString("code");
		if(_code == null ){
			return false;
		}
		return _code.equals(code);
	}
	
	/**
	 *  判断 result.getJSONObject("head").getString("code") 是否和 0000 相等
	 * @param result
	 * @return
	 */
	public static Boolean equalDefSuccCode(JSONObject result){
		return codeEqual(result, "0000");
	}
	
	/**
	 * 设置 body
	 * @param result  if null will new an instance
	 * @param headCode
	 * @param headMsg
	 * @return
	 */
	public static JSONObject setBody(JSONObject result,  String key,Object value){
		if(result == null){
			 result = JsonUtils.commonJsonReturn();
		}else{
			JSONObject body  = 	result.getJSONObject("body");
			body.put(key, value);
			result.put("body", body);
		}
		return result;
	}
	/**
	 * 设置 head
	 * @param result  if null will new an instance
	 * @param headCode
	 * @param headMsg
	 * @return
	 */
	public static JSONObject setHead(JSONObject result,  String headCode,String headMsg){
		if(result == null){
			 result = JsonUtils.commonJsonReturn(headCode, headMsg);
		}else{
			JSONObject head  = 	result.getJSONObject("head");
			head.put("code", headCode);
			head.put("msg", headMsg);
			result.put("head", head);
		}
		return result;
	}
	
	/**
	 */
	public static String getHeadCode(JSONObject result){
		JSONObject head =result!=null ? result.getJSONObject("head") : null;
		return  head!=null ? head.getString("code") : null;
	}
	/**
	 */
	public static String getHeadMsg(JSONObject result){
		JSONObject head =result!=null ? result.getJSONObject("head") : null;
		return   head!=null ? head.getString("msg") : null; 
	}
	/**
	 */
	public static Object getBodyValue(JSONObject result,String key){
		JSONObject body =result!=null ? result.getJSONObject("body") : null;
		return body!=null ?body.get(key) : null;
	}
}
