package com.jxcia.blog.service.config;

import com.jxcia.blog.blog.security.service.CustomUserDetails;
import com.jxcia.blog.common.constant.RoleConstant;
import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.entity.Role;
import com.jxcia.blog.service.mapper.admin.AdminMapper;
import com.jxcia.blog.service.mapper.admin.PermissionMapper;
import com.jxcia.blog.service.mapper.admin.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityServiceConfig {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            Admin admin = adminMapper.getByEmail(email);
            if (admin == null) return null;

            // 查询角色
            List<Role> roleList = roleMapper.getByAdminId(admin.getId());
            List<Integer> roleIdList = roleList.stream()
                    .filter(r -> r.getStatus().equals(RoleConstant.ENABLE))
                    .map(Role::getId)
                    .toList();

            // 查询权限
            permissionMapper.getByRoleIdList(roleIdList);

            return CustomUserDetails.builder()
                    .email(email)
                    .password(admin.getPassword())
                    .id(admin.getId())
//                    .authorities()
                    .build();
        };
    }
}
