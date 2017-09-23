package com.lmy.core.beanstalkd.job;

public class QueueNameConstant {
	public static final String weiXinPayConfrim = "weiXinPayConfrim";
	/**老师端 有新订单消息 **/
	public static final String NEWORDER_MASTER_CONFIRM = "NEWORDER_MASTER_CONFIRM";
	/**新的聊天消息 master or buyUsr  未读确认 **/
	public static final String ORDER_CHAT_NEWMSG_UNREADCONFIRM = "ORDER_CHAT_NEWMSG_UNREADCONFIRM";
	/**用户端 新订单 master 超过24小时未回复 系统自动退款 **/
	public static final String masterNoReply24HoursAutoRefund = "masterNoReply24HoursAutoRefund";
}
