package com.github.ncdhz.jerry.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ncdhz.jerry.entity.*;
import com.github.ncdhz.jerry.session.Request;
import com.github.ncdhz.jerry.session.Session;
import com.github.ncdhz.jerry.util.DataFormat;
import com.github.ncdhz.jerry.util.URIUtil;
import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class JerryDataParsingHandler implements DataParsingHandler,Handler{

    private static Map<String,MethodMapping> mappingMap= DefaultConfig.annotationResolver.getMappingMap();

    /**
     * 用于解析数据
     * @param jerry 数据
     */
    private ResponseData dataParsing(Jerry jerry) {
        try {
            /**
             * 格式化 URI
             */
            String standardPath = URIUtil.getStandardPath(jerry.getAddress());
            /**
             * 获取 URI 对应的对象
             */
            MethodMapping methodMapping = mappingMap.get(standardPath);

            if (methodMapping==null)
                return null;
            String data = jerry.getData();

            /**
             * 获取数据的处理方式把数据交给处理函数
             */
            String sessionId = jerry.getSessionId();

            ResponseData responseData = null;

            if (jerry.getAccept().equals(DataFormat.ACCEPT_OBJ)){

                JSONObject jsonObject = JSON.parseObject(data);

                responseData = dataProcessing(methodMapping, jsonObject, null, sessionId);

            }else if (jerry.getAccept().equals(DataFormat.ACCEPT_MAP)){
                JerryData jerryData = JSON.parseObject(data,JerryData.class);
                responseData = dataProcessing(methodMapping,null,jerryData,sessionId);
            }
            /**
             * 把客户端请求格式复赋值到 response
             */
            if (responseData!=null)
                responseData.setAccept(jerry.getAccept());
            return responseData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 用于处理解析后的数据
     */
    private ResponseData dataProcessing(MethodMapping methodMapping, JSONObject jsonObject, JerryData jerryData,String sessionId) throws InvocationTargetException, IllegalAccessException {
        /**
         * 获取方法参数对象
         */
        List<MethodTypeMapping> methodTypeMappings = methodMapping.getMethodTypeMapping();

        Object[] oj=null;
        if (methodTypeMappings!=null&&methodTypeMappings.size()!=0){
            oj = new Object[methodTypeMappings.size()];
            for (int i = 0; i < methodTypeMappings.size(); i++) {
                MethodTypeMapping methodTypeMapping = methodTypeMappings.get(i);
                String methodTypeName = methodTypeMapping.getMethodTypeName();
                Class methodType = methodTypeMapping.getMethodType();
                Object o = null;
                if (methodTypeName!=null){
                    if (jsonObject!=null)
                        o = jsonObject.get(methodTypeName);
                    else{
                        o = jerryData.get(methodTypeName);
                        if (o != null)
                            if (o.getClass() == JSONObject.class){
                                o = JSON.parseObject(JSON.toJSONString(o),methodType);
                            }
                    }
                }else {
                    /**
                     * 判断方法类型  如果类型为 Session 会默认提取他自己的 Session 初始化
                     * 如果类型是 JerryData 会新建一个 JerryData 初始化
                     */
                    if(methodType == Session.class){
                        if (sessionId==null)
                            return null;
                        o = Request.getSession(sessionId);
                    }else if (methodType == JerryData.class){
                        o = new JerryData();
                    }
                }
                if (o==null){
                    oj[i] = methodTypeMapping.getMethodTypeDefault();
                }
                else
                    oj[i] = o;
            }
        }

        Method method = methodMapping.getMethod();
        Object object = methodMapping.getObject();
        method.setAccessible(true);
        Object invoke;
        if (oj!=null)
            invoke = method.invoke(object,oj);
        else
            invoke = method.invoke(object);
        String responseMapping = methodMapping.getResponseMapping();
        if (responseMapping==null)
            return null;
        ResponseData responseData = new ResponseData();
        responseData.setResponsePath(responseMapping);
        responseData.setData(invoke);
        responseData.setUserId(sessionId);
        return responseData;
    }

    @Override
    public ResponseData serverDataParsing(String data) {
        Jerry jerry = JSON.parseObject(data,Jerry.class);
        if(jerry.getAccept().equals(DataFormat.ACCEPT_ID)){
            String sessionId = jerry.getSessionId();
            ResponseData responseData = new ResponseData();
            responseData.setUserId(sessionId);
            responseData.setAccept(jerry.getAccept());
            return responseData;
        }else
            return dataParsing(jerry);

    }

    @Override
    public String clientDataParsing(String data) {
        Jerry jerry = JSON.parseObject(data, Jerry.class);
        dataParsing(jerry);
        return null;
    }
}
