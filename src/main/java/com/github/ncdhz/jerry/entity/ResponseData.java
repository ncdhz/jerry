package com.github.ncdhz.jerry.entity;

public class ResponseData {
    /**
     * 响应数据
     */
    private Object data;

    /**
     * 请求数据格式数据格式
     */
    private String accept;
    /**
     * 响应路径
     */
    private String responsePath;

    /**
     * 响应的用户id
     */
    private String userId;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getResponsePath() {
        return responsePath;
    }

    public void setResponsePath(String responsePath) {
        this.responsePath = responsePath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }
}
