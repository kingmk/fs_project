package com.lmy.common.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 防双(暴)击 重复提交
 * @author fidel
 * @since 2017/06/02 23:56
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventDoubleClickAnnotation {
	int intervalSec() default 5;
}
