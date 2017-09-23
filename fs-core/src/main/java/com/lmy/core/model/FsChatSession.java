package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsChatSession  extends BaseObject {
  /** 会话编号 */
  private  String  sessionNo;
  /**  */
  private  Long  usrId;
  /** 会话有效截止日期 */
  private  Date  expiryDate;
  /** effect 有效;invalid */
  private  String  status;
  /** Y;N */
  private  String  isServiceProvider;
  /** 会话编号 */
	public String getSessionNo(){
		return this.sessionNo;
	}
  /** 会话编号 */
	public FsChatSession setSessionNo(String sessionNo){
		 this.sessionNo=sessionNo;
		 return this;
	}
  /**  */
	public Long getUsrId(){
		return this.usrId;
	}
  /**  */
	public FsChatSession setUsrId(Long usrId){
		 this.usrId=usrId;
		 return this;
	}
  /** 会话有效截止日期 */
	public Date getExpiryDate(){
		return this.expiryDate;
	}
  /** 会话有效截止日期 */
	public FsChatSession setExpiryDate(Date expiryDate){
		 this.expiryDate=expiryDate;
		 return this;
	}
  /** effect 有效;invalid */
	public String getStatus(){
		return this.status;
	}
  /** effect 有效;invalid */
	public FsChatSession setStatus(String status){
		 this.status=status;
		 return this;
	}
  /** Y;N */
	public String getIsServiceProvider(){
		return this.isServiceProvider;
	}
  /** Y;N */
	public FsChatSession setIsServiceProvider(String isServiceProvider){
		 this.isServiceProvider=isServiceProvider;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
