package com.github.ncdhz.jerry.handler;


import com.github.ncdhz.jerry.util.config.DefaultConfig;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public final class JerryServerHandler extends ServerHandler{
    /**
     * 服务端二进制缓冲
     */
    private static ByteBuffer dataBuffer = DefaultConfig.serverDataBuffer;

    public String readable(SelectionKey key){
        return readData(dataBuffer, key);
    }


    public SocketChannel acceptable(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        /**
         * 注册连接 并监听数据读取
         */
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        return clientChannel;
    }
}
