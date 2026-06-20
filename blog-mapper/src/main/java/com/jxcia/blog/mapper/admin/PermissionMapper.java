package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.pojo.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PermissionMapper {

    @Select("select * from permission")
    List<Permission> getAll();

    List<Permission> getByRoleIdList(List<Integer> roleIdList);

    @Select("select * from permission where id = #{id}")
    Permission getById(Integer id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into permission (name, url, description, create_time) " +
            "values (#{name}, #{url}, #{description}, #{createTime})")
    void insert(Permission permission);

    void update(Permission permission);

    @Delete("delete from permission where id = #{id}")
    void deleteById(Integer id);

    @Delete("delete from role_permission_relation where permission_id = #{permissionId}")
    void deleteRolePermissionRelations(Integer permissionId);

    @Select("select r.* from `role` r " +
            "left join role_permission_relation rpr on r.id = rpr.role_id " +
            "where rpr.permission_id = #{permissionId}")
    List<Role> getRolesByPermissionId(Integer permissionId);
}
