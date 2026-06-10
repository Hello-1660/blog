package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单 mapper
 */
@Mapper
public interface MenuMapper {

    @Select("select * from menu " +
            "where id in " +
            "(select menu_id from role_menu_relation where role_id in " +
            "(select distinct id from admin_role_relation where admin_id = 1)) " +
            "and `status` = #{adminId}")
    List<Menu> getByAdminId(Integer adminId);
}
