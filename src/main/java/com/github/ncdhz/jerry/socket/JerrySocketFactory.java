package com.github.ncdhz.jerry.socket;

import com.github.ncdhz.jerry.exception.PortOccupiedException;
import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.util.Set;
import java.util.TreeSet;

abstract class JerrySocketFactory implements SocketFactory {

    /**
     * 服务器开的端口
     */
    private static Set<Integer> PORTS = new TreeSet<>();

    private Integer port = DefaultConfig.PORT;

    private String hostname = DefaultConfig.HOSTNAME;

    /**
     * 用系统默认的端口提供服务
     * @return 返回一个服务
     * @throws PortOccupiedException
     */
    public ServerSocket getServer() throws PortOccupiedException {
        if(PORTS.contains(port)){
            throw new PortOccupiedException();
        }
        PORTS.add(port);
        return Server.initNIOServer(port);

    }

    /**
     * 可以自定义端口
     * @param port 端口
     * @return 返回一个服务
     * @throws PortOccupiedException
     */
    public ServerSocket getServer(int port) throws PortOccupiedException {
        if(PORTS.contains(port)){
            throw new PortOccupiedException();
        }
        PORTS.add(port);
        return Server.initNIOServer(port);

    }

    /**
     * 用系统默认的端口 返回一个客户端
     * @return 返回一个客户端
     */
    public ClientSocket getClient(){
        return Client.initNIOClient(hostname,port);
    }

    /**
     * 自定义端口返回一个客户服务端地址为默认地址
     * @param port 端口
     * @return 返回一个客户
     */
    public ClientSocket getClient(int port){
        return Client.initNIOClient(hostname,port);
    }

    /**
     * 自定义ip 端口为系统底层端口
     * @param host 服务端的地址
     * @return 返回一个客户
     */
    public ClientSocket getClient(String host){
        return Client.initNIOClient(host,port);
    }

    /**
     * 自定义端口和服务端地址
     * @param hostname 服务端的地址
     * @param port 端口
     * @return 返回一个客户
     */
    public ClientSocket getClient(String hostname,int port){
        return Client.initNIOClient(hostname,port);
    }
}
