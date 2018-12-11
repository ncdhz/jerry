package com.github.ncdhz.jerry.session;

import java.nio.channels.SocketChannel;

public interface Session {

    <T> void setAttribute(String name, T value);

    <T> T getAttribute(String name);

    void removeAttribute(String name);

    String getId();

    void refresh();

    SocketChannel getSocketChannel();
}