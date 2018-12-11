package com.github.ncdhz.jerry.session;

class SessionData<T> {

    private T data;

    T getData() {
        return data;
    }

    void setData(T data) {
        this.data = data;
    }
}
