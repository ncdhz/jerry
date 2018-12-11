package com.github.ncdhz.jerry.socket;

import com.github.ncdhz.jerry.entity.JerryData;

public interface ClientSocket{

    void write(Object object, String address);

    void write(String address, JerryData object);

    /**
     * 关闭客户端
     */
    void close();
}
