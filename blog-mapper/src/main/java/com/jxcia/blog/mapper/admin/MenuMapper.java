package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.entity.Menu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MenuMapper {

    @Select("select * from menu " +
            "where id in " +
            "(select menu_id from role_menu_relation where role_id in " +
            "(select distinct role_id from admin_role_relation where admin_id = #{adminId})) " +
            "and status = 1")
    @ResultMap("MenuResultMap")
    List<Menu> getByAdminId(Integer adminId);

    @Insert("insert into menu (p_id, name, level, web_name, icon, sort, status, create_time) " +
            "values (#{pId}, #{name}, #{level}, #{webNme}, #{icon}, #{sort}, #{status}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Menu menu);

    @Select("select * from menu order by level, sort")
    @ResultMap("MenuResultMap")
    List<Menu> getAll();

    @Select("select * from menu where id = #{id}")
    @ResultMap("MenuResultMap")
    Menu getById(Integer id);

    void update(Menu menu);

    @Delete("delete from menu where id = #{id}")
    void deleteById(Integer id);

    @Select("select * from menu where p_id = #{pId} order by sort")
    @ResultMap("MenuResultMap")
    List<Menu> getChildrenByPid(Integer pId);

    List<Menu> getByRoleIdList(List<Integer> roleIdList);
}
