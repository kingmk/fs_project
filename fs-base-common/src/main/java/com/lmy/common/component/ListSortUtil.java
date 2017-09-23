package com.lmy.common.component;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
/** 
 *  List按照指定字段排序工具类 
 * 
 * @param <T> 
 */  
  
public class ListSortUtil {  
	private static final Logger logger = Logger.getLogger(ListSortUtil.class);
	
	public static enum SortMode{
		DESC,
		ASC
	}
    /** 
     * @param targetList 目标排序List 
     * @param sortField 排序字段(实体类属性名) 
     * @param sortMode 排序方式（asc or  desc） 
     */  
	
	static int getRetVal(SortMode sortMode ,java.util.Date begin ,java.util.Date end){
		int result = 0;
		if (sortMode != null &&  SortMode.DESC.equals(sortMode)) {  
			result = 	 begin.after(end) ? -1 : 1;
        } else {  
        	result = 	 begin.after(end) ? 1 : -1;
        }  
		return result;
	}
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sort(List targetList, final String sortField, final SortMode sortMode) {  
        Collections.sort(targetList, new Comparator() {  
            public int compare(Object obj1, Object obj2) {   
                int retVal = 0;  
                try {  
                    //首字母转大写  
                    String newStr=sortField.substring(0, 1).toUpperCase()+sortField.replaceFirst("\\w","");   
                    String methodStr="get"+newStr;  
                      
                    Method method1 = (obj1).getClass().getMethod(methodStr);  
                    Method method2 = (obj2).getClass().getMethod(methodStr);  
                    Object  robj1 = method1.invoke(obj1);
                    Object  robj2  =  method2.invoke(obj2);
                    
                    if( robj1 instanceof java.util.Date ){
                    	java.util.Date begin = (java.util.Date) robj1;  
                    	java.util.Date end = (java.util.Date) robj2;  
                        return getRetVal(sortMode, begin, end);
                    }
                    if (sortMode != null &&  SortMode.DESC.equals(sortMode)) {  
                        retVal = robj2.toString().compareTo(robj1.toString()); // 倒序  
                    } else {  
                        retVal = robj1.toString().compareTo(robj2.toString()); // 正序  
                    }  
                } catch (Exception e) {  
                	logger.error("", e);
                }  
                return retVal;  
            }  
        });  
    }  
}  