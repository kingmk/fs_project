package com.lmy.common.redis;
/**
 * 自定义缓存异常
 * @author houzhanshan
 *
 */
public class CacheExcetpion extends Exception {
	/**
	 * 
	 */ 
	private  static final long serialVersionUID = 1L;

	public CacheExcetpion(String message) {
		super(message);
	}

	public CacheExcetpion(String message, Throwable cause) {
		super(message, cause);
	}

	public CacheExcetpion(Throwable cause) {
		super(cause);
	}

	
}
