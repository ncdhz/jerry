package com.github.ncdhz.jerry.handler;


import com.github.ncdhz.jerry.entity.ResponseData;

public interface DataParsingHandler {

    ResponseData serverDataParsing(String data);

    String clientDataParsing(String data);


}
