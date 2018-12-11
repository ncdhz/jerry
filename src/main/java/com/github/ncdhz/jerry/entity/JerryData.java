package com.github.ncdhz.jerry.entity;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 传递键值对数据
 */
public class JerryData {
    
    private Map<String,DataType> dataMap = new ConcurrentHashMap<>();

    public  <T> void put(String name,T data){
        DataType<T> d = new DataType<>();
        d.setData(data);
        dataMap.put(name,d);
    }

    public <T> T get(String name){
        DataType dataType = dataMap.get(name);
        if (dataType==null)
            return null;
        return (T) dataType.getData();
    }

    public Map getDataMap(){
        return dataMap;
    }

    public void setDataMap(Map<String, DataType> dataMap) {
        this.dataMap = dataMap;
    }
}


