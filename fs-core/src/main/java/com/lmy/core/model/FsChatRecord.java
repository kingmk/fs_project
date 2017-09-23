package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsChatRecord  extends BaseObject {
  /** 会话id 关联表 fs_chat_session.session_no */
  private  String  sessionNo;
  /**  */
  private  String  clientUniqueNo;
  /** 发送人用户id */
  private  Long  sentUsrId;
  /** 接收人用户id */
  private  Long  receUsrId;
  /** 消费发送者是否为master Y;N;0 其他 */
  private  String  sentIsMaster;
  /** 订单id */
  private  Long  orderId;
  /** 消息类型 text 普通文本(含普通表情文);img 图片; imgtext 图文; richtext 富文本 */
  private  String  msgType;
  /** 是否开启转义 Y;N 默认值 */
  private  String  isEscape;
  /** 消息内容; 消息类型为img 为图片url */
  private  String  content;
  /**  */
  private  Long  fileStoreId;
  /** 长度/文件大小 */
  private  Long  size;
  /** 文件后缀 */
  private  String  suffixName;
  /** 图片高度 */
  private  Integer  height;
  /** 图片宽度 */
  private  Integer  width;
  /** Y;N */
  private  String  isRead;
  /** effect;invalid;del 已删除 */
  private  String  status;
  /** 排序字段 def is 0 */
  private  Long  sort;
  /** 对方阅读时间 */
  private  Date  readTime;
  /**  */
  private  Date  updateTime;
  /** 会话id 关联表 fs_chat_session.session_no */
	public String getSessionNo(){
		return this.sessionNo;
	}
  /** 会话id 关联表 fs_chat_session.session_no */
	public FsChatRecord setSessionNo(String sessionNo){
		 this.sessionNo=sessionNo;
		 return this;
	}
	  public String getClientUniqueNo() {
		return clientUniqueNo;
	}
	public FsChatRecord setClientUniqueNo(String clientUniqueNo) {
		this.clientUniqueNo = clientUniqueNo;
		return this;
	}
/** 发送人用户id */
	public Long getSentUsrId(){
		return this.sentUsrId;
	}
  /** 发送人用户id */
	public FsChatRecord setSentUsrId(Long sentUsrId){
		 this.sentUsrId=sentUsrId;
		 return this;
	}
  /** 接收人用户id */
	public Long getReceUsrId(){
		return this.receUsrId;
	}
  /** 接收人用户id */
	public FsChatRecord setReceUsrId(Long receUsrId){
		 this.receUsrId=receUsrId;
		 return this;
	}
  /** 消费发送者是否为master Y;N;0 其他 */
	public String getSentIsMaster(){
		return this.sentIsMaster;
	}
  /** 消费发送者是否为master Y;N;0 其他 */
	public FsChatRecord setSentIsMaster(String sentIsMaster){
		 this.sentIsMaster=sentIsMaster;
		 return this;
	}
  /** 订单id */
	public Long getOrderId(){
		return this.orderId;
	}
  /** 订单id */
	public FsChatRecord setOrderId(Long orderId){
		 this.orderId=orderId;
		 return this;
	}
  /** 消息类型 text 普通文本(含普通表情文);img 图片; imgtext 图文; richtext 富文本 */
	public String getMsgType(){
		return this.msgType;
	}
  /** 消息类型 text 普通文本(含普通表情文);img 图片; imgtext 图文; richtext 富文本 */
	public FsChatRecord setMsgType(String msgType){
		 this.msgType=msgType;
		 return this;
	}
  /** 是否开启转义 Y;N 默认值 */
	public String getIsEscape(){
		return this.isEscape;
	}
  /** 是否开启转义 Y;N 默认值 */
	public FsChatRecord setIsEscape(String isEscape){
		 this.isEscape=isEscape;
		 return this;
	}
  /** 消息内容; 消息类型为img 为图片url */
	public String getContent(){
		return this.content;
	}
  /** 消息内容; 消息类型为img 为图片url */
	public FsChatRecord setContent(String content){
		 this.content=content;
		 return this;
	}
	  /**  */
		public Long getFileStoreId(){
			return this.fileStoreId;
		}
	  /**  */
		public FsChatRecord setFileStoreId(Long fileStoreId){
			 this.fileStoreId=fileStoreId;
			 return this;
		}
	  /** 长度/文件大小 */
		public Long getSize(){
			return this.size;
		}
	  /** 长度/文件大小 */
		public FsChatRecord setSize(Long size){
			 this.size=size;
			 return this;
		}
	  /** 文件后缀 */
		public String getSuffixName(){
			return this.suffixName;
		}
	  /** 文件后缀 */
		public FsChatRecord setSuffixName(String suffixName){
			 this.suffixName=suffixName;
			 return this;
		}
	  /** 图片高度 */
		public Integer getHeight(){
			return this.height;
		}
	  /** 图片高度 */
		public FsChatRecord setHeight(Integer height){
			 this.height=height;
			 return this;
		}
	  /** 图片宽度 */
		public Integer getWidth(){
			return this.width;
		}
	  /** 图片宽度 */
		public FsChatRecord setWidth(Integer width){
			 this.width=width;
			 return this;
		}
  /** Y;N */
	public String getIsRead(){
		return this.isRead;
	}
  /** Y;N */
	public FsChatRecord setIsRead(String isRead){
		 this.isRead=isRead;
		 return this;
	}
  /** effect;invalid;del 已删除 */
	public String getStatus(){
		return this.status;
	}
  /** effect;invalid;del 已删除 */
	public FsChatRecord setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** 排序字段 def is 0 */
	public Long getSort(){
		return this.sort;
	}
  /** 排序字段 def is 0 */
	public FsChatRecord setSort(Long sort){
		 this.sort=sort;
		 return this;
	}
  /** 对方阅读时间 */
	public Date getReadTime(){
		return this.readTime;
	}
  /** 对方阅读时间 */
	public FsChatRecord setReadTime(Date readTime){
		 this.readTime=readTime;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsChatRecord setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
