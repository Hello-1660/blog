package com.jxcia.blog.blog.security.service;

import com.jxcia.blog.common.constant.RoleConstant;
import com.jxcia.blog.mapper.admin.AdminMapper;
import com.jxcia.blog.mapper.admin.PermissionMapper;
import com.jxcia.blog.mapper.admin.RoleMapper;
import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.pojo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdminUserDetailService implements UserDetailsService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminMapper.getByEmail(email);

        if (admin == null) throw new UsernameNotFoundException("管理员 " + email + " 不存在");

        // 查询角色
        List<Role> roleList = roleMapper.getByAdminId(admin.getId());
        List<Integer> roleIdList = roleList.stream()
                .filter(r -> r.getStatus().equals(RoleConstant.ENABLE))
                .map(Role::getId)
                .toList();

        // 查询权限
        List<Permission> permissionList = permissionMapper.getByRoleIdList(roleIdList);
        List<GrantedAuthority> grantedAuthorityList = permissionList.stream()
                .map(Permission::getUrl)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return CustomUserDetails.builder()
                .email(email)
                .password(admin.getPassword())
                .id(admin.getId())
                .authorities(grantedAuthorityList)
                .build();
    }
}
