package com.lmy.core.utils;

import java.text.DecimalFormat;
import java.util.Date;

import com.lmy.common.component.CommonUtils;
import com.lmy.common.redis.RedisClient;
import com.lmy.core.constant.CacheConstant;

public class UniqueNoUtil {

	/**
	 * @return sysDate(yyyyMMddHH)10位 + 8为数字 
	 */
	public static String getSimpleUniqueNo(){
		   String prefix = CommonUtils.dateToString(new Date(), "yyyyMMddHH", "");
		   DecimalFormat df = new DecimalFormat("00000000");
		   Long suffix = RedisClient.incrby(CacheConstant.FS_SIMPLE_UNIQUENO_SUFFIX, 1l);
		   if(FsEnvUtil.isPro()){
			   return prefix + df.format(suffix  % 10000000);
		   }else{
			   String s =  prefix + df.format(suffix  % 10000000);
			   return "10"+s.substring(2);
		   }
	   }
	public static void main(String[] args) {
		System.out.println(getSimpleUniqueNo());
	}
}
