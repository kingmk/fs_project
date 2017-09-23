package com.lmy.core.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/**
 * 
 * @author fidel
 * @since 2017/05/09
 */
public class FsExecutorUtil {
	private static  Executor  executor = Executors.newScheduledThreadPool(512);
	public static Executor getExecutor(){
		return executor;
	}
	public static ScheduledExecutorService getSchExecutor(){
		return (ScheduledExecutorService)executor;
	}
	
}
