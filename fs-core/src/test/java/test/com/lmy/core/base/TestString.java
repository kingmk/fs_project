package test.com.lmy.core.base;

import java.util.Date;

import com.lmy.common.component.CommonUtils;

import junit.framework.TestCase;

public class TestString extends TestCase {

	
	public void test_test(){
		
		System.out.println( 	CommonUtils.dateToString(new Date(1491477863000l), CommonUtils.dateFormat2, "")	  );
		
	}
	
}
