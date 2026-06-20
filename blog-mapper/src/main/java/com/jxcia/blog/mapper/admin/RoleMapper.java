package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.entity.Role;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RoleMapper {

    @Select("select * from role r " +
            "left join admin_role_relation arr on r.id = arr.role_id " +
            "where arr.admin_id = #{adminId}")
    List<Role> getByAdminId(Integer adminId);

    @Select("select * from role order by create_time desc")
    List<Role> getAll();

    @Select("select * from role where id = #{id}")
    Role getById(Integer id);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into role (name, description, create_time, status) " +
            "values (#{name}, #{description}, #{createTime}, #{status})")
    void insert(Role role);

    void update(Role role);

    @Delete("delete from role where id = #{id}")
    void deleteById(Integer id);

    void insertRolePermissions(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);

    @Delete("delete from role_permission_relation where role_id = #{roleId}")
    void deleteRolePermissions(Integer roleId);

    void insertRoleMenus(@Param("roleId") Integer roleId, @Param("menuIds") List<Integer> menuIds);

    @Delete("delete from role_menu_relation where role_id = #{roleId}")
    void deleteRoleMenus(Integer roleId);
}
