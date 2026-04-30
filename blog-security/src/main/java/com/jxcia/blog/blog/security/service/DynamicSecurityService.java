package com.jxcia.blog.blog.security.service;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * 动态资源服务接口
 */
public interface DynamicSecurityService {
    /**
     * 加载资源 ANT 通配符和 MAP 资源
     * @return
     */
    Map<String, ConfigAttribute> loadDataSource();
}
