package com.jxcia.blog.blog.security.component;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

public interface DynamicSecurityService {
    /**
     * 加载资源 ANT 通配符和 MAP 资源
     * @return
     */
    Map<String, ConfigAttribute> loadDataSource();
}
