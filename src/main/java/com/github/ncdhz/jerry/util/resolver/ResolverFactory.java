package com.github.ncdhz.jerry.util.resolver;

/**
 * 解释器工厂
 */
public class ResolverFactory {

    private static JerryAnnotationResolver jerryAnnotationResolver = new JerryAnnotationResolver();

    /**
     * 获取注解解释器
     * @return 返回注解解释器
     */
    public static AnnotationResolver getJerryAnnotationResolver(){

        return jerryAnnotationResolver;
    }
}
