package com.github.ncdhz.jerry.session;

import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class JerrySession implements Session{

    /**
     * 会话id
     */
    private String sessionId;

    /**
     * 存储用户数据
     */
    private Map<String,SessionData> sessionMap;

    /**
     * 和用户会话的通道
     */
    private SocketChannel socketChannel;

    /**
     * session 失效时间
     */
    private long SessionFailureTime;

    public <T> void setAttribute(String name,T value){

        if (sessionMap==null){
            /**
             * ConcurrentHashMap 性能是 HashMap 的16倍左右  线程安全
             */
            sessionMap = new ConcurrentHashMap<>();
        }

        SessionData<T> data= new SessionData<>();
        data.setData(value);

        sessionMap.put(name,data);

    }

    public  <T> T getAttribute(String name){
        if(sessionMap==null)
            return null;
        SessionData sessionData = sessionMap.get(name);
        if (sessionData ==null)
            return null;
        return (T) sessionData.getData();

    }

    /**
     * 删除指定名字的session属性
     * @param name session名字
     */
    public void removeAttribute(String name){
        sessionMap.remove(name);
    }

    /**
     * 获取当前的会话ID
     */
    public String getId(){
        return sessionId;
    }

    /**
     * 刷新会话
     */
    @Override
    public void refresh() {
        setSessionFailureTime(getNowSessionFailureTime());
    }

    /**
     * 获取基于当前时间的 session 死亡时间
     * @return 返回基于当前时间的 session 死亡时间
     */
    private static long getNowSessionFailureTime(){
        return System.currentTimeMillis()+ (long) DefaultConfig.SESSION_FAILURE*60*1000;
    }

    public void setSessionId(String sessionId){
        this.sessionId = sessionId;
    }
    /**
     * 使session失效。可以立即使当前会话失效
     */
    public void invalidate(){

    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public long getSessionFailureTime() {
        return SessionFailureTime;
    }

    public void setSessionFailureTime(long sessionFailureTime) {
        SessionFailureTime = sessionFailureTime;
    }
}
