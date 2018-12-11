package com.github.ncdhz.jerry.socket;

import com.github.ncdhz.jerry.entity.JerryData;
import com.github.ncdhz.jerry.entity.ResponseData;
import com.github.ncdhz.jerry.handler.JerryServerHandler;
import com.github.ncdhz.jerry.handler.ServerHandler;
import com.github.ncdhz.jerry.session.Request;
import com.github.ncdhz.jerry.session.Session;
import com.github.ncdhz.jerry.util.DataFormat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * 支持创建多台服务器
 */
class Server extends Socket implements ServerSocket {

    private Selector selector;

    private boolean close = false;

    private ServerSocketChannel serverSocketChannel;

    /**
     * 注册服务端处理程序
     */
    private static ServerHandler jerryServerHandler = new JerryServerHandler();


    private Server(){}

    /**
     * 根据端口初始化一个服务程序
     */
    private  void init(int port){
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     *
     * @param port 传入一个 端口
     * @return
     */
    static Server  initNIOServer(int port){
        Server server = new Server();
        server.init(port);
        server.start();
        return server;
    }



    public void close(){
        try {
            close = true;
            if (selector!=null)
                selector.close();
            if(selector!=null)
                serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (!close) try {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isValid() && key.isAcceptable()) {
                    jerryServerHandler.acceptable(selector, serverSocketChannel);
                }
                else if (key.isValid()&&key.isReadable()) {
                    /**
                     * 处理好接受到的数据
                     */
                    List<String> datas = jerryServerHandler.feedbackFormatting(jerryServerHandler.readable(key));

                    for (String data : datas) {
                        /**
                         * 把数据交给数据处理程序
                         */
                        new Thread(){
                            @Override
                            public void run() {
                                ResponseData responseData = dataParsingHandler.serverDataParsing(data);

                                if (responseData!=null){
                                    /**
                                     * 通过用户的sessionId 获取用户的session
                                     */
                                    String sessionId = responseData.getUserId();
                                    /**
                                     * 在没有链接完全时客户端的sessionId为空
                                     * 当sessionId为空时  将跳过 @JerryResponseMapping 注解
                                     */
                                    if (sessionId!=null){
                                        if (responseData.getAccept().equals(DataFormat.ACCEPT_ID)){
                                            SocketChannel channel = (SocketChannel) key.channel();
                                            /**
                                             * 注册session
                                             */
                                            Request.newSession(sessionId,channel);
                                            return;
                                        }
                                        Session session = Request.getSession(sessionId);
                                        if (session!=null){
                                            SocketChannel socketChannel = session.getSocketChannel();
                                            if (socketChannel!=null){
                                                Object o = responseData.getData();
                                                if (o!=null)
                                                    if (o.getClass() == JerryData.class)
                                                        write(responseData.getResponsePath(),(JerryData) o,socketChannel);
                                                    else
                                                        write(o,responseData.getResponsePath(),socketChannel);

                                            }

                                        }
                                    }
                                }
                            }
                        }.start();

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void write(Object data,String address,SocketChannel socketChannel) {
        write(address,data,true,socketChannel);
    }

    @Override
    public void write(String address,JerryData data,SocketChannel socketChannel) {
        write(address,data,false,socketChannel);
    }


    private void write(String address,Object data,boolean isObj,SocketChannel socketChannel){

        if (isObj)
            write(address,data,DataFormat.CONTENT_TYPE_JSON,DataFormat.ACCEPT_OBJ,socketChannel);
        else
            write(address,data,DataFormat.CONTENT_TYPE_JSON,DataFormat.ACCEPT_MAP,socketChannel);
    }


    private void write(String address,Object data,String contentType,String accept,SocketChannel socketChannel){
        write(data,socketChannel,address,contentType,accept,"");
    }
}