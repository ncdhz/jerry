package com.github.ncdhz.jerry.socket;


import com.github.ncdhz.jerry.entity.JerryData;

import java.nio.channels.SocketChannel;

public interface ServerSocket {
    /**
     * 关闭服务端
     */
    void close();
    /**
     * 写数据给 socketChannel 这个客户端
     */
    void write(Object data, String address, SocketChannel socketChannel);


     void write(String address, JerryData data, SocketChannel socketChannel);
}
