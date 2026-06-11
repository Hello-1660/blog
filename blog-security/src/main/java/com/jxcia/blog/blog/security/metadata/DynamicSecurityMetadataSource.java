package com.jxcia.blog.blog.security.metadata;

import com.jxcia.blog.blog.security.service.DynamicSecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DynamicSecurityMetadataSource implements InitializingBean {
    private static final String PERMISSION_CACHE_KEY = "security:permission:cache";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired(required = false)
    private DynamicSecurityService dynamicSecurityService;

    private Map<String, Collection<ConfigAttribute>> configAttributeMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (dynamicSecurityService != null) {
            configAttributeMap = dynamicSecurityService.loadDataSource();
        }
    }

    /**
     * 加载权限
     * @param request
     * @return
     */
    public Collection<ConfigAttribute> getAllConfigAttributes(HttpServletRequest request) {
        // 懒加载，内存为空时从 redis 和 DB 中加载
        if (configAttributeMap.isEmpty()) configAttributeMap = loadDataSource();

        String path = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();

        for (Map.Entry<String, Collection<ConfigAttribute>> en : configAttributeMap.entrySet()) {
            if (matcher.match(en.getKey(), path)) {
                return en.getValue();
            }
        }

        return Collections.emptyList();
    }

    public void clearDataSource() {
        configAttributeMap.clear();
        redisTemplate.delete(PERMISSION_CACHE_KEY);
    }

    /**
     * 加载接口权限
     * @return
     */
    private Map<String, Collection<ConfigAttribute>> loadDataSource() {
        // L1 查 redis
        Object cache = redisTemplate.opsForValue().get(PERMISSION_CACHE_KEY);
        if (cache != null) return (Map<String, Collection<ConfigAttribute>>) cache;

        // L2 查 DB
        if (dynamicSecurityService == null) return Collections.emptyMap();
        Map<String, Collection<ConfigAttribute>> data = dynamicSecurityService.loadDataSource();
        if (data == null) data = Collections.emptyMap();

        redisTemplate.opsForValue().set(PERMISSION_CACHE_KEY, data, 30, TimeUnit.MINUTES);
        return data;
    }
}
