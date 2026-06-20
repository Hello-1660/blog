package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.dto.AdminPageDto;
import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.vo.AdminPageVo;
import com.jxcia.blog.pojo.vo.AdminVo;
import jakarta.validation.constraints.Email;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 管理员 mapper
 */
@Mapper
public interface AdminMapper {

    @Select("select * from admin where email = #{email}")
    Admin getByEmail(@Email String email);

    @Insert("insert into admin (nickname, email, password, create_time, status) " +
            "value (#{nickname}, #{email}, #{password}, #{createTime}, #{status})")
    void insert(Admin admin);

    @Select("select * from admin where id = #{id}")
    AdminVo getById(Integer id);

    void update(Admin admin);

    List<AdminPageVo> getPage(AdminPageDto dto);

    Integer count(AdminPageDto dto);

    @Delete("delete from admin where id = #{id}")
    void deleteById(Integer id);

    void insertAdminRole(@Param("adminId") Integer adminId, @Param("roleIds") List<Integer> roleIds);

    @Delete("delete from admin_role_relation where admin_id = #{adminId}")
    void deleteAdminRoles(Integer adminId);

    @Select("select role_id from admin_role_relation where admin_id = #{adminId}")
    List<Integer> getRoleIdsByAdminId(Integer adminId);

    @Select("select * from admin where id = #{id}")
    Admin getEntityById(Integer id);
}
