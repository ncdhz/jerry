package com.github.ncdhz.jerry.handler;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class ClientHandler extends SocketHandler{

    public abstract SocketChannel connectable(Selector selector, SelectionKey key);
}
