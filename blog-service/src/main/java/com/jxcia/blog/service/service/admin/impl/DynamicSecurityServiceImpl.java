package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.blog.security.service.DynamicSecurityService;
import com.jxcia.blog.mapper.admin.PermissionMapper;
import com.jxcia.blog.pojo.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DynamicSecurityServiceImpl implements DynamicSecurityService {
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public Map<String, Collection<ConfigAttribute>> loadDataSource() {
        List<Permission> permissions = permissionMapper.getAll();

        HashMap<String, Collection<ConfigAttribute>> map = new HashMap<>();
        for (Permission p : permissions) {
            ConfigAttribute attr = new SecurityConfig(p.getUrl());
            map.computeIfAbsent(p.getUrl(), k -> new ArrayList<>()).add(attr);
        }

        return map;
    }
}
