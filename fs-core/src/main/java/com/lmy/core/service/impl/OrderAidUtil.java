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
import java.util.Map.Entry;
import java.util.Set;

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
	
	private static final List<String> forbidWords = Arrays.asList("毒品","冰毒","摇头丸","海洛因","大麻","K粉","枪","中共","共产党","习近平","李克强","栗战书","汪洋","王沪宁","赵乐际","张德江","俞正声","刘云山","王岐山","张高丽","江泽民","朱镕基","邓小平","韩正","陈良宇","李鹏","胡锦涛","温家宝","文革","64","六四","学生运动","暴动","暴乱","法轮功","李洪志","十九大","天安门","台独","港独","土共","共匪","赤匪","文化大革命","政治","民主","三民主义","蛮夷","中纪委","中央","党中央","中央政治局","国家级","日本鬼子","战斗民族","国家领导人","国家主席","习大大","真主","周总理","绿茶婊","周恩来","权势狗","毛泽东","毛主席","国家领导人","国家机关","恐怖事件","屌炸天","屌丝","分裂","撕逼","武装斗争","反腐","性交","银行账号","身份证号码","电话","QQ","四人帮","装逼","草泥马","特么的","撕逼","玛拉戈壁","爆菊","JB","呆逼","本屌","齐B短裙","法克鱿","丢你老母","达菲鸡","装13","逼格","蛋疼","傻逼","你妈的","表砸","屌爆了","买了个婊","已撸","吉跋猫","妈蛋","逗比","我靠","碧莲","碧池","然并卵","日了狗","屁民","吃翔","XX狗","淫家","你妹","浮尸国","滚粗","中华民国","台湾政府","台联","台联党","荷治","大陆","大陆政府","白手套","台语","原住民","TMD");
	
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
	
	public  static List<String> getMasterWaitIncomeStatus(){
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
	public static boolean isOrderCanApplyRefund(FsOrder order, Date now){
		return order.getStatus().equals(OrderStatus.completed.getStrValue())
			&& order.getSellerFirstReplyTime()!=null && order.getCompletedTime().getTime() < now.getTime()
			&& order.getSettlementTime().getTime() > now.getTime();
	}
	
	public static boolean isOrderCanEvaluate(FsOrder order, Date now) {
		boolean isCanEvaluate = (order.getEvaluateTime() == null && 
				OrderAidUtil.getCanEvaluateStatus().contains(order.getStatus()) && 
				order.getSettlementTime().getTime() > now.getTime());
		return isCanEvaluate;
	}
	
	public static boolean isOrderCanDelete(FsOrder order, Date now) {
		return OrderAidUtil.getCanEvaluateStatus().contains(order.getStatus());
	}

	
	
	public static List<Long> getNeedSupplyOrderInfoZxCateIds(){
		return Arrays.asList(
				100000L,100001L,100016L,100002L,100017L,
				100004L,100006L,100007L,100009L,100003L,
				100010L,100005L,
				100018L,100019L,100020L,
				100021L,100022L,100023L
				);
	}
	
	
	public static boolean supplyOrderInfoParamsCheck(Long cateId , JSONArray dataList){
		logger.info("=====data list for supply info: "+ dataList.toJSONString()+"=====");
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
		// 全年运势
		mustParamsMap.put(100000l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime",
				"marriageStatus","familyRank"));
		// 婚恋感情
		mustParamsMap.put(100001l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 健康旺衰
		mustParamsMap.put(100016l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 事业财运
		mustParamsMap.put(100002l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime",
				"marriageStatus","familyRank"));
		// 学业预测
		mustParamsMap.put(100017l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));

		// 结婚吉日  list 可选 参数 englishName, expectMarriageDateBegin,expectMarriageDateEnd
		mustParamsMap.put(100004l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime","isFiancee","isFiance"));
		// 开张开市 可选参数: 公司曾用名
		mustParamsMap.put(100006l, Arrays.asList("industry","comAddress","scopeOfBusiness",
				"curComName","isSelf","realName","birthAddress","birthDate",
				"sex" ,"birthTimeType","birthTime")); 
		// 乔迁择日 list 另一半的信息  ,可选参数 expectMoveDate
		mustParamsMap.put(100007l, Arrays.asList("newAddress","completedTime",
				"isSelf","realName","birthAddress","birthDate","sex",
				"birthTimeType","birthTime","isOwner","isSpouse")
				);
		
		// 个人改名 curUsrName 当前名; 曾用名 onceName
		mustParamsMap.put(100008l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthTimeType","birthTime","marriageStatus","familyRank"));
		// 个人起名
		mustParamsMap.put(100009l, Arrays.asList("birthDate","birthTimeType","birthTime",
				"sex","familyRank"));
		
		// 八字详批
		mustParamsMap.put(100003l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime",
				"marriageStatus","familyRank"));
		// 公司起名 可选参数: 公司现用名 comNowName
		mustParamsMap.put(100010l, Arrays.asList("establishedDate","industry",
				"comAddress","scopeOfBusiness","isSelf","realName","birthDate",
				"sex","birthTimeType","birthTime"));
		// 择吉生产 fetusNum  第几胎，
		// list 可选参数:  englishName;expectTimeRange eg: 2017-01-01 08:18~2017-01-01 08:18
		mustParamsMap.put(100005l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime","fetusNum",
				"isFather","isMather"));

		// 远程公寓居家风水
		mustParamsMap.put(100018l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 远程复式、联排别墅居家风水
		mustParamsMap.put(100019l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 远程独立别墅居家风水
		mustParamsMap.put(100020l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 远程500平米以下办公风水
		mustParamsMap.put(100021l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 远程500-1000平米办公风水
		mustParamsMap.put(100022l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		// 远程1000平米以上办公风水
		mustParamsMap.put(100023l, Arrays.asList("isSelf","realName","birthDate",
				"sex","birthAddress","birthTimeType","birthTime"));
		
		List<String> mustParams = mustParamsMap.get(cateId);
		if(CollectionUtils.isEmpty(mustParams)){
			return true;
		}
		//性别 M男人;F女人;O其他
		String [] correctSex = new String[]{"M","F","O"};
		String [] correctMarriageStatus = new String[]{"single","celibate","married","divorce","widowed","remarriage"};
		int i=0;
		boolean checkRlt = false;
		for(Object obj :  dataList){
			if (cateId == 100007l && i==1) {
				break;
			}
			boolean tmpCheckRlt = true;
			JSONObject checkData = (JSONObject) obj;
			for(String param : mustParams){
				String value = checkData.getString( param);
				if(StringUtils.isEmpty(value)){
					logger.warn("参数"+param+",value:"+value+",格式错误 checkData:" +checkData+",dataList:"+dataList );
					tmpCheckRlt = tmpCheckRlt&false;
				}
				if("realName".equals(param)){
					if( StringUtils.isNotEmpty(value) &&(value.length()<2 || !CommonUtils.isChinese(value)  )){
						logger.warn("参数"+param+",value:"+value+",姓名格式错误 checkData:" +checkData+",dataList:"+dataList );
						tmpCheckRlt = tmpCheckRlt&false;
					}
				}
				else if("sex".equals(param)){
					if(    !ArrayUtils.contains(correctSex,value)    ){
						logger.warn("参数"+param+",value:"+value+",性别错误 checkData:" +checkData+",dataList:"+dataList );
						tmpCheckRlt = tmpCheckRlt&false;
					}
				}
				else if("marriageStatus".equals(param)){
					if( !ArrayUtils.contains(correctMarriageStatus,   value  )    ){
						logger.warn("参数"+param+",value:"+value+",婚姻状态 checkData:" +checkData+",dataList:"+dataList );
						tmpCheckRlt = tmpCheckRlt&false;
					}
				}
				else if("birthDate".equals(param) || "expectMoveDate".equals(param) ||  "completedTime".equals(param)){
					if(StringUtils.isEmpty(value) || ( !"未知".equals(value  )  && CommonUtils.stringToDate(value, "yyyy-MM-dd") ==null)   ){
						logger.warn("参数"+param+",value:"+value+",日期错误 checkData:" +checkData+",dataList:"+dataList );
						tmpCheckRlt = tmpCheckRlt&false;
					}
				}
				//第几胎
				//fetusNum
				else if("fetusNum".equals(param)){
					Long d = Long.valueOf(value);
					if( d<1 || d>10 ){
						logger.warn("参数"+param+",value:"+value+", 第几胎错误 checkData:" +checkData+",dataList:"+dataList );
						tmpCheckRlt = tmpCheckRlt&false;
					}
				}else{
					if( value.length()<1 ){
						logger.warn("参数"+param+",value:"+value+", 参数长度错误 checkData:" +checkData+",dataList:"+dataList );
						tmpCheckRlt = tmpCheckRlt&false;
					}
				}
			}
			
			if (cateId != 100001l && !tmpCheckRlt) {
				// if the category is not for love event, any false check can cause the final result to be false
				return false;
			} else {
				checkRlt = checkRlt|tmpCheckRlt;
			}
			i++;
		}
		logger.info("====check supply info rlt: "+Boolean.toString(checkRlt)+"====");
		if (cateId == 100001l && !checkRlt) {
			// if the category is for love, two personal data are all wrong, the final result is false
			return false;
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
//	public static long getSumAmtFromStatusAmtMap(Map<String, Long> statusTotalFeeMap , String status){
//		return  (statusTotalFeeMap!=null &&  statusTotalFeeMap.get(status) !=null ) ? statusTotalFeeMap.get(status) : 0l;
//	}
	public static HashMap<String, Long> getSumAmtFromStatusAmtMap(JSONObject statAmtMap , List<String> statusList){
		if(CollectionUtils.isEmpty(statusList) || (statAmtMap==null || statAmtMap.isEmpty() ) ){
			return null;
		}
		Long sumAmtPay = 0l;
		Long sumAmtDiscount = 0l;
		Long sumAmtDiscountPlat = 0l;
		Long sumAmtDiscountMaster = 0l;
		for(String status: statusList){
			if (!statAmtMap.containsKey(status)) {
				continue;
			}
			JSONObject amtMap = statAmtMap.getJSONObject(status);
			sumAmtPay += amtMap.getLong("sumAmtPay");
			sumAmtDiscount += amtMap.getLong("sumAmtDiscount");
			sumAmtDiscountPlat += amtMap.getLong("sumAmtDiscountPlat");
			sumAmtDiscountMaster += amtMap.getLong("sumAmtDiscountMaster");
		}
		HashMap<String, Long> result = new HashMap<>();
		result.put("sumAmtPay", sumAmtPay);
		result.put("sumAmtDiscount", sumAmtDiscount);
		result.put("sumAmtDiscountPlat", sumAmtDiscountPlat);
		result.put("sumAmtDiscountMaster", sumAmtDiscountMaster);
		return result;
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
		else if(beforeTaxIncomeRmbAmt> 4000 * 100 && beforeTaxIncomeRmbAmt <= 25000 * 100){
			long _jian = mul(beforeTaxIncomeRmbAmt, 0.2d).longValue();
			return mul(beforeTaxIncomeRmbAmt -_jian , 0.2d).longValue();
			//return Double.valueOf( ( beforeTaxIncomeRmbAmt - (0.2d *beforeTaxIncomeRmbAmt ) ) * 0.2d  ).longValue();
		}
		else if(beforeTaxIncomeRmbAmt> 25000 * 100&& beforeTaxIncomeRmbAmt <= 62500 * 100){
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
    
    public static String containForbiddenWord(String s) {
    	for (String forbidWord : forbidWords) {
			if (s.contains(forbidWord)) {
				return forbidWord;
			}
		}
    	return null;
    }
	
   public static void main(String[] args) {
	   long beforeTaxIncomeRmbAmt = 60000 * 100;
	   long tax  = calPersonalIncomeTaxRmbAmt(beforeTaxIncomeRmbAmt);
	   System.out.println( tax);
   }
}
