package com.jxcia.blog.blog.security.component;

import com.jxcia.blog.blog.security.service.DynamicSecurityService;
import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.mapper.admin.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DynamicSecurityServiceImpl implements DynamicSecurityService {
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 加载资源 ANT 通配符和 MAP 资源
     *
     * @return
     */
    @Override
    public Map<String, ConfigAttribute> loadDataSource() {
        Map<String, ConfigAttribute> map = new ConcurrentHashMap<>();
        List<Permission> permissionList = permissionMapper.getAll();
        permissionList.forEach(p -> map.put(p.getUrl(), new SecurityConfig(p.getUrl())));
        return map;
    }
}
