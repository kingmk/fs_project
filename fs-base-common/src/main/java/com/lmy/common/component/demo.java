package com.lmy.common.component;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 本工具原本是用于安卓日历小程序的，现在工程暂时搁置了，就把这个生成八字的方法发出来。
 * 测试类
 *
 */
public class demo {
	
	public static void main(String[] args) throws Exception {
		//创建一个java.util.Date 对象，并设置好他的时间。。。
		Date date = new Date();
		//创建格式化类，设定格式为 yyyy-MM-dd HH:mm:ss
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//将date类的时间赋上具体值
		date = sdf.parse("1970-01-01 15:59:59");
		
		
		//新建一个bazi对象，传 入Date参数
		Bazi test = new Bazi(date);
		
		//根据需要输出相对应的值 如下方法输出的是 String 类型完整八字
		System.out.print(test.getbazi());
	}
	
}
