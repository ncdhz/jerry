package com.github.ncdhz.jerry.entity;

import java.util.List;

public class ConfigData {
    /**
     * 服务端缓冲区长度
     */
    private Integer serverBufferSize;
    /**
     * 客户端缓冲区长度
     */
    private Integer clientBufferSize;
    /**
     * 编码格式
     */
    private String codingFormat;
    /**
     * 端口
     */
    private Integer port;
    /**
     * ip
     */
    private String hostname;
    /**
     * 配置扫描的包
     */
    private List<String> packetMapping;
    /**
     * 配置扫描的类
     */
    private List<String> classMapping;

    /**
     *配置session 心跳时间 时间的单位是秒
     */
    private Double sessionHeartbeat;
    /**
     * 配置session 失效时间 时间的单位分钟
     */
    private Double sessionFailure;

    public List<String> getClassMapping() {
        return classMapping;
    }

    public void setClassMapping(List<String> classMapping) {
        this.classMapping = classMapping;
    }

    public List<String> getPacketMapping() {
        return packetMapping;
    }

    public void setPacketMapping(List<String> packetMapping) {
        this.packetMapping = packetMapping;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getCodingFormat() {
        return codingFormat;
    }

    public void setCodingFormat(String codingFormat) {
        this.codingFormat = codingFormat;
    }

    public Integer getServerBufferSize() {
        return serverBufferSize;
    }

    public void setServerBufferSize(Integer serverBufferSize) {
        this.serverBufferSize = serverBufferSize;
    }

    public Integer getClientBufferSize() {
        return clientBufferSize;
    }

    public void setClientBufferSize(Integer clientBufferSize) {
        this.clientBufferSize = clientBufferSize;
    }

    public Double getSessionHeartbeat() {
        return sessionHeartbeat;
    }

    public void setSessionHeartbeat(Double sessionHeartbeat) {
        this.sessionHeartbeat = sessionHeartbeat;
    }

    public Double getSessionFailure() {
        return sessionFailure;
    }

    public void setSessionFailure(Double sessionFailure) {
        this.sessionFailure = sessionFailure;
    }
}
