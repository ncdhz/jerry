package com.github.ncdhz.jerry.entity;

import java.util.Date;

/**
 * Jerry 协议的格式
 */
public class Jerry {
    /**
     * 请求host
     */
     private String host;
    /**
     * 请求地址
     */
     private String address;
    /**
     * 请求的内容类型
     */
    private String contentType;

    /**
     * 会话id 服务端创建 客户端的每次请求必须携带
     */
    private String sessionId;
    /**
     * 发送请求方
     * 接受信息的格式
     */
    private String accept;

    /**
     * 数据
     * 对象数据  或者是 键值对数据
     */
    private String data;

    /**
     * 传输时间
     */
    private Date date;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
