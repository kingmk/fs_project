package com.lmy.core.utils;

import com.lmy.common.utils.ResourceUtils;

public class FsEnvUtil {
		//#dev,test,pro
		private static String env = ResourceUtils.getValue("lmy-core","sys.env");
		/**
		 * 是否是开发环境	
		 * @return
		 */
		public static boolean  isDev(){
			return "dev".equals(env) ? true :false;
		}
		
		public static String  getEnv(){
			return env;
		}
		/**
		 * 是否是测试环境	
		 * @return
		 */
		public static boolean  isTest(){
			return "test".equals(env) ? true :false;
		}
		/**
		 * 是否是生产环境	
		 * @return
		 */
		public static boolean  isPro(){
			return "pro".equals(env) ? true :false;
		}
}
