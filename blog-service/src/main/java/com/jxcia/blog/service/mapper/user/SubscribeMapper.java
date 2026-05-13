package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.Subscribe;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubscribeMapper {

    /**
     * 根据用户编号和关注用户编号来查询用户关注记录
     * @param subscribe 用户关注记录
     * @return 用户关注记录
     */
    @Select("select * from subscribe where user_id = #{userId} and sub_user_id = #{subUserId}")
    Subscribe getBySubscribe(Subscribe subscribe);

    /**
     * 插入用户关注记录
     * @param subscribe 用户关注记录
     */
    @Insert("insert into subscribe (user_id, sub_user_id, sort, create_time)" +
            "value (#{userId}, #{subUserId}, #{sort}, #{createTime})")
    void insert(Subscribe subscribe);

    /**
     * 根据用户编号和关注用户编号删除用户记录
     * @param ss 用户记录
     */
    @Delete("delete from subscribe where user_id = #{userId} and sub_user_id = #{subUserId}")
    void deleteBySubscribe(Subscribe ss);
}
