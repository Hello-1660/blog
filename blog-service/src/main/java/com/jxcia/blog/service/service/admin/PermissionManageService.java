package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.PermissionDto;
import com.jxcia.blog.pojo.dto.RolePermissionDto;
import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.pojo.vo.PermissionDetailVo;

import java.util.List;

public interface PermissionManageService {
    List<Permission> list();
    PermissionDetailVo detail(Integer id);
    void save(PermissionDto dto);
    void update(PermissionDto dto);
    void delete(Integer id);
    void assignPermission(RolePermissionDto dto);
}
