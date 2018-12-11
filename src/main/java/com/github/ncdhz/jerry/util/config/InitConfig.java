package com.github.ncdhz.jerry.util.config;

import com.github.ncdhz.jerry.exception.ConfigurationFileNotFoundException;

public interface InitConfig {
    /**
     * 初始化配置
     * @param file 传配置文件路径
     * @return 返回true 初始化成功 返回false 初始化失败
     */
    boolean loadConfig(String file) throws ConfigurationFileNotFoundException;

    boolean loadConfig();
}
