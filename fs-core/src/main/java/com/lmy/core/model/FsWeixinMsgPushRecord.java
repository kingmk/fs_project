package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsWeixinMsgPushRecord  extends BaseObject {
  /** 用户id */
  private  Long  userId;
  /** openid */
  private  String  openId;
  /** 模板id */
  private  String  templateId;
  /** 消息标题 */
  private  String  title;
  /**  */
  private  String  respMsgid;
  /** 消息推送返回code */
  private  String  respCode;
  /** 消息推送返回信息 */
  private  String  respMsg;
  /** 推送内容json */
  private  String  content;
  /**  */
  private  Long  orderId;
  /**  */
  private  String  remark;
  /** 用户id */
	public Long getUserId(){
		return this.userId;
	}
  /** 用户id */
	public FsWeixinMsgPushRecord setUserId(Long userId){
		 this.userId=userId;
		 return this;
	}
  /** openid */
	public String getOpenId(){
		return this.openId;
	}
  /** openid */
	public FsWeixinMsgPushRecord setOpenId(String openId){
		 this.openId=openId;
		 return this;
	}
  /** 模板id */
	public String getTemplateId(){
		return this.templateId;
	}
  /** 模板id */
	public FsWeixinMsgPushRecord setTemplateId(String templateId){
		 this.templateId=templateId;
		 return this;
	}
  /** 消息标题 */
	public String getTitle(){
		return this.title;
	}
  /** 消息标题 */
	public FsWeixinMsgPushRecord setTitle(String title){
		 this.title=title;
		 return this;
	}
  /**  */
	public String getRespMsgid(){
		return this.respMsgid;
	}
  /**  */
	public FsWeixinMsgPushRecord setRespMsgid(String respMsgid){
		 this.respMsgid=respMsgid;
		 return this;
	}
  /** 消息推送返回code */
	public String getRespCode(){
		return this.respCode;
	}
  /** 消息推送返回code */
	public FsWeixinMsgPushRecord setRespCode(String respCode){
		 this.respCode=respCode;
		 return this;
	}
  /** 消息推送返回信息 */
	public String getRespMsg(){
		return this.respMsg;
	}
  /** 消息推送返回信息 */
	public FsWeixinMsgPushRecord setRespMsg(String respMsg){
		 this.respMsg=respMsg;
		 return this;
	}
  /** 推送内容json */
	public String getContent(){
		return this.content;
	}
  /** 推送内容json */
	public FsWeixinMsgPushRecord setContent(String content){
		 this.content=content;
		 return this;
	}
  /**  */
	public Long getOrderId(){
		return this.orderId;
	}
  /**  */
	public FsWeixinMsgPushRecord setOrderId(Long orderId){
		 this.orderId=orderId;
		 return this;
	}
  /**  */
	public String getRemark(){
		return this.remark;
	}
  /**  */
	public FsWeixinMsgPushRecord setRemark(String remark){
		 this.remark=remark;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
