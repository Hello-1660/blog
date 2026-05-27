package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 mapper
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据管理员编号获取角色
     * @param adminId 管理员编号
     * @return 角色列表
     */
    @Select("select * from role r " +
            "left join admin_role_relation arr on r.id = arr.role_id " +
            "where arr.admin_id = #{adminId}")
    List<Role> getByAdminId(Integer adminId);
}
