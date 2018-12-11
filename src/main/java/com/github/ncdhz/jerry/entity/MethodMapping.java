package com.github.ncdhz.jerry.entity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于方法的映射
 */
public class MethodMapping {

    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法所在的对象
     */
    private Object object;
    /**
     * 方法
     */
    private Method method;

    /**
     * 方法的返回值  处理连接
     */
    private String responseMapping;
    /**
     * 方法的参数信息
     */
    private List<MethodTypeMapping> methodTypeMappings;

    public List<MethodTypeMapping> getMethodTypeMapping(){
        return methodTypeMappings;
    }

    public void setMethodTypeMapping(MethodTypeMapping methodTypeMapping){
        if (methodTypeMappings==null){

            methodTypeMappings = new ArrayList<>();
        }
        methodTypeMappings.add(methodTypeMapping);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getResponseMapping() {
        return responseMapping;
    }

    public void setResponseMapping(String responseMapping) {
        this.responseMapping = responseMapping;
    }
}
