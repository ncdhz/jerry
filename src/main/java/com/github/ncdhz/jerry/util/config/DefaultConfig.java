package com.github.ncdhz.jerry.util.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.ncdhz.jerry.entity.ConfigData;
import com.github.ncdhz.jerry.exception.ConfigurationFileNotFoundException;
import com.github.ncdhz.jerry.util.resolver.AnnotationResolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 系统的默认配置
 */

public class DefaultConfig implements InitConfig{

    private static DefaultConfig defaultConfig;

    public static InitConfig getInitConfig(){
        if (defaultConfig==null){
            synchronized (DefaultConfig.class){
                if (defaultConfig==null){
                    defaultConfig = new DefaultConfig();
                }
            }
        }
        return defaultConfig;
    }
    private DefaultConfig(){}
    /**
     * 默认缓存长度  服务端
     */
    private static int SERVER_BUFFER_SIZE = 512;
    /**
     * 服务端缓冲流
     */
    public static ByteBuffer clientDataBuffer;
    /**
     * 客户端缓冲流
     */
    public static ByteBuffer serverDataBuffer;
    /**
     * 返回指定的字符集
     */
    private static Charset charset;

    /**
     *  编码器
     */
    public static CharsetEncoder encoder;
    /**
     *  解码器
     */
    public static CharsetDecoder decoder;

    /**
     * 默认缓存长度  客户端
     */
    private static int CLIENT_BUFFER_SIZE = 512;

    /**
     * 默认端口
     */
    public static int PORT = 9900;


    /**
     * 连接的 host
     */
    public static String HOSTNAME = "localhost";
    /**
     * 数据编码解码格式
     */
    private static String CODING_FORMAT = "UTF-8";
    /**
     * session 心跳时间
     * 默认是十秒
     */
    public static double SESSION_HEARTBEAT = 10;

    /**
     * session 失效时间
     * 默认是三十分钟
     */
    public static double SESSION_FAILURE = 30;

    public static List<String> PACKET_MAPPING;

    public static List<String> CLASS_MAPPING;

    /**
     * 初始化后这里会存放全局解释注解过后的信息
     * 具体信息可以查看JerryAnnotationResolver
     */
    public static AnnotationResolver annotationResolver;

    @Override
    public boolean loadConfig(String file) throws ConfigurationFileNotFoundException {
        ConfigData load = load(file);
        if (load==null){
            throw new ConfigurationFileNotFoundException();
        }
        updateConfig(load);
        defaultLoad();
        return true;
    }

    @Override
    public boolean loadConfig() {
        ConfigData load = load("/jerry.json");
        if (load!=null){
            updateConfig(load);
        }
        defaultLoad();
        return true;
    }

    /**
     * 加载默认配置
     */
    private void defaultLoad(){
        serverDataBuffer = ByteBuffer.allocateDirect(SERVER_BUFFER_SIZE);
        clientDataBuffer = ByteBuffer.allocateDirect(CLIENT_BUFFER_SIZE);
        charset = Charset.forName(CODING_FORMAT);
        encoder = charset.newEncoder();
        decoder = charset.newDecoder();
    }

    /**
     * 更新配置文件
     */
    private void updateConfig(ConfigData config){
        Integer serverBufferSize = config.getServerBufferSize();
        Integer clientBufferSize = config.getClientBufferSize();
        String codingFormat = config.getCodingFormat();
        Integer port = config.getPort();
        String hostname = config.getHostname();
        List<String> packetMapping = config.getPacketMapping();
        List<String> classMapping = config.getClassMapping();
        Double sessionHeartbeat = config.getSessionHeartbeat();
        Double sessionFailure = config.getSessionFailure();

        if (serverBufferSize!=null)
            SERVER_BUFFER_SIZE = serverBufferSize;
        if (clientBufferSize!=null)
            CLIENT_BUFFER_SIZE = clientBufferSize;
        if (codingFormat!=null)
            CODING_FORMAT = codingFormat;
        if (port!=null)
            PORT = port;
        if (hostname!=null)
            HOSTNAME = hostname;
        if (packetMapping!=null)
            PACKET_MAPPING = packetMapping;
        if (classMapping!=null)
            CLASS_MAPPING =classMapping;
        if (sessionHeartbeat!=null&&sessionHeartbeat>0)
            SESSION_HEARTBEAT = sessionHeartbeat;
        if (sessionFailure!=null&&sessionFailure>0)
            SESSION_FAILURE = sessionFailure;

    }

    private ConfigData load(String filePath){
        String fileN = DefaultConfig.class.getResource(filePath).getFile();
        if (fileN==null)
            return null;

        File file = new File(fileN);

        if (!file.isFile())
            return null;
        BufferedReader bufferedReader = null;
        ConfigData defaultConfig = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            StringBuffer dataBuffer = new StringBuffer();
            String data;
            while ((data=bufferedReader.readLine())!=null){
                String dataT = data.trim();
                /**
                 * 剔除 // 单行注解
                 */
                if (!dataT.startsWith("//")){
                    String[] split = dataT.split("//");
                    if (split.length>1){
                        dataT=split[0];
                    }
                    dataBuffer.append(dataT);
                }
            }

            Pattern p = Pattern.compile("/\\*([\\S\\s]*?)\\*/");
            Matcher m = p.matcher(dataBuffer.toString());
            data = m.replaceAll("");
            defaultConfig = JSON.parseObject(data, ConfigData.class);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedReader!=null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return defaultConfig;
    }


}
