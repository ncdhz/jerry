package com.github.ncdhz.jerry.socket;


import com.github.ncdhz.jerry.entity.JerryData;
import com.github.ncdhz.jerry.handler.ClientHandler;
import com.github.ncdhz.jerry.handler.JerryClientHandler;
import com.github.ncdhz.jerry.util.DataFormat;
import com.github.ncdhz.jerry.util.RandomUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

class Client extends Socket implements ClientSocket{

    private SocketChannel socketChannel;

    private Selector selector;

    private boolean close = false;

    private Client(){}

    private String sessionId;
    /**
     * 加载默认客户端处理程序
     */
    private static ClientHandler jerryClientHandler =  new JerryClientHandler();


    private boolean init(String hostname,int port) throws IOException{
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(hostname,port));
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        /**
         * 注册客户端处理程序
         */

        return true;
    }
    static Client initNIOClient(String hostname, int port){
        Client client = new Client();
            try {
                client.init(hostname,port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        client.start();
        return client;
    }

    public void close(){
        try {
            close = true;
            if (socketChannel!=null)
                socketChannel.close();
            if (selector!=null)
                selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!close){
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isConnectable()){
                        SocketChannel connectable = jerryClientHandler.connectable(selector, key);
                        /**
                         * 客户端连接成功把sessionId发给服务端
                         */
                        writeSessionId(connectable);
                    }
                    else if(key.isValid()&&key.isReadable()){
                        /**
                         * 处理好接受到的数据
                         */
                        List<String> datas = jerryClientHandler.feedbackFormatting(jerryClientHandler.readable(key));
                        for (String data : datas) {
                            /**
                             * 把数据交给数据处理程序
                             */
                            new Thread(){
                                @Override
                                public void run() {
                                    dataParsingHandler.clientDataParsing(data);
                                }
                            }.start();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void write(Object data,String address) {
        write(address,data,true);
    }

    @Override
    public void write(String address,JerryData data) {
        write(address,data,false);
    }

    private void write(String address,Object data,boolean isObj){

        if (isObj)
            write(address,data,DataFormat.CONTENT_TYPE_JSON,DataFormat.ACCEPT_OBJ);
        else
            write(address,data,DataFormat.CONTENT_TYPE_JSON,DataFormat.ACCEPT_MAP);
    }

    private String writeSessionId(SocketChannel socketChannel){

        if (sessionId==null)
            sessionId = RandomUtil.getCurrentTimeMillis()+RandomUtil.getRandomNum(7);
        write(sessionId,socketChannel,"", DataFormat.CONTENT_TYPE_JSON,DataFormat.ACCEPT_ID,sessionId);
        return sessionId;
    }

    private void write(String address,Object data,String contentType,String accept){
        write(data,socketChannel,address,contentType,accept,sessionId);
    }
}
