package com.lmy.core.dao ;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONObject;
import com.lmy.core.model.FsPayRecord;
@Component
public class FsPayRecordDao extends GenericDAOImpl<FsPayRecord> {
	@Override
	public String getNameSpace() {
		return "fs_pay_record";
	}
	
	public FsPayRecord findByOutTradeNo(String outTradeNo){
		Assert.isTrue(  StringUtils.isNotEmpty(outTradeNo));
		JSONObject map = new JSONObject();
		map.put("outTradeNo", outTradeNo);
		return this.getSqlSession().selectOne(this.getNameSpace()+".findByOutTradeNo", map);
	}
	/** 根据支付结果 update order  **/
	public int updateForPayByResult(long id , String bank_type , String transaction_id, boolean paySucc , Date now){
		return updateForPayByResult(id, transaction_id, bank_type,null, null, null, paySucc, now);
	}
	public int updateForPayByResult(long id ,String respTradeNo,String bankType ,String respCode , String respMsg,String remark, boolean paySucc , Date now){
		if(now == null ){
			now = new Date();
		}
		JSONObject map = new JSONObject();
		map.put("id", id);
		if(paySucc){
			map.put("tradeStatus", "succ");
		}else{
			map.put("tradeStatus", "fail");
		}
		map.put("tradeConfirmTime"	, now);
		map.put("updateTime"	, now);
		if(StringUtils.isNotEmpty(respTradeNo)){
			map.put("respTradeNo"	, respTradeNo);	
		}
		if(StringUtils.isNotEmpty(bankType)){
			map.put("bankType"	, bankType);	
		}
		if(StringUtils.isNotEmpty(respCode)){
			map.put("respCode"	, respCode);	
		}
		if(StringUtils.isNotEmpty(respMsg)){
			map.put("respMsg"	, respMsg);	
		}
		if(StringUtils.isNotEmpty(remark)){
			map.put("remark"	, remark);	
		}
		return this.getSqlSession().update(this.getNameSpace()+".updateForPayByResult", map);
	}
	
	
	public List<FsPayRecord> findByOrderIdAndTradeType(long orderId,String tradeType){
		JSONObject parameter = new JSONObject();
		parameter.put("orderId", orderId);
		if(StringUtils.isNotBlank(tradeType)){
			parameter.put("tradeType", tradeType);		
		}
		return this.getSqlSession().selectList(this.getNameSpace()+".findByOrderIdAndTradeType", parameter) ;
	}
		
	public List<FsPayRecord> findForConfirm1(Long gtId,int currentPage,int perPageNum , List<String> tradetypeList,List<String> tradeStatusList ){
		JSONObject parameter = new JSONObject();
		parameter.put("gtId", gtId);
		if(CollectionUtils.isNotEmpty(tradetypeList)){
			parameter.put("tradetypeList", tradetypeList);		
		}
		if(CollectionUtils.isNotEmpty(tradeStatusList)){
			parameter.put("tradeStatusList", tradeStatusList);		
		}
		parameter.put("limitBegin", perPageNum * currentPage);
		parameter.put("limitEnd", perPageNum);
		return this.getSqlSession().selectList(this.getNameSpace()+".findForConfirm1", parameter) ;
	}
	
	
}
