package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 更具用户信息查询用户
     * @param user 要查询的信息
     * @return 用户列表
     */
    List<User> findByUser(User user);

    /**
     * 查出数据
     * @param user 要插入的用户
     */
    @Insert("insert into user (nickname, icon, email, password, description, theme_id, create_time, like_show_status, account_status) " +
            "value (#{nickname}, #{icon}, #{email}, #{password}, #{description}, #{themeId}, #{createTime}, #{likeShowStatus}, #{accountStatus})")
    void insert(User user);

    @Select("select * from user where email = #{email}")
    User findByEmail(String email);
}
