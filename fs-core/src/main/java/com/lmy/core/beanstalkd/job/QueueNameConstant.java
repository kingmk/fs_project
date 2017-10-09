package com.lmy.core.beanstalkd.job;

public class QueueNameConstant {
	public static final String QUEUE_WXPAY_CONFIRM = "weiXinPayConfrim";
	/**新的聊天消息 master or buyUsr  未读确认 **/
	public static final String QUEUE_ORDER_CHAT = "QUEUE_ORDER_CHAT";
	/**订单相关的消息处理**/
	public static final String QUEUE_ORDER = "QUEUE_ORDER";
	
	/**用户端 新订单 master 超过24小时未回复 系统自动退款 -- 需要整合到QUEUE_ORDER中**/
	public static final String masterNoReply24HoursAutoRefund = "masterNoReply24HoursAutoRefund";
	
	/**检查用户订单资料是否填写**/
	public static final String MSG_ORDER_INFO_CHECK = "MSG_ORDER_INFO_CHECK";
	/**检查老师是否已结单**/
	public static final String MSG_ORDER_BEGIN_CHECK = "MSG_ORDER_BEGIN_CHECK";

	/**老师回复用户后，立即通知用户**/
	public static final String MSG_ORDER_USER_NOTIFY = "MSG_ORDER_USER_NOTIFY";
	/**用户回复老师后，立即通知老师**/
	public static final String MSG_ORDER_MASTER_NOTIFY = "MSG_ORDER_MASTER_NOTIFY";
	
	/**检查老师回复用户后，用户是否已读**/
	public static final String MSG_ORDER_USER_UNREAD_CHECK = "MSG_ORDER_USER_UNREAD_CHECK";
	/**检查用户回复老师后，老师是否已读**/
	public static final String MSG_ORDER_MASTER_UNREAD_CHECK = "MSG_ORDER_MASTER_UNREAD_CHECK";
}
