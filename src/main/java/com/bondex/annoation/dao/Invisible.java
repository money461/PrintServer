package com.bondex.annoation.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
/**
 * @author Allen
 * @date 2018/3/9
 *
 *  自定义的注解,用于排除多余的变量（自定义注解，过滤多余字段）
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Invisible {
	
}
