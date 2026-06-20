package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.constant.RoleConstant;
import com.jxcia.blog.common.exception.AdminException;
import com.jxcia.blog.mapper.admin.MenuMapper;
import com.jxcia.blog.mapper.admin.PermissionMapper;
import com.jxcia.blog.mapper.admin.RoleMapper;
import com.jxcia.blog.pojo.dto.RoleDto;
import com.jxcia.blog.pojo.dto.RoleMenuDto;
import com.jxcia.blog.pojo.dto.RolePermissionDto;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.pojo.entity.Role;
import com.jxcia.blog.pojo.vo.RoleDetailVo;
import com.jxcia.blog.service.service.admin.RoleManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleManageServiceImpl implements RoleManageService {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Role> list() {
        return roleMapper.getAll();
    }

    @Override
    public RoleDetailVo detail(Integer id) {
        Role role = roleMapper.getById(id);
        if (role == null) throw new AdminException("角色不存在");
        List<Permission> permissions = permissionMapper.getByRoleIdList(List.of(id));
        List<Menu> menus = menuMapper.getByRoleIdList(List.of(id));
        return RoleDetailVo.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .status(role.getStatus())
                .createTime(role.getCreateTime())
                .permissions(permissions)
                .menus(menus)
                .build();
    }

    @Override
    public void save(RoleDto dto) {
        Role role = new Role();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setStatus(RoleConstant.ENABLE);
        role.setCreateTime(java.time.LocalDateTime.now().toString());
        roleMapper.insert(role);
    }

    @Override
    public void update(RoleDto dto) {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setStatus(dto.getStatus());
        roleMapper.update(role);
    }

    @Override
    public void delete(Integer id) {
        roleMapper.deleteRolePermissions(id);
        roleMapper.deleteRoleMenus(id);
        roleMapper.deleteById(id);
    }

    @Override
    public void assignPermission(RolePermissionDto dto) {
        roleMapper.deleteRolePermissions(dto.getRoleId());
        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            roleMapper.insertRolePermissions(dto.getRoleId(), dto.getPermissionIds());
        }
    }

    @Override
    public void assignMenu(RoleMenuDto dto) {
        roleMapper.deleteRoleMenus(dto.getRoleId());
        if (dto.getMenuIds() != null && !dto.getMenuIds().isEmpty()) {
            roleMapper.insertRoleMenus(dto.getRoleId(), dto.getMenuIds());
        }
    }
}
