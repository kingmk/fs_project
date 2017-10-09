package com.lmy.core.constant;

public class CacheConstant {
	public static final String FS_WEIXIN_INFO1_HKEY = "fs_weixin_info1_hkey";
	public static final String FS_LOGIN = "fs_login";
	public static final String FS_SIMPLE_UNIQUENO_SUFFIX = "FS_SIMPLE_UNIQUENO_SUFFIX";
	public static final String FS_USR_REG_MOBILE_SEDN_CODE_RESULT = "FS_USR_REG_MOBILE_SEDN_CODE_RESULT";
	public static final String FS_USR_REG_MOBILE_CHECK_CODE_RESULT = "FS_USR_REG_MOBILE_CHECK_CODE_RESULT";
	public static final String FS_WEIXIN_ACCESS_TOKEN1 = "FS_WEIXIN_ACCESS_TOKEN1";
	/** 客户发送消息后如果10分钟内未读，则向老师推送 某一订单第一条推送记录**/
	public static final String ORDER_CHAT_MASTER_UNREAD_LAST_PUSH = "ORDER_CHAT_MASTER_UNREAD_LAST_PUSH_NEW";
	/** 老师某次回复后，如果半小时内该用户没有再访问该订单，将会收到“老师回复了”**/
	public static final String ORDER_CHAT_BUYUSR_UNREAD_LAST_PUSH = "ORDER_CHAT_BUYUSR_UNREAD_LAST_PUSH";
	/** 老师第一次回复   保留 7天 **/
	public static final String ORDER_CHAT_MASTER_FIRST_REPLY = "ORDER_CHAT_MASTER_FIRST_REPLY";
	public static final String AUTO_JOB= "AUTO_JOB";
	/** web 防暴击key 默认保留5秒**/
	public static final String WEBCONTROLLER_PREVENT_DOUBLECLICK= "webcontroller_prevent_doubleclick";
	
	public static final String FS_SEARCH_MASTER = "FS_SEARCH_MASTER";
	
}
