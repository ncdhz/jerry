package com.github.ncdhz.jerry.socket;

import com.github.ncdhz.jerry.exception.ConfigurationFileNotFoundException;
import com.github.ncdhz.jerry.util.config.DefaultConfig;
import com.github.ncdhz.jerry.util.config.InitConfig;
import com.github.ncdhz.jerry.util.resolver.ResolverFactory;

public class InitSocket extends JerrySocketFactory {
    private static InitConfig initConfig = DefaultConfig.getInitConfig();
    private InitSocket(){}

    public static SocketFactory loadConfig(String filePate) throws ConfigurationFileNotFoundException {
        /**
         * 初始化配置文件
         */
        initConfig.loadConfig(filePate);
        /**
         * 初始化注解
         */
        DefaultConfig.annotationResolver = ResolverFactory.getJerryAnnotationResolver();
        return new InitSocket();
    }

    public static SocketFactory loadConfig(){
        /**
         * 初始化配置文件
         */
        initConfig.loadConfig();
        /**
         * 初始化注解
         */
        DefaultConfig.annotationResolver = ResolverFactory.getJerryAnnotationResolver();
        return new InitSocket();
    }

}
