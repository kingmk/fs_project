package com.lmy.core.model.dto;
import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
@SuppressWarnings("serial")
public class FsChatRecordDto implements Serializable {
	private Long id ;
	  /** 会话id 关联表 fs_chat_session.session_no */
	  private  String  sessionNo;
	  /**  */
	  private  String  clientUniqueNo;
	  /** 发送人用户id */
	  private  Long  sentUsrId;
	  private String sendtUsrHeadImgUrl;
	  /** 接收人用户id */
	  private  Long  receUsrId;
      private String receUsrHeadImgUrl;
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
	  /** 对方阅读时间 */
	  private  Date  readTime;
	  /** 发送时间 **/
	  private Date createTime;
	  public boolean isImg(){
			//常见图片格式
		  if( StringUtils.isEmpty(suffixName)){
			  return false;
		  }else{
				String  [] imgsSuffixName = new String[]{"BMP","JPG","JPEG" ,"PNG","GIF" ,"PCX","TIFF","TGA","EXIF","FPX","SVG","PSD","CDR","PCD","DXF","UFO","EPS","HDRI","AI","RAW"};
				return ArrayUtils.contains(imgsSuffixName, suffixName.toUpperCase());			  
		  }
	  }
	  /** 会话id 关联表 fs_chat_session.session_no */
		public String getSessionNo(){
			return this.sessionNo;
		}
	  /** 会话id 关联表 fs_chat_session.session_no */
		public FsChatRecordDto setSessionNo(String sessionNo){
			 this.sessionNo=sessionNo;
			 return this;
		}
	  public String getClientUniqueNo() {
		return clientUniqueNo;
	}
	public FsChatRecordDto setClientUniqueNo(String clientUniqueNo) {
		this.clientUniqueNo = clientUniqueNo;
		return this;
	}
	/** 发送人用户id */
		public Long getSentUsrId(){
			return this.sentUsrId;
		}
	  /** 发送人用户id */
		public FsChatRecordDto setSentUsrId(Long sentUsrId){
			 this.sentUsrId=sentUsrId;
			 return this;
		}
	  /** 接收人用户id */
		public Long getReceUsrId(){
			return this.receUsrId;
		}
	  /** 接收人用户id */
		public FsChatRecordDto setReceUsrId(Long receUsrId){
			 this.receUsrId=receUsrId;
			 return this;
		}
		/** 消费发送者是否为master Y;N;0 其他 */
	  public String getSentIsMaster() {
		return sentIsMaster;
	}
	  /** 消费发送者是否为master Y;N;0 其他 */
	public FsChatRecordDto setSentIsMaster(String sentIsMaster) {
		this.sentIsMaster = sentIsMaster;
		return this;
	}
	/** 订单id */
		public Long getOrderId(){
			return this.orderId;
		}
	  /** 订单id */
		public FsChatRecordDto setOrderId(Long orderId){
			 this.orderId=orderId;
			 return this;
		}
	  /** 消息类型 text 普通文本(含普通表情文);img 图片; imgtext 图文; richtext 富文本 */
		public String getMsgType(){
			return this.msgType;
		}
	  /** 消息类型 text 普通文本(含普通表情文);img 图片; imgtext 图文; richtext 富文本 */
		public FsChatRecordDto setMsgType(String msgType){
			 this.msgType=msgType;
			 return this;
		}
	  /** 是否开启转义 Y;N 默认值 */
		public String getIsEscape(){
			return this.isEscape;
		}
	  /** 是否开启转义 Y;N 默认值 */
		public FsChatRecordDto setIsEscape(String isEscape){
			 this.isEscape=isEscape;
			 return this;
		}
	  /** 消息内容; 消息类型为img 为图片url */
		public String getContent(){
			return this.content;
		}
	  /** 消息内容; 消息类型为img 为图片url */
		public FsChatRecordDto setContent(String content){
			 this.content=content;
			 return this;
		}
		  /**  */
			public Long getFileStoreId(){
				return this.fileStoreId;
			}
		  /**  */
			public FsChatRecordDto setFileStoreId(Long fileStoreId){
				 this.fileStoreId=fileStoreId;
				 return this;
			}
		  /** 长度/文件大小 */
			public Long getSize(){
				return this.size;
			}
		  /** 长度/文件大小 */
			public FsChatRecordDto setSize(Long size){
				 this.size=size;
				 return this;
			}
		  /** 文件后缀 */
			public String getSuffixName(){
				return this.suffixName;
			}
		  /** 文件后缀 */
			public FsChatRecordDto setSuffixName(String suffixName){
				 this.suffixName=suffixName;
				 return this;
			}
		  /** 图片高度 */
			public Integer getHeight(){
				return this.height;
			}
		  /** 图片高度 */
			public FsChatRecordDto setHeight(Integer height){
				 this.height=height;
				 return this;
			}
		  /** 图片宽度 */
			public Integer getWidth(){
				return this.width;
			}
		  /** 图片宽度 */
			public FsChatRecordDto setWidth(Integer width){
				 this.width=width;
				 return this;
			}
	  /** Y;N */
		public String getIsRead(){
			return this.isRead;
		}
	  /** Y;N */
		public FsChatRecordDto setIsRead(String isRead){
			 this.isRead=isRead;
			 return this;
		}
	  /** 对方阅读时间 */
		public Date getReadTime(){
			return this.readTime;
		}
	  /** 对方阅读时间 */
		public FsChatRecordDto setReadTime(Date readTime){
			 this.readTime=readTime;
			 return this;
		}
		public String toString(){
		    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
		}
		public Long getId() {
			return id;
		}
		public FsChatRecordDto setId(Long id) {
			this.id = id;
			return this;
		}
		public String getSendtUsrHeadImgUrl() {
			return sendtUsrHeadImgUrl;
		}
		public FsChatRecordDto setSendtUsrHeadImgUrl(String sendtUsrHeadImgUrl) {
			this.sendtUsrHeadImgUrl = sendtUsrHeadImgUrl;
			return this;
		}
		public String getReceUsrHeadImgUrl() {
			return receUsrHeadImgUrl;
		}
		public FsChatRecordDto setReceUsrHeadImgUrl(String receUsrHeadImgUrl) {
			this.receUsrHeadImgUrl = receUsrHeadImgUrl;
			return this;
		}
		public Date getCreateTime() {
			return createTime;
		}
		public FsChatRecordDto setCreateTime(Date createTime) {
			this.createTime = createTime;
			return this;
		}
}
