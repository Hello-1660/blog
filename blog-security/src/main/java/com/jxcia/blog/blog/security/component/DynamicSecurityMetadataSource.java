package com.jxcia.blog.blog.security.component;


import com.jxcia.blog.blog.security.service.DynamicSecurityService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.*;

/**
 * 动态权限数据源，用于获动态权限规则
 */
public class DynamicSecurityMetadataSource {

    private final Map<String, ConfigAttribute> configAttributeMap = new HashMap<>();
    private final PathMatcher pathMatcher =  new AntPathMatcher();
    @Autowired
    private DynamicSecurityService dynamicSecurityService;

    @PostConstruct
    public void loadDataSource() {
        configAttributeMap.clear();
        configAttributeMap.putAll(dynamicSecurityService.loadDataSource());
    }

    /**
     * 清空缓存
     */
    public void clearDataSource() {
        configAttributeMap.clear();
    }

    /**
     * 根据请求路径获取当前接口所需要的权限
     * @param request 请求
     * @return 权限集合
     */
    public Collection<ConfigAttribute> getAllConfigAttributes(HttpServletRequest request) {
        String path = request.getRequestURI();
        List<ConfigAttribute> list = new ArrayList<>();

        for (Map.Entry<String, ConfigAttribute> entry : configAttributeMap.entrySet()) {
            String pattern = entry.getKey();
            if (pathMatcher.match(pattern, path)) list.add(entry.getValue());
        }

        return list;
    }
}
