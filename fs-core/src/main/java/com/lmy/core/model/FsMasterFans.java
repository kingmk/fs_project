package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsMasterFans  extends BaseObject {
  /** 关注人(粉丝)usr_id */
  private  Long  followUsrId;
  /** 被关注人 usr_id */
  private  Long  focusUsrId;
  /** followed 已关注;cancel;已取消关注 */
  private  String  status;
  /**  */
  private  Date  updateTime;
  /** 关注人(粉丝)usr_id */
	public Long getFollowUsrId(){
		return this.followUsrId;
	}
  /** 关注人(粉丝)usr_id */
	public FsMasterFans setFollowUsrId(Long followUsrId){
		 this.followUsrId=followUsrId;
		 return this;
	}
  /** 被关注人 usr_id */
	public Long getFocusUsrId(){
		return this.focusUsrId;
	}
  /** 被关注人 usr_id */
	public FsMasterFans setFocusUsrId(Long focusUsrId){
		 this.focusUsrId=focusUsrId;
		 return this;
	}
  /** followed 已关注;cancel;已取消关注 */
	public String getStatus(){
		return this.status;
	}
  /** followed 已关注;cancel;已取消关注 */
	public FsMasterFans setStatus(String status){
		 this.status=status;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsMasterFans setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
