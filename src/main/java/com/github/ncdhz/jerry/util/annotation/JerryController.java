package com.github.ncdhz.jerry.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 带有此注解的类
 * 里面的所有JerryRequestMapping JerryRequestParam JerryResponseMapping才会被解释
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JerryController {
}
