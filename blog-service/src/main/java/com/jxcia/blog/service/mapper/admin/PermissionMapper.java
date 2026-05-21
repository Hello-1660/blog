package com.jxcia.blog.service.mapper.admin;

import com.jxcia.blog.pojo.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 mapper
 */
@Mapper
public interface PermissionMapper {

    /**
     * 查询全部权限
     * @return
     */
    @Select("select * from permission")
    List<Permission> getAll();

    /**
     * 根据角色列表查询权限
     * @param roleIdList 角色列表
     * @return 权限列表
     */
    List<Permission> getByRoleIdList(List<Integer> roleIdList);
}
