package com.github.ncdhz.jerry.handler;

import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.nio.charset.CharsetDecoder;
public interface Handler {
    /**
     * 解码器
     */
    CharsetDecoder decoder = DefaultConfig.decoder;

}
