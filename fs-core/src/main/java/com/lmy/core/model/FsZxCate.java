package com.lmy.core.model ;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Date;
import com.lmy.common.model.base.BaseObject;
@SuppressWarnings("serial")
public class FsZxCate  extends BaseObject {
  /** 类目名称 */
  private  String  name;
  /** 一句话描述 */
  private  String  description;
  /** 层级 1 顶级; */
  private  Long  level;
  /** 父类目id */
  private  Long  parentId;
  /** 父类目名称 */
  private  String  parentName;
  /** 建议人民币价格 单位分 */
  private  Long  suggestAmt;
  /** 排序字段 */
  private  Long  sort;
  /** 用户自定义 Y;N */
  private  String  usrDefined;
  /** 创建人id */
  private  Long  createUsrId;
  /** 当前状态;DEL 已删除,EFFECT 有效,INVALID 无效 */
  private  String  status;
  /**  */
  private  Date  updateTime;
  /** 类目名称 */
	public String getName(){
		return this.name;
	}
  /** 类目名称 */
	public FsZxCate setName(String name){
		 this.name=name;
		 return this;
	}
  /** 一句话描述 */
	public String getDescription(){
		return this.description;
	}
  /** 一句话描述 */
	public FsZxCate setDescription(String description){
		 this.description=description;
		 return this;
	}
  /** 层级 1 顶级; */
	public Long getLevel(){
		return this.level;
	}
  /** 层级 1 顶级; */
	public FsZxCate setLevel(Long level){
		 this.level=level;
		 return this;
	}
  /** 父类目id */
	public Long getParentId(){
		return this.parentId;
	}
  /** 父类目id */
	public FsZxCate setParentId(Long parentId){
		 this.parentId=parentId;
		 return this;
	}
  /** 父类目名称 */
	public String getParentName(){
		return this.parentName;
	}
  /** 父类目名称 */
	public FsZxCate setParentName(String parentName){
		 this.parentName=parentName;
		 return this;
	}
  /** 建议人民币价格 单位分 */
	public Long getSuggestAmt(){
		return this.suggestAmt;
	}
  /** 建议人民币价格 单位分 */
	public FsZxCate setSuggestAmt(Long suggestAmt){
		 this.suggestAmt=suggestAmt;
		 return this;
	}
  /** 排序字段 */
	public Long getSort(){
		return this.sort;
	}
  /** 排序字段 */
	public FsZxCate setSort(Long sort){
		 this.sort=sort;
		 return this;
	}
  /** 用户自定义 Y;N */
	public String getUsrDefined(){
		return this.usrDefined;
	}
  /** 用户自定义 Y;N */
	public FsZxCate setUsrDefined(String usrDefined){
		 this.usrDefined=usrDefined;
		 return this;
	}
  /** 创建人id */
	public Long getCreateUsrId(){
		return this.createUsrId;
	}
  /** 创建人id */
	public FsZxCate setCreateUsrId(Long createUsrId){
		 this.createUsrId=createUsrId;
		 return this;
	}
  /** 当前状态;DEL 已删除,EFFECT 有效,INVALID 无效 */
	public String getStatus(){
		return this.status;
	}
  /** 当前状态;DEL 已删除,EFFECT 有效,INVALID 无效 */
	public FsZxCate setStatus(String status){
		 this.status=status;
		 return this;
	}
  /**  */
	public Date getUpdateTime(){
		return this.updateTime;
	}
  /**  */
	public FsZxCate setUpdateTime(Date updateTime){
		 this.updateTime=updateTime;
		 return this;
	}
	public String toString(){
	    return JSON.toJSONString(this,SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
	}
}
