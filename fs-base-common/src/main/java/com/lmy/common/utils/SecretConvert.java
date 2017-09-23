package com.lmy.common.utils;

import org.apache.commons.lang.StringUtils;

/**
 * 私密数据 处理 打*
 * @author fidel
 * @since 2015-11-17
 */
public class SecretConvert {
	
	public static String convertUserName(String userName){
		if( StringUtils.isEmpty(userName) ){
			return userName;
		}else{
			return  "*"+userName.substring(1);
		}
	}
	
	public static String convertMobile(String mobile){
		if( StringUtils.isEmpty(mobile) ||  mobile.length()<7){
			return mobile;
		}else{
			return mobile.substring(0, 3)+"****"  +mobile.substring(7);
		}
	}
	
	public static String convertEmail(String email){
		if( StringUtils.isEmpty(email) ){
			return email;
		}else{
			return email.substring(0, 3)+"****"  +email.substring(email.lastIndexOf('@'));
		}
	}
	
	public static String convertCertNo(String certNo){
		if( StringUtils.isEmpty(certNo) ){
			return certNo;
		}else{
			return certNo.substring(0, 6)+"********"  +certNo.substring(certNo.length() - 4);
		}
	}
	
	public static String convertCardNum(String cardNum){
		if( StringUtils.isEmpty(cardNum) ){
			return cardNum;
		}else{
			return cardNum.substring(0, 6)+"******"  +cardNum.substring(12);
		}
	}
}
