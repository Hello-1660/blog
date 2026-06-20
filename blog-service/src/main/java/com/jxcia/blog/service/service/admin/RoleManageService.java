package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.RoleDto;
import com.jxcia.blog.pojo.dto.RoleMenuDto;
import com.jxcia.blog.pojo.dto.RolePermissionDto;
import com.jxcia.blog.pojo.entity.Role;
import com.jxcia.blog.pojo.vo.RoleDetailVo;

import java.util.List;

public interface RoleManageService {
    List<Role> list();
    RoleDetailVo detail(Integer id);
    void save(RoleDto dto);
    void update(RoleDto dto);
    void delete(Integer id);
    void assignPermission(RolePermissionDto dto);
    void assignMenu(RoleMenuDto dto);
}
