package com.github.ncdhz.jerry.socket;

import com.alibaba.fastjson.JSON;
import com.github.ncdhz.jerry.entity.Jerry;
import com.github.ncdhz.jerry.handler.DataParsingHandler;
import com.github.ncdhz.jerry.handler.JerryDataParsingHandler;
import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetEncoder;
import java.util.Date;

abstract class Socket extends Thread{


    static DataParsingHandler dataParsingHandler = new JerryDataParsingHandler();
    /**
     * 编码器
     */
    CharsetEncoder encoder = DefaultConfig.encoder;

    void write(Jerry jerry, SocketChannel socketChannel) throws IOException {
        String data = "$$"+JSON.toJSONString(jerry)+"##";

        ByteBuffer encodeData = encoder.encode(CharBuffer.wrap(data.toCharArray()));
        socketChannel.write(encodeData);
    }

    void write(Object data,SocketChannel socketChannel,String address,String contentType,String accept,String sessionId){
        try {
            String d = JSON.toJSONString(data);
            SocketAddress localAddress = null;
            try {
                localAddress= socketChannel.getLocalAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Jerry jerry = new Jerry();
            if (localAddress!=null){
                jerry.setHost(localAddress.toString());
            }
            jerry.setSessionId(sessionId);
            jerry.setData(d);
            jerry.setAddress(address);
            jerry.setDate(new Date());
            jerry.setContentType(contentType);
            jerry.setAccept(accept);
            write(jerry,socketChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
