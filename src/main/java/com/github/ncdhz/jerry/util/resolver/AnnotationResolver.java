package com.github.ncdhz.jerry.util.resolver;

import com.github.ncdhz.jerry.entity.MethodMapping;

import java.util.Map;

public interface AnnotationResolver {

    Map<String,MethodMapping> getMappingMap();
}
