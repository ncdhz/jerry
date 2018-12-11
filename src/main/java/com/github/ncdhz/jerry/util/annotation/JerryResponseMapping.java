package com.github.ncdhz.jerry.util.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解为响应路径注解
 * 请求路径会根据此注解定位
 * 只能在服务端使用 客户端不支持使用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JerryResponseMapping {
    String value();
}
