package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsFileStore  extends BaseObject {
  /** 文件提交人用户id */
  private  Long  usrId;
  /** 原文件名 :200632336810302_ecs_elife01.png */
  private  String  originalFileName;
  /** 文件后缀 */
  private  String  suffixName;
  /** 现文件名(显示用) eg: test.jpg */
  private  String  fileName;
  /** 文件大小 单位字节 */
  private  Long  fileSize;
  /** 图片高度 */
  private  Integer  height;
  /** 图片宽度 */
  private  Integer  width;
  /** 文件绝对路劲 不对外暴露 前缀eg : /opt */
  private  String  filePathPrefix;
  /** 文件绝对路劲 可对外暴露 后缀缀eg : /data0/file/store/headimg/201704/05/uuid.png */
  private  String  filePathSuffix;
  /** 文件访问地址 eg: http://10.10.17.41/data0/file/store/headimg/201704/05/uuid.png */
  private  String  httpUrl;
  /** 有效截止日期 */
  private  Date  expiryEndTime;
  /** 文件当前状态 del,已删除;effect 有效,invalid 无效 */
  private  String  status;
  /** 文件访问地址 后缀缀eg : https://10.10.17.41/data0/file/store/headimg/201704/05/uuid.png */
  private  String  httpsUrl;
  /**  */
  private  Date  updateTime;
  /** 文件提交人用户id */
	public Long getUsrId(){
		return this.usrId;
	}
  /** 文件提交人用户id */
	public FsFileStore setUsrId(Long usrId){
		 this.usrId=usrId;
		 return this;
	}
  /** 原文件名 :200632336810302_ecs_elife01.png */
	public String getOriginalFileName(){
		return this.originalFileName;
	}
  /** 原文件名 :200632336810302_ecs_elife01.png */
	public FsFileStore setOriginalFileName(String originalFileName){
		 this.originalFileName=originalFileName;
		 return this;
	}
  /** 文件后缀 */
	public String getSuffixName(){
		return this.suffixName;
	}
  /** 文件后缀 */
	public FsFileStore setSuffixName(String suffixName){
		 this.suffixName=suffixName;
		 return this;
	}
  /** 现文件名(显示用) eg: test.jpg */
	public String getFileName(){
		return this.fileName;
	}
  /** 现文件名(显示用) eg: test.jpg */
	public FsFileStore setFileName(String fileName){
		 this.fileName=fileName;
		 return this;
	}
  /** 文件大小 单位字节 */
	public Long getFileSize(){
		return this.fileSize;
	}
  /** 文件大小 单位字节 */
	public FsFileStore setFileSize(Long fileSize){
		 this.fileSize=fileSize;
		 return this;
	}
  /** 图片高度 */
	public Integer getHeight(){
		return this.height;
	}
  /** 图片高度 */
	public FsFileStore setHeight(Integer height){
		 this.height=height;
		 return this;
	}
  /** 图片宽度 */
	public Integer getWidth(){
		return this.width;
	}
  /** 图片宽度 */
	public FsFileStore setWidth(Integer width){
		 this.width=width;
		 return this;
	}
  /** 文件绝对路劲 不对外暴露 前缀eg : /opt */
	public String getFilePathPrefix(){
		return this.filePathPrefix;
	}
  /** 文件绝对路劲 不对外暴露 前缀eg : /opt */
	public FsFileStore setFilePathPrefix(String filePathPrefix){
		 this.filePathPrefix=filePathPrefix;
		 return this;
	}
  /** 文件绝对路劲 可对外暴露 后缀缀eg : /data0/file/store/headimg/201704/05/uuid.png */
	public String getFilePathSuffix(){
		return this.filePathSuffix;
	}
  /** 文件绝对路劲 可对外暴露 后缀缀eg : /data0/file/store/headimg/201704/05/uuid.png */
	public FsFileStore setFilePathSuffix(String filePathSuffix){
		 this.filePathSuffix=filePathSuffix;
		 return this;
	}
  /** 文件访问地址 eg: http://10.10.17.41/data0/file/store/headimg/201704/05/uuid.png */
	public String getHttpUrl(){
		return this.httpUrl;
	}
  /** 文件访问地址 eg: http://10.10.17.41/data0/file/store/headimg/201704/05/uuid.png */
	public FsFileStore setHttpUrl(String httpUrl){
		 this.httpUrl=httpUrl;
		 return this;
	}
  /** 有效截止日期 */
	public Date getExpiryEndTime(){
		return this.expiryEndTime;
	}
  /** 有效截止日期 */
	public FsFileStore setExpiryEndTime(Date expiryEndTime){
		 this.expiryEndTime=expiryEndTime;
		 return this;
	}
  /** 文件当前状态 del,已删除;effect 有效,invalid 无效 */
	public String getStatus(){
		return this.status;
	}
  /** 文件当前状态 del,已删除;effect 有效,invalid 无效 */
	public FsFileStore setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** 文件访问地址 后缀缀eg : https://10.10.17.41/data0/file/store/headimg/201704/05/uuid.png */
	public String getHttpsUrl(){
		return this.httpsUrl;
	}
  /** 文件访问地址 后缀缀eg : https://10.10.17.41/data0/file/store/headimg/201704/05/uuid.png */
	public FsFileStore setHttpsUrl(String httpsUrl){
		 this.httpsUrl=httpsUrl;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsFileStore setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
