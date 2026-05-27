package com.jxcia.blog.mapper.admin;

import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.vo.AdminVo;
import jakarta.validation.constraints.Email;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 管理员 mapper
 */
@Mapper
public interface AdminMapper {

    /**
     * 根据管理员邮箱查询管理员
     * @param email 邮箱
     * @return 管理员
     */
    @Select("select * from admin where email = #{email}")
    Admin getByEmail(@Email String email);

    /**
     * 插入管理员记录
     * @param admin 管理员信息
     */
    @Insert("insert into admin (nickname, email, password, create_time, status) " +
            "value (#{nickname}, #{email}, #{password}, #{createTime}, #{status})")
    void insert(Admin admin);

    @Select("select * from admin where id = #{id}")
    AdminVo getById(Integer id);
}
