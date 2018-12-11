package com.github.ncdhz.jerry.socket;

import com.github.ncdhz.jerry.exception.PortOccupiedException;

public interface SocketFactory {
    /**
     * 用系统默认的端口提供服务
     * @return 返回一个服务
     * @throws PortOccupiedException
     */
    ServerSocket getServer() throws PortOccupiedException;
    /**
     * 可以自定义端口
     * @param port 端口
     * @return 返回一个服务
     * @throws PortOccupiedException
     */
    ServerSocket getServer(int port) throws PortOccupiedException;
    /**
     * 用系统默认的端口 返回一个客户端
     * @return 返回一个客户端
     */
    ClientSocket getClient();
    /**
     * 自定义端口返回一个客户服务端地址为默认地址
     * @param port 端口
     * @return 返回一个客户
     */
    ClientSocket getClient(int port);
    /**
     * 自定义ip 端口为系统底层端口
     * @param host 服务端的地址
     * @return 返回一个客户
     */
    ClientSocket getClient(String host);
    /**
     * 自定义端口和服务端地址
     * @param hostname 服务端的地址
     * @param port 端口
     * @return 返回一个客户
     */
    ClientSocket getClient(String hostname, int port);
}