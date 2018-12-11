package com.github.ncdhz.jerry.util.resolver;

import com.github.ncdhz.jerry.entity.JerryData;
import com.github.ncdhz.jerry.entity.MethodMapping;
import com.github.ncdhz.jerry.entity.MethodTypeMapping;
import com.github.ncdhz.jerry.exception.*;
import com.github.ncdhz.jerry.session.Session;
import com.github.ncdhz.jerry.util.URIUtil;
import com.github.ncdhz.jerry.util.annotation.*;
import com.github.ncdhz.jerry.util.config.DefaultConfig;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注释解析器
 */
class JerryAnnotationResolver implements AnnotationResolver {

    private static List<String> packetMapping = DefaultConfig.PACKET_MAPPING;

    private static List<String> classMapping =DefaultConfig.CLASS_MAPPING;

    /**
     * 用于路径转换 把绝对路径转换成可以 Class.forName()的路径
     */
    private static String ClassPath;

    private static ConcurrentHashMap<String,MethodMapping> mappingMap =new ConcurrentHashMap<>();

    public Map<String,MethodMapping> getMappingMap(){
        return mappingMap;
    }

    private static ConcurrentHashMap<String,Object> autoWired =new ConcurrentHashMap<>();

    JerryAnnotationResolver(){
        try {
            Set<String> allAnnotationClass = getAllAnnotationClass();

            for (String annotationClass : allAnnotationClass) {
                Class<?> clazz = Class.forName(annotationClass);
                /**
                 * 初始化 JerryBean 和 JerryService
                 */
                initJerryBeanAndJerryService(clazz);
            }


            for (String annotationClass : allAnnotationClass) {
                Class<?> clazz = Class.forName(annotationClass);
                initJerryController(clazz);
            }


            for (String annotationClass : allAnnotationClass) {
                Class<?> clazz = Class.forName(annotationClass);
                /**
                 * 初始化 JerryAutoWired
                 */
                initJerryAutoWired(clazz);
            }


            for (String annotationClass : allAnnotationClass) {
                Class<?> clazz = Class.forName(annotationClass);
                /**
                 * 初始化 JerryAutoWired
                 */
                initJerryBeanMethod(clazz);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化 JerryAutoWired
     */
    private void initJerryAutoWired(Class<?> clazz) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, JerryAutoWiredInitException {

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            JerryAutoWired jerryAutoWired = field.getAnnotation(JerryAutoWired.class);
            if (jerryAutoWired!=null){

                Object value = autoWired.get(field.getType().getName());
                Object key = autoWired.get(clazz.getName());

                /**
                 * 如果 autoWired 不存在字段对应的类
                 */
                if (value==null){
                    Class<?> type = field.getType();
                    /**
                     * 判断type对应的是不是类
                     */
                    if (isClass(type)){
                        /**
                         * 如果是类直接初始化
                         */
                        value = initConstructor(type);
                    }else {
                        /**
                         * 如果不是类 找到他的子类然后初始化
                         */
                        objToValue:for (Object o : autoWired.values()) {
                            Class<?> superclass = o.getClass().getSuperclass();
                            if (superclass == type){
                                value = o;
                                break;
                            }
                            Class<?>[] interfaces = o.getClass().getInterfaces();
                            for (Class<?> anInterface : interfaces) {
                                if (anInterface == type){
                                    value = o;
                                    break objToValue;
                                }
                            }
                        }
                        if (value==null)
                            throw new JerryAutoWiredInitException();
                    }

                    autoWired.put(field.getType().getName(),value);
                }
                if (key==null){
                    key = initConstructor(clazz);
                    autoWired.put(clazz.getName(),key);
                }
                field.setAccessible(true);
                field.set(key,value);

            }
        }
    }

    /**
     * 初始化 JerryController
     */
    private void initJerryController(Class<?> clazz) throws MissingRequestParamException, IllegalAccessException, InstantiationException, AddressConflictException {

        JerryController jerryController = clazz.getAnnotation(JerryController.class);
        if (jerryController==null)
            return;
        JerryRequestMapping jerryRequestMapping = clazz.getAnnotation(JerryRequestMapping.class);
        String requestPath = "";
        if (jerryRequestMapping!=null){
            String value = jerryRequestMapping.value();
            requestPath = URIUtil.getStandardPath(value);
        }

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            JerryRequestMapping jerryRequestMappingMethod = method.getAnnotation(JerryRequestMapping.class);

            if (jerryRequestMappingMethod!=null){
                /**
                 * 映射出方法的 @JerryRequestMapping 的value值
                 * 和类的 @JerryRequestMapping 组合成一股URI
                 */
                String value = jerryRequestMappingMethod.value();

                String path = requestPath+URIUtil.getStandardPath(value);

                String methodName = method.getName();

                /**
                 * 构建一个方法存储实体类
                 */
                MethodMapping methodMapping = new MethodMapping();

                JerryResponseMapping jerryResponseMapping = method.getAnnotation(JerryResponseMapping.class);

                if (jerryResponseMapping!=null){
                    String jerryResponseMappingValue = jerryResponseMapping.value();
                    methodMapping.setResponseMapping(URIUtil.getStandardPath(jerryResponseMappingValue));
                }


                methodMapping.setMethodName(methodName);

                methodMapping.setMethod(method);

                /**
                 * 方法参数的注解解析
                 */

                Parameter[] parameters = method.getParameters();
                for (Parameter parameter : parameters) {
                    JerryRequestParam jerryRequestParam = parameter.getAnnotation(JerryRequestParam.class);
                    MethodTypeMapping methodTypeMapping = new MethodTypeMapping();
                    methodTypeMapping.setMethodType(parameter.getType());
                    if (jerryRequestParam==null){
                        if (parameter.getType() != Session.class &&parameter.getType() != JerryData.class)
                            throw new MissingRequestParamException();
                    }else {
                        methodTypeMapping.setMethodTypeName(jerryRequestParam.name());
                        methodTypeMapping.setMethodTypeDefault(jerryRequestParam.defaultValue());
                    }
                    methodMapping.setMethodTypeMapping(methodTypeMapping);

                }

                Object obj = autoWired.get(clazz.getName());

                if (obj==null){
                    obj = clazz.newInstance();
                    autoWired.put(clazz.getName(),obj);
                }
                methodMapping.setObject(obj);
                if (mappingMap.get(path)!=null)
                    throw new AddressConflictException();
                mappingMap.put(path,methodMapping);

            }
        }
    }
    /**
     * 初始化JerryBean 和 JerryService
     */
    private void initJerryBeanAndJerryService(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        /**
         * 如果 clazz 不是类就会跳过
         */
        if (!isClass(clazz))
            return;

        JerryBean jerryBeanClass = clazz.getAnnotation(JerryBean.class);
        JerryService JerryServiceClass = clazz.getAnnotation(JerryService.class);
        if (jerryBeanClass!=null||JerryServiceClass!=null){
            Object obj = autoWired.get(clazz.getName());
            /**
             * 当 类中含有 JerryBean 或者 JerryService 时 并且autoWired 中不存在这个对象
             */
            if (obj==null){
                /**
                 * 利用初始化函数初始化这个对象  并放入 autoWired
                 */
                autoWired.put(clazz.getName(),initConstructor(clazz));
            }
        }
    }

    /**
     *  初始化 含有JerryBean的方法
     */
    private void initJerryBeanMethod(Class<?> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method  method: declaredMethods) {
            method.setAccessible(true);
            JerryBean jerryBeanMethod = method.getAnnotation(JerryBean.class);
            /**
             * 判断方法中是否含有 JerryBean 的注解
             */
            if (jerryBeanMethod!=null){
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length==0){
                    Object obj = autoWired.get(clazz.getName());
                    if (obj==null){
                        autoWired.put(clazz.getName(),initConstructor(clazz));
                    }
                    method.invoke(obj);
                }
            }
        }
    }


    /**
     * 获取所有解释的类的路径
     * @return 返回一个装这所有需要解释的类的set集合
     * @throws ClassMappingException ClassMapping配置错误
     * @throws PacketMappingException PacketMapping配置错误
     */
    private Set<String> getAllAnnotationClass() throws ClassMappingException, PacketMappingException {

        /**
         * 存放需要解释类的路径
         */
        Set<String> allFilePath = new TreeSet<>();
        /**
         * 解析所有的类文件配置
         */
        if(classMapping!=null){
            for (String clazz : classMapping) {
                String clazzR = "/" +clazz.replace(".", "/") +".class";
                URL resource = getClass().getResource(clazzR);
                if (resource==null)
                    throw new ClassMappingException();
                String clazzPath = resource.getPath();
                File file = new File(clazzPath);
                if (file.isFile()){
                    allFilePath.add(clazz);
                }
                else
                    throw new ClassMappingException();
            }
        }

        /**
         * 存放所有的包
         */
        LinkedList<String> allPacket = null;
        /**
         * 把所有合适的包放到allPacket
         */
        if (packetMapping!=null){
            allPacket=new LinkedList<>();
            for (String packet : packetMapping) {
                String packetR = packet.replace(".", "/");
                String packetRA = "/" + packetR;

                URL resource = getClass().getResource(packetRA);

                if (resource==null)
                    throw new PacketMappingException();

                String packetPath = resource.getPath();
                File filePacket = new File(packetPath);
                /**
                 * 包映射路径配置错误
                 */
                if (!filePacket.isDirectory())
                    throw new PacketMappingException();

                String[] split = packetPath.split(packetR);
                ClassPath = split[0];

                allPacket.add(packetPath);
            }
        }
        /**
         * 非递归实现遍历文件夹 拿到所有类文件
         */
        while (true){
            if (allPacket==null||allPacket.size()==0)
                break;
            String firstPacket = allPacket.removeFirst();
            File file = new File(firstPacket);
            File[] files = file.listFiles();
            for (File f : files) {

                String path = f.getPath();

                if (f.isFile()&&path.endsWith(".class")){
                    allFilePath.add(getRelativePath(path));
                }
                else if(f.isDirectory())
                    allPacket.add(path);
            }
        }
        return allFilePath;
    }

    /**
     * 根据ClassPath切割一个绝对路径 返回一个可以让Class.forName操作的路径
     * @param path 一个绝对路径
     * @return 可以让Class.forName操作的路径
     */
    private String getRelativePath(String path){
        String[] split = path.split("\\.");
        String[] split1 = split[0].split(ClassPath);
        return split1[split1.length-1].replace(File.separator,".");
    }

    /**
     * 判断 Class 是类还是接口与抽象类
     * 类返回 true 抽象类和接口返回 false
     */
    private boolean isClass(Class<?> clazz){

        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isEnum();
    }

    /**
     * 初始化无参构造函数
     */
    private Object initConstructor(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
