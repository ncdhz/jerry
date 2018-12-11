package com.github.ncdhz.jerry.session;

import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class Request {
    /**
     * 用于存储所有的用户会话
     */
    private static ConcurrentHashMap<String,Session>  requestSession = new ConcurrentHashMap<>();
    static {

        new Thread(){
            private long sessionHeartbeat= (long) DefaultConfig.SESSION_HEARTBEAT*1000;
            @Override
            public void run() {
                while (true){
                    for (String s : requestSession.keySet()) {
                        /**
                         * 遍历session池
                         */
                        JerrySession session = (JerrySession)requestSession.get(s);
                        /**
                         * 判断会话是否为空
                         * 如果会话为空 在连接池中清空会话
                         */
                        if (session==null)
                            requestSession.remove(s);
                        else {
                            /**
                             * 获取会话的死亡时间
                             */
                            long sessionFailureTime = session.getSessionFailureTime();
                            if(System.currentTimeMillis()>=sessionFailureTime){
                                /**
                                 * 当前时间大于会话死亡时间
                                 */
                                SocketChannel socketChannel = session.getSocketChannel();
                                /**
                                 * 如果会话管道为空
                                 * 删除会话
                                 */
                                if (socketChannel==null)
                                    requestSession.remove(s);
                                /**
                                 * 如果会话管道关闭
                                 * 删除会话
                                 */
                                else if (!socketChannel.isOpen()){
                                    requestSession.remove(s);
                                /**
                                 * 如果会话管道打开更新会话死亡时间
                                 */
                                }else if (socketChannel.isOpen()){
                                    session.refresh();
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(sessionHeartbeat);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
    /**
     * 通过sessionid获取session
     */
    public static Session getSession(String sessionId){
        JerrySession session = (JerrySession) requestSession.get(sessionId);

        if (session!=null)
            session.refresh();
        return session;
    }

    /**
     * 获取所有的用户会话
     */
    public static Collection<Session> getAllSession(){
        return requestSession.values();
    }

    /**
     * 新建一个 Session
     */
    public static Session newSession(String sessionId, SocketChannel socketChannel){
        /**
         * 用sessionId 查询是否存在会话
         */
        JerrySession session = (JerrySession)requestSession.get(sessionId);
        /**
         * 如果存在跟新 会话的状态 跟新会话销毁时间
         */
        if (session!=null){
            session.setSocketChannel(socketChannel);
            session.refresh();
            return session;
        }
        /**
         * 如果不存在新建session 放入 session 池
         */
        JerrySession jerrySession = new JerrySession();
        jerrySession.setSessionId(sessionId);
        jerrySession.setSocketChannel(socketChannel);
        jerrySession.refresh();
        requestSession.put(sessionId,jerrySession);

        return jerrySession;
    }


    /**
     * 通过sessionId删除session
     */
    public static void deleteSession(String sessionId){
        requestSession.remove(sessionId);
    }
}
