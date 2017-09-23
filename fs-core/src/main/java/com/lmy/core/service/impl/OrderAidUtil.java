package com.lmy.core.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.core.model.FsOrder;
import com.lmy.core.model.enums.OrderStatus;

public class OrderAidUtil {
	private static final Logger logger = Logger.getLogger(OrderAidUtil.class);
	/**  所有支付成功过的 **/
	public  static List<String> getCommAllOrderStatus(){
		return Arrays.asList(OrderStatus.pay_succ.getStrValue()  ,OrderStatus.completed.getStrValue(), 
				OrderStatus.refund_applied.getStrValue(),OrderStatus.refunding.getStrValue(), OrderStatus.refunded.getStrValue(), OrderStatus.refund_fail.getStrValue(),
				OrderStatus.settlementing.getStrValue(),OrderStatus.settlemented.getStrValue(),OrderStatus.settlement_fail.getStrValue() );
	}
	
	
	public  static List<String> getMasterLeiJitatus(){
		return Arrays.asList(OrderStatus.pay_succ.getStrValue(),  OrderStatus.completed.getStrValue(),
				OrderStatus.refund_applied.getStrValue(),OrderStatus.refunding.getStrValue(),OrderStatus.refunded.getStrValue(), OrderStatus.refund_fail.getStrValue(),
				OrderStatus.settlementing.getStrValue(),OrderStatus.settlemented.getStrValue(),OrderStatus.settlement_fail.getStrValue() );
	}
	
	public  static List<String> getMasterWaitIncometatus(){
		return Arrays.asList(OrderStatus.pay_succ.getStrValue() ,OrderStatus.completed.getStrValue(), OrderStatus.refund_fail.getStrValue(),
				OrderStatus.settlementing.getStrValue(),OrderStatus.settlement_fail.getStrValue() );
	}
	/** 已退款 = 退款确认成功+ 退款确认中 **/
	public  static List<String> getMasterRefundSatus(){
		return Arrays.asList( OrderStatus.refunded.getStrValue() ,OrderStatus.refunding.getStrValue()  );
	}
	
	public  static List<String> getCanEvaluateStatus(){
		return Arrays.asList(OrderStatus.completed.getStrValue(), 
				OrderStatus.refund_applied.getStrValue(),OrderStatus.refunding.getStrValue(), OrderStatus.refunded.getStrValue(), OrderStatus.refund_fail.getStrValue(),
				OrderStatus.settlementing.getStrValue(),OrderStatus.settlemented.getStrValue(),OrderStatus.settlement_fail.getStrValue() );
	}
	
	/** 订单是否可以(普通)发起退款申请 **/
	public static boolean isOrderCanApplyRefund(FsOrder order , Date now){
		return  ( Arrays.asList(OrderStatus.pay_succ.getStrValue(), OrderStatus.completed.getStrValue()).contains( order.getStatus() )  )
										&& (order.getSellerFirstReplyTime()!=null &&   CommonUtils.calculateDiffSeconds(order.getSellerFirstReplyTime(), now) > 3600 * 24 )
										&& order.getSettlementTime().getTime() > now.getTime();
	}
	
	

	
	
	public static List<Long> getNeedSupplyOrderInfoZxCateIds(){
		return Arrays.asList(
				100000L,100001L,100002L,100003L , 
				100008L,100009L  ,100010L,
				100004L , 100005L,
				100006L,
				100007L
				);
	}
	
	
	public static boolean supplyOrderInfoParamsCheck(Long cateId , JSONArray dataList){
		if(!getNeedSupplyOrderInfoZxCateIds().contains( cateId )){
			logger.info("cateId:"+cateId+",不需要补充资料 dataList:"+dataList);
			return true;
		}
		if(CollectionUtils.isEmpty(dataList)){
			return true;
		}
		Set<Long> sizeGtOne =new  HashSet<Long>();
		sizeGtOne.add(100004l);
		sizeGtOne.add(100005l);
		sizeGtOne.add(100007l);
		if( sizeGtOne.contains(cateId)  && dataList.size()<2){
			return false;
		}
		//isFiancee Y|N;isFiance Y|N;isFather Y|N;isMather Y|N;isOwner 是否为主人;isSpouse 是否为配偶 Y:N
		//birthTimeType  	rank 区间; min 精确到分钟;birthTime 09:30~11:20; 00:00~23:59 未填写; 21:30 精确到分
		Map<Long , List<String>> mustParamsMap = new HashMap<Long ,List<String>>();
		mustParamsMap.put(100000l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" , "birthAddress", "birthTimeType", "birthTime","marriageStatus" , "familyRank")); // 流年运势  1994-02-20 ;birthTimeType 	rank 区间; min 精确到分钟;birthTime 09:30~11:20; 00:00~23:59 未填写; 21:30 精确到分; 可选参数:  englishName
		mustParamsMap.put(100001l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" , "birthAddress", "birthTimeType", "birthTime")); //婚恋感情
		mustParamsMap.put(100002l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" , "birthAddress", "birthTimeType", "birthTime","marriageStatus" , "familyRank")); //健康事业财运
		mustParamsMap.put(100003l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" , "birthAddress", "birthTimeType", "birthTime","marriageStatus" , "familyRank")); //命运祥批  

		
		mustParamsMap.put(100008l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" ,"birthTimeType", "birthTime", "marriageStatus" , "familyRank")); //个人改名  curUsrName 当前名; 曾用名 onceName
		mustParamsMap.put(100009l, Arrays.asList("birthDate" , "birthTimeType", "birthTime","sex"  , "familyRank")); //个人起名   TODO 
		//																	成立时间                  行业           公司地址         经营范围                 是否本人Y|N   企业主姓名
		mustParamsMap.put(100010l, Arrays.asList("establishedDate" , "industry" , "comAddress","scopeOfBusiness" ,"isSelf",         "realName"  ,    "birthDate" , "sex" ,"birthTimeType", "birthTime")); //公司起名 可选参数: 公司现用名 comNowName
		//																																																	     期望结婚时间               //
		mustParamsMap.put(100004l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" , "birthAddress", "birthTimeType", "birthTime" ,"isFiancee","isFiance")); //结婚吉日  list 可选 参数 englishName, expectMarriageDateBegin,expectMarriageDateEnd
		
		//fetusNum  第几胎
		mustParamsMap.put(100005l, Arrays.asList("isSelf","realName" ,"birthDate" , "sex" , "birthAddress", "birthTimeType", "birthTime","fetusNum", "isFather","isMather")); //择日生子 list 可选参数:  englishName ;expectTimeRange eg: 2017-01-01 08:18~2017-01-01 08:18
		
		//		成立时间                  行业           公司地址         经营范围                  企业现用名         是否本人Y|N   企业主姓名
		mustParamsMap.put(100006l, Arrays.asList(  "industry" , "comAddress","scopeOfBusiness" ,"curComName" ,"isSelf",  "realName"  ,  "birthAddress",  "birthDate" , "sex" ,"birthTimeType", "birthTime")); ///开张开市 可选参数: 公司曾用名 
		
																		//新宅地址          //新宅落成时间       					
		mustParamsMap.put(100007l, Arrays.asList("newAddress" , "completedTime",
				"isSelf",         "realName"  ,  "birthAddress",  "birthDate" , "sex" ,"birthTimeType", "birthTime","isOwner","isSpouse")
				); //乔迁择日 list 另一半的信息  ,可选参数 expectMoveDate


		List<String> mustParams =    mustParamsMap.get(cateId);
		if(CollectionUtils.isEmpty(mustParams)){
			return true;
		}
		//性别 M男人;F女人;O其他
		String [] correctSex = new String[]{"M","F","O"};
		String [] correctMarriageStatus = new String[]{"single","celibate","married","divorce","widowed","remarriage"};
		int i=0;
		for(Object obj :  dataList){
			if (cateId == 100007l && i==1) {
				break;
			}
			JSONObject checkData = (JSONObject) obj;
			for(String param :  mustParams){
				String value = checkData.getString( param);
				if(StringUtils.isEmpty(value)){
					logger.warn("参数"+param+",value:"+value+  ",格式错误 checkData:" +checkData+",dataList:"+dataList );
					return false;
				}
				if("realName".equals(param)){
					if( StringUtils.isNotEmpty(value) &&(  value .length()<2 ||  !CommonUtils.isChinese(value)  )){
						logger.warn("参数"+param+",value:"+value+  ",姓名格式错误 checkData:" +checkData+",dataList:"+dataList );
						return false;
					}
				}
				else if("sex".equals(param)){
					if(    !ArrayUtils.contains(correctSex,  value )    ){
						logger.warn("参数"+param+",value:"+value+  ",性别错误 checkData:" +checkData+",dataList:"+dataList );
						return false;
					}
				}
				else if("marriageStatus".equals(param)){
					if( !ArrayUtils.contains(correctMarriageStatus,   value  )    ){
						logger.warn("参数"+param+",value:"+value+  ",婚姻状态 checkData:" +checkData+",dataList:"+dataList );
						return false;
					}
				}
				else if("birthDate".equals(param) || "expectMoveDate".equals(param) ||  "completedTime".equals(param)){
					if(StringUtils.isEmpty(value) || ( !"未知".equals(value  )  && CommonUtils.stringToDate(value, "yyyy-MM-dd") ==null)   ){
						logger.warn("参数"+param+",value:"+value+  ",日期错误 checkData:" +checkData+",dataList:"+dataList );
						return false;
					}
				}
				//第几胎
				//fetusNum
				else if("fetusNum".equals(param)){
					Long d = Long.valueOf(value);
					if( d<1 || d>10 ){
						logger.warn("参数"+param+",value:"+value+  ", 第几胎错误 checkData:" +checkData+",dataList:"+dataList );
						return false;
					}
				}else{
					if( value.length()<1 ){
						logger.warn("参数"+param+",value:"+value+  ", 参数长度错误 checkData:" +checkData+",dataList:"+dataList );
						return false;
					}
				}
			}
			i++;
		}
		return true;
	}
	
	
	
	
	public static String getOrerExtraInfoUsrName(String orderExtraInfo){
		if(StringUtils.isEmpty(orderExtraInfo)){
			return null;
		}
		JSONArray list = JSON.parseArray(orderExtraInfo);
		if(CollectionUtils.isEmpty(list)){
			return "";
		}
		JSONObject one = list.getJSONObject(0);
		return one.getString("realName");
	}
	public static  long getSumAmtFromStatusAmtMap(Map<String, Long> statusTotalFeeMap){
		if(statusTotalFeeMap == null || statusTotalFeeMap.isEmpty()){
			return 0l;
		}
		Set<Entry<String, Long>>  set = statusTotalFeeMap.entrySet();
		Long all = 0l;
		for(Entry<String, Long> entry :  set ){
			all = all + entry.getValue();
		}
		return all;
	}
	public static long getSumAmtFromStatusAmtMap(Map<String, Long> statusTotalFeeMap , String status){
		return  (statusTotalFeeMap!=null &&  statusTotalFeeMap.get(status) !=null ) ? statusTotalFeeMap.get(status) : 0l;
	}
	public static long getSumAmtFromStatusAmtMap(Map<String, Long> statusTotalFeeMap , List<String> statusList){
		if(CollectionUtils.isEmpty(statusList) || (statusTotalFeeMap==null || statusTotalFeeMap.isEmpty() ) ){
			return 0l;
		}
		Long all = 0l;
		for(String status :  statusList){
			all = all + (statusTotalFeeMap.get(status) !=null  ? statusTotalFeeMap.get(status) : 0l );
		}
		return all;
	}
	
	
	
	public static String masterIncomeDetailGetStatusDesc(String status){
		if(OrderStatus.pay_succ.getStrValue().equals(status) ||  OrderStatus.completed.getStrValue().equals(status) ||
			OrderStatus.settlementing.getStrValue().equals(status)  || OrderStatus.settlement_fail.getStrValue().equals(status)||OrderStatus.refund_fail.getStrValue().equals(status) ){
			return "未到账";
		}
		else if(OrderStatus.refunding.getStrValue().equals(status)  || OrderStatus.refund_applied.getStrValue().equals(status)  ){
			return "退款申请中";
		}
		else if(OrderStatus.refunded.getStrValue().equals(status)){
			return "已退款";
		}
		else if(OrderStatus.settlemented.getStrValue().equals(status)){
			return "已到账";
		}else{
			return OrderStatus.value(status).getStrValue();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static String buildLastChatTimeStr(Date now , Date lastChatTime){
		if(lastChatTime == null ){
			return  "刚刚";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		long time2 = cal.getTimeInMillis();
		cal.setTime(now);
		long time1 = lastChatTime.getTime();
		long between = (time2 - time1) / 1000;// 除以1000是为了转换成秒
		long minute1 = between / (60);
		//2小时内：刚刚
		if (minute1 <= 5) {
			return  "刚刚";
		}
		//当天：HH:mm
		else if ( DateUtils.isSameDay(now, lastChatTime)	&&	minute1 >5 ) {
			SimpleDateFormat hhmm = new SimpleDateFormat("HH:mm");
			return  hhmm.format(lastChatTime);
		}
		//历史：MM-DD 同年
		else if (  !DateUtils.isSameDay(now, lastChatTime) &&  (now.getYear() == lastChatTime.getYear() ) ) {
			SimpleDateFormat hhmm = new SimpleDateFormat("MM-dd HH:mm");
			return hhmm.format(lastChatTime);
		}
		//历史：YY-MM-DD 夸年
		else if (  !DateUtils.isSameDay(now, lastChatTime) &&  (now.getYear() != lastChatTime.getYear() ) ) {
			SimpleDateFormat hhmm = new SimpleDateFormat("YY-MM-dd");
			return hhmm.format(lastChatTime);
		}
		else {
			SimpleDateFormat hhmm = new SimpleDateFormat("YY-MM-dd");
			return  hhmm.format(lastChatTime);
		}
	}
	/**
	 * 计算应交个税 单位分
	 * @param beforeTaxIncomeRmbAmt 税前收入 单位分
	 * @return
	 */
	public static long calPersonalIncomeTaxRmbAmt(long beforeTaxIncomeRmbAmt){
		if(beforeTaxIncomeRmbAmt<= 800 * 100){
			return 0l;
		}
		else if(beforeTaxIncomeRmbAmt> 800 * 100 && beforeTaxIncomeRmbAmt <= 4000 * 100){
			//return Double.valueOf( (beforeTaxIncomeRmbAmt - 800*100 )* 0.2d ).longValue();
			return mul(beforeTaxIncomeRmbAmt - 800*100 , 0.2d).longValue();
		}
		else if(beforeTaxIncomeRmbAmt> 4000 * 100 && beforeTaxIncomeRmbAmt <= 20000 * 100){
			long _jian = mul(beforeTaxIncomeRmbAmt, 0.2d).longValue();
			return mul(beforeTaxIncomeRmbAmt -_jian , 0.2d).longValue();
			//return Double.valueOf( ( beforeTaxIncomeRmbAmt - (0.2d *beforeTaxIncomeRmbAmt ) ) * 0.2d  ).longValue();
		}
		else if(beforeTaxIncomeRmbAmt> 20000 * 100&& beforeTaxIncomeRmbAmt <= 50000 * 100){
			long _jian = mul(beforeTaxIncomeRmbAmt, 0.2d).longValue();
			return mul(beforeTaxIncomeRmbAmt -_jian , 0.3d).longValue() - 2000 * 100 ;
			//Double d = (beforeTaxIncomeRmbAmt - 0.2d * beforeTaxIncomeRmbAmt) *0.3d - 2000 * 100 ;
			//return d.longValue();
		}else{
			long _jian = mul(beforeTaxIncomeRmbAmt, 0.2d).longValue();
			return mul(beforeTaxIncomeRmbAmt -_jian , 0.4d).longValue()   -  7000*100 ;
			//Double d = (beforeTaxIncomeRmbAmt-  0.2d * beforeTaxIncomeRmbAmt) * 0.4d - 7000*100 ;
			//return d.longValue();
		}
	}
	/**
	 * 0.3d
	 * @return
	 */
	public static Double getPlatCommissionRate(){
		return 0.3d;
	}
	
    /**
     * 两个Double数相乘
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double mul(Double v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }
    /**
     * 两个Double数相乘
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double mul(Long v1,Double v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }
	
   public static void main(String[] args) {
	   long beforeTaxIncomeRmbAmt = 60000 * 100;
	   long tax  = calPersonalIncomeTaxRmbAmt(beforeTaxIncomeRmbAmt);
	   System.out.println( tax);
   }
}
