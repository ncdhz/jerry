package com.github.ncdhz.jerry.handler;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class ServerHandler extends SocketHandler{

    public abstract SocketChannel acceptable(Selector selector, ServerSocketChannel socketChannel) throws IOException;


}
