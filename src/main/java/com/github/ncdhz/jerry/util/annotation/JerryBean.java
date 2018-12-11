package com.github.ncdhz.jerry.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 带有此注解的类会默认被构造  必须有能运行无惨构造函数 函数必须为public
 * 带有此注解的方法会默认被执行 并且方法必须没有参数
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JerryBean {
}
