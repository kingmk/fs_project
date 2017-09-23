package com.lmy.common.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public  class BizException extends RuntimeException {
	/**
	 */
	private static final long serialVersionUID = -1708643103932249972L;

	protected final Log logger = LogFactory.getLog(this.getClass());

	/** 异常信息 */
	private String msg;

	private String errorCode;
	
	private String errorMsg;

	/**
	 * 异常处理构造函数
	 * 
	 * @param errorCode 异常代码
	 * @param params 异常信息
	 * @param cause 异常
	 */
	public BizException(String errorCode, String msg, Throwable cause)
	{
		super(cause);
		this.errorCode = errorCode;
		this.msg = msg;
	}

	/**
	 * 异常处理构造函数
	 * 
	 * @param errorCode 异常代码，自己到message.properties去看
	 * @param params 异常信息参数，自己到message.properties去看
	 */
	public BizException(String errorCode, String  msg)
	{
		this.msg = msg;
		this.errorCode = errorCode;
	}

	/**
	 * 异常处理构造函数
	 * 
	 * @param errorCode 异常代码，自己到message.properties去看
	 * @param params 异常信息参数，自己到message.properties去看
	 */
	public BizException(String errorCode, String  msg,String errorMsg)
	{
		this.msg = msg;
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	/**
	 * 业务异常处理构造函数
	 * 
	 * @param cause 异常
	 */
	public BizException(String errorMsg)
	{
		this.errorMsg = errorMsg;
	}

	
	/**
	 * 业务异常处理构造函数
	 * 
	 * @param cause 异常
	 */
	public BizException(Throwable cause)
	{
		super(cause);
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getErrorCode()
	{
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
