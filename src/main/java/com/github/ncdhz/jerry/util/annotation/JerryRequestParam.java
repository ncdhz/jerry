package com.github.ncdhz.jerry.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明参数的名字 可以给定默认值
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JerryRequestParam {

    String name();

    String defaultValue() default "";
}
