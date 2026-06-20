package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.exception.AdminException;
import com.jxcia.blog.mapper.admin.PermissionMapper;
import com.jxcia.blog.mapper.admin.RoleMapper;
import com.jxcia.blog.pojo.dto.PermissionDto;
import com.jxcia.blog.pojo.dto.RolePermissionDto;
import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.pojo.entity.Role;
import com.jxcia.blog.pojo.vo.PermissionDetailVo;
import com.jxcia.blog.service.service.admin.PermissionManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PermissionManageServiceImpl implements PermissionManageService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Permission> list() {
        return permissionMapper.getAll();
    }

    @Override
    public PermissionDetailVo detail(Integer id) {
        Permission permission = permissionMapper.getById(id);
        if (permission == null) throw new AdminException("权限不存在");
        List<Role> roles = permissionMapper.getRolesByPermissionId(id);
        return PermissionDetailVo.builder()
                .id(permission.getId())
                .name(permission.getName())
                .url(permission.getUrl())
                .description(permission.getDescription())
                .createTime(permission.getCreateTime())
                .roles(roles)
                .build();
    }

    @Override
    public void save(PermissionDto dto) {
        Permission permission = new Permission();
        permission.setName(dto.getName());
        permission.setUrl(dto.getUrl());
        permission.setDescription(dto.getDescription());
        permission.setCreateTime(LocalDateTime.now());
        permissionMapper.insert(permission);
    }

    @Override
    public void update(PermissionDto dto) {
        Permission permission = new Permission();
        permission.setId(dto.getId());
        permission.setName(dto.getName());
        permission.setUrl(dto.getUrl());
        permission.setDescription(dto.getDescription());
        permissionMapper.update(permission);
    }

    @Override
    public void delete(Integer id) {
        permissionMapper.deleteRolePermissionRelations(id);
        permissionMapper.deleteById(id);
    }

    @Override
    public void assignPermission(RolePermissionDto dto) {
        roleMapper.deleteRolePermissions(dto.getRoleId());
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            roleMapper.insertRolePermissions(dto.getRoleId(), dto.getPermissionIds());
        }
    }
}
