package com.github.ncdhz.jerry.handler;


import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public final class JerryClientHandler extends ClientHandler{

    private ByteBuffer dataBuffer = DefaultConfig.clientDataBuffer;

    @Override
    public SocketChannel connectable(Selector selector, SelectionKey key) {

        SocketChannel serverChannel = (SocketChannel) key.channel();
        try {
            if (serverChannel.isConnectionPending()){
                serverChannel.finishConnect();
            }
            serverChannel.configureBlocking(false);
            serverChannel.register(selector,SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverChannel;
    }

    @Override
    public String readable(SelectionKey key) {
        return readData(dataBuffer, key);
    }


}
