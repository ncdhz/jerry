package com.github.ncdhz.jerry.entity;

/**
 * 方法的属性
 * 属性名
 * 和默认值
 */
public class MethodTypeMapping {

    private Class methodType;

    private String methodTypeName;

    private String methodTypeDefault;

    public String getMethodTypeDefault() {
        return methodTypeDefault;
    }

    public void setMethodTypeDefault(String methodTypeDefault) {
        this.methodTypeDefault = methodTypeDefault;
    }

    public String getMethodTypeName() {
        return methodTypeName;
    }

    public void setMethodTypeName(String methodTypeName) {
        this.methodTypeName = methodTypeName;
    }

    public Class getMethodType() {
        return methodType;
    }

    public void setMethodType(Class methodType) {
        this.methodType = methodType;
    }

}
