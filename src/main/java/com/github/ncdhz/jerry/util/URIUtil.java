package com.github.ncdhz.jerry.util;

public class URIUtil {
    /**
     * 规范请求地址
     * @param value 请求地址
     * @return 规范后的请求地址
     */
    public static String getStandardPath(String value){
        if(value.endsWith("/")&&value.length()>1){
            value = value.substring(0,value.length()-1);
        }
        if (!"".endsWith(value)&&!value.startsWith("/")){
            value = "/"+value;
        }
        return value;
    }
}
