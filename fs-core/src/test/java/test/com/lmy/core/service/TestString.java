package test.com.lmy.core.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmy.common.component.CommonUtils;
import com.lmy.common.component.JsonUtils;
import com.lmy.common.utils.HexUtil;
import com.lmy.common.utils.RSAHelper;
import com.lmy.core.service.impl.OrderAidUtil;

public class TestString extends TestCase{

	public void test_test1(){
		Date date1 = CommonUtils.stringToDate("2015-12-11", CommonUtils.dateFormat1);
		Date date2 = CommonUtils.stringToDate("2015-12-12", CommonUtils.dateFormat1);
		System.out.println( date2.after(date1) );
	}
	
	public void test_test2(){
		 JSONObject result = JsonUtils.commonJsonReturn() ;
		//JsonUtils.setBody(result, "unReadNum", 5);
		System.out.println(result);
	}
	
	public void test_test3(){
		
		System.out.println( System.currentTimeMillis() );
	}
	
	
	
	public void test_test4(){
		Long dd =350l;
		Double d = Double.valueOf(dd* OrderAidUtil.getPlatCommissionRate() );
		
		System.out.println(  d);
		System.out.println(  d.longValue());
		
		System.out.println(OrderAidUtil.mul(dd, OrderAidUtil.getPlatCommissionRate()));
		
		
	}
	
	
	
	public void test_str() throws UnsupportedEncodingException{
		String priRsaKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCm+2O/f5C2ko3YvlFUwIVM88Q6VXPPpuElzuqudeSw6pq54s3gC8FDMfprwqm0LlDObi7ZliYJQkZZNNxHcP8IPT+ehuVRkjwhwzWBIEnJsprKxH/2JBF4x+25scjM2acv5ZT9ta7tf3qdd6Y7njuk8Nmj4VHerCZdpVqWXydfah7gFR7JTCiapU2N+TJP8vpzOWTjZopCFjiILksqtFH8ltMff6mpBgoEaLdwfLm4zFkgTvuJbghZRt5Qrqry6kDLh9yBZVl9iODkqOtFOsdCGtW7DSaM7sKgfDTTLoGL5jqbpOe+wNYuPdHZbS723X8zukeHcOUcETYwPCcohLTPAgMBAAECggEATuFskncBdq2uolftdbrofZyd8uyRI+PycfjYNgftO4HoC63PkWocJtECOkuf/UTv2USFjzX1y+Pp9ZQaBwmjAFJmrk2NIMaS72QW72PAV66unuDKjg0lz8eqTlFkyRh8eQzcICnmc70HwEFYXst144n/VgJHuYfbK9vD+ksC/3gPQQhAT1QVzYp1d5tb+TuH2EVk/L4U7RkXMkS7dnC+GKxnVCJQSghUBtsFDq/QZtKBIQExdocVGKyrTActDRlIOA6e7QT8Yk7Lyqdbt4D43kp4EHbXrNVAGQ22o2fT65MSaUtT94wjkHEyff3pmWdNL+ckm24gBMX7jToOK3vguQKBgQDd0KB9iXylh0JAbxYqSaqE9BN+JR1n5+qZVQZ5xk4SyjoRhl6AIVSBPcVkiC0vgsqX0hutHMWudfEY+3HwzvXZiSrSPH+WlH7aIEHXXEAdMk1UYTbZkuPiH4AcUFHA+HZgncqavfNUrLsnDrRsqEtleG7yJHzSHmT6vjf6SGHVYwKBgQDAt2lOibUXapsuYMaSqn4y1WiFxYdcdP6xpzyCWwujlDarUSi7rolBZ5f83vWjecwByowHY0SOP9WCtk2YYDdniZ5mAAiz7KjU7zoAGUOxa0FPSF2QWNKIVlW1nwfsCNLfeI8ASMzRkeByScnBVBycEv4Yy3nOFg7ctsa7vA7kpQKBgHvZMMLnMSF7FBLF86pI11zEqt2T+4c4hlR2lNcJUi5Lm4CNddY0xeojg0NOhWwxqsx6E9nkZruyPpukSUobREnhvHcGMHbzEqDXwettJp8mpuamIOn6iiKHVBB7CCqmj/ICKE2eIW71wslb/IFutipBxDCPDuKC9f7Klpa4M2fhAoGBAIKFNdstROrhFsyoWpTGx8Xh53KCP5UM39quKzsWMqHNJeGNjArgwLD9WmC3GKJpQRQNfB0czDeTYCWxFoiW0a9b812dtEc3h3j/tMaQVdp5i7gIiOXUYnJYFqB1XsYri7YyTpegtqdRJzQAaZZ4QxAphNKCLLK5GcO+PoazbVrJAoGAbu+AoN8rKxZ/AeThZrOpoYiazzXLebw14iXF8Hk4z02xkN53ES4B+wA9o+tusUzUaLnuQVeSjfELNxGE2720URiN/m0OwvB7JxPMpiz3zz/dXV1faalw1GuYBNPZtyfVmNBqBkKzrUglGR6OzgIcfCb4+VIayP++kEoj5zgs1lk=";
		String pubRsaKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApvtjv3+QtpKN2L5RVMCFTPPEOlVzz6bhJc7qrnXksOqaueLN4AvBQzH6a8KptC5Qzm4u2ZYmCUJGWTTcR3D/CD0/noblUZI8IcM1gSBJybKaysR/9iQReMftubHIzNmnL+WU/bWu7X96nXemO547pPDZo+FR3qwmXaVall8nX2oe4BUeyUwomqVNjfkyT/L6czlk42aKQhY4iC5LKrRR/JbTH3+pqQYKBGi3cHy5uMxZIE77iW4IWUbeUK6q8upAy4fcgWVZfYjg5KjrRTrHQhrVuw0mjO7CoHw00y6Bi+Y6m6TnvsDWLj3R2W0u9t1/M7pHh3DlHBE2MDwnKIS0zwIDAQAB";
		
		String minwen = "GOw0bICmUAcTVn50pSWYvCKyxhJ2lzMA/apKWbqZidOGUSedKNsC/keKwx93iFo/E3P2VcrwFk Aabx65IoQ/i40QYCsq a EQPJP/DPzv/ECuRzgmnJ8O19eGje6m9wsPcNVhBXgxutFR8nDv1eBCe/EQ7yHvgZaSKwBvxO5BmNG1qKo5GE4sx old/aFLRyCekzkxo59D VEn11nIOPOkW/M7bsVTfoYuyFTg0viJ3s8Yh1OwDlZB3qQJ8OzBStqKwMftP7ssqRPyEGTuKEcoeqlwFxseLy2tcXENTvzzuDNgs6Kj6WsI7QpIz9e J7Ca9ubYn3HdgCBlxANGAOw==";
		
		String testPlaint = "1234" ;
		
		
		RSAHelper rsa = new RSAHelper(priRsaKey, pubRsaKey, 2048);
		//解密
		//String dataPlaint =  rsa.decrypt(minwen ,"utf-8" );
		//System.out.println( dataPlaint  );
		
		String testMiwen = rsa.encrypt(testPlaint, "utf-8") ;

		System.out.println(testMiwen);
		
		System.out.println( rsa.decrypt(testMiwen, "utf-8") );
		
	}
	
	public void test_str2(){
		System.out.println( java.net.URLEncoder.encode("/order/chat_index?chatSessionNo=201704161800000120&orderId=100039") );
	}
	
	public void test_str3() throws InterruptedException{
		//AlidayuSmsFacadeImpl.alidayuSmsSend(null, "18221360028", "SMS_69740105" , null);
		
		/*JSONObject smsParamJson = new JSONObject();
		smsParamJson.put("reason","就是不通过测试");
		smsParamJson.put("product","雷门易");
		AlidayuSmsFacadeImpl.alidayuSmsSend(smsParamJson, "18221360028", "SMS_69925055" , null);
		Thread.sleep(10);*/
	}
	
	public void test_str4(){
		//System.out.println(HexUtil.toHexString( "ddfjsjfsrenjsf1212f454f5sf785713fsfslfjsjsef".getBytes()));
		
		JSONArray ja = new JSONArray();
		JSONObject dataOne = JSONObject.parseObject( "{\"memberNo\":\"5545\",\"mobile\":\"137079122154\",\"email\":\"test@163.com\",\"certType\":\"0\",\"certNo\":\"360101198001010017\",\"name\":\"张三\",\"comName\":\"易福励技术有限公司\",\"comNo\":\"6589\",\"headImg\":\"https://test.com/head.png\"}" )
		;
		ja.add(dataOne);
		System.out.println( ja  );
	}
	
	
	public void test_str5() throws NoSuchAlgorithmException{
		String certType = "0";
		String certNo = "36010119800101001x";
		String name = "张三";
		String hashMiwen = 	getHashMiWen(certType, StringUtils.isNotBlank(certNo)?certNo.toUpperCase() : certNo, name);
		System.out.println("hashMiwen:"+hashMiwen  );
	}
	
	private String getHashMiWen(String certType ,  String certNo ,String name ) throws NoSuchAlgorithmException{
		String salt = calSalt(certNo);
		System.out.println("salt:"+salt );
		String plaintext = certType + certNo +  name+salt;
		System.out.println("plaintext:"+ plaintext);
		MessageDigest    md = MessageDigest.getInstance("SHA-256");
		md.update(plaintext.getBytes());
		return HexUtil.toHexString(md.digest());
	}
	
	
	
	private String calSalt(String certNo){
		if(StringUtils.isNotEmpty(certNo)){
			if(certNo.length()>3){
				return certNo.substring(certNo.length() - 4);
			}else{
				return certNo;
			}
		}
		return "";
	}
	/** 
	 * 八月核销数据.
	 select e.id,e.wf_order_id,e.sn_code,e.channel_name,e.wf_mer_id,e.mer_name,e.veri_time,e.is_refund,e.refund_time,e.ecoupon_type,e.status,e.expiry_end_date,
o.sale_price,o.create_time,o.pay_rmb_amt,  o.brand_name,o.pro_name,o.buy_num,o.has_refund,o.wf_usr_id 
from wf_ecoupon e, wf_order o
where e.wf_order_id = o.id 
and e.status='USED'
and e.veri_time>'2017-08-01 00:00:00' and e.veri_time < '2017-08-31 23:59:59'
order by e.wf_order_id asc
;
	  
	  
	 */
	
	
	
	public void test_coupon() throws Exception{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(new File("C:/Users/Administrator/Desktop/bug截图/201708_fyw_veri.txt")));
		String _str = null;
		while(  (_str = br.readLine()) !=null ){
			sb.append(_str);
		}
		JSONObject result = JSONObject.parseObject( sb.toString() );
		JSONArray ja = result.getJSONObject("body").getJSONArray("list");
		Date now = new Date();
		StringBuffer sbw = new StringBuffer();
		sbw.append(getFirstStr() );
		for(int i=0;i<ja.size();i++){
			JSONObject dataOne = ja.getJSONObject(i);
			sbw.append( getStr(now ,dataOne) );
		}
		BufferedWriter bw = new BufferedWriter( new FileWriter(new File("C:/Users/Administrator/Desktop/bug截图/201708_fyw_veri.csv")));
		bw.write( sbw.toString()  );
		br.close();
		bw.close();
	}
	
	
	private String getFirstStr(){
		StringBuffer sbw = new StringBuffer();
		sbw.append("品牌");
		sbw.append(",");
		
		sbw.append("商品");
		sbw.append(",");

		sbw.append("渠道名称");
		sbw.append(",");
		
		sbw.append("券号");
		sbw.append(",");
		
		
		sbw.append("核销状态");
		sbw.append(",");
		
		sbw.append("核销时间");
		sbw.append(",");
		
		sbw.append("码券类型");
		sbw.append(",");

		sbw.append("是否有退款");
		sbw.append(",");
		
		
		sbw.append("券有效截止日期");
		sbw.append(",");

		sbw.append("公司名称");
		sbw.append(",");

		sbw.append("销售价");
		sbw.append(",");

		sbw.append("兑换时间");
		sbw.append(",");
		
		sbw.append("RMB价格");
		sbw.append("\n");
		
		return sbw.toString();
	}
	
	
	private String getStr( Date now , JSONObject data){
		StringBuffer sbw = new StringBuffer();
		sbw.append( StringUtils.isNotEmpty(data.getString("brand_name")) ?data.getString("brand_name") :""  );
		sbw.append(",");
		
		sbw.append( StringUtils.isNotEmpty(data.getString("pro_name")) ?data.getString("pro_name") :""  );
		sbw.append(",");

		sbw.append( StringUtils.isNotEmpty(data.getString("channel_name")) ?data.getString("channel_name") :""  );
		sbw.append(",");
		
		sbw.append( StringUtils.isNotEmpty(data.getString("sn_code")) ?"\t"+data.getString("sn_code") :""  );
		sbw.append(",");
		
		Date expiry_end_date = data.getDate("expiry_end_date");
	
		if("NOT_USED".equals(data.getString("status")) &&   now.after(expiry_end_date)){
			data.put("status", "EXPIRED");
		}
		
		sbw.append( StringUtils.isNotEmpty(data.getString("status")) ?data.getString("status") :""  );
		sbw.append(",");
		
		sbw.append( StringUtils.isNotEmpty(data.getString("veri_time")) ?data.getString("veri_time") :""  );
		sbw.append(",");
		
		sbw.append( StringUtils.isNotEmpty(data.getString("ecoupon_type")) ?data.getString("ecoupon_type") :""  );
		sbw.append(",");

		sbw.append( StringUtils.isNotEmpty(data.getString("has_refund")) ?data.getString("has_refund") :""  );
		sbw.append(",");
		
		
		sbw.append( StringUtils.isNotEmpty(data.getString("expiry_end_date")) ?data.getString("expiry_end_date") :""  );
		sbw.append(",");

		sbw.append( StringUtils.isNotEmpty(data.getString("mer_name")) ?data.getString("mer_name") :""  );
		sbw.append(",");

		sbw.append( StringUtils.isNotEmpty(data.getString("sale_price")) ?data.getString("sale_price") :""  );
		sbw.append(",");

		sbw.append( StringUtils.isNotEmpty(data.getString("create_time")) ?data.getString("create_time") :""  );
		sbw.append(",");
		
		sbw.append( StringUtils.isNotEmpty(data.getString("pay_rmb_amt")) ?data.getString("pay_rmb_amt") :"0"  );
		sbw.append("\n");
		
		return sbw.toString();
	}
	
	
	
}
