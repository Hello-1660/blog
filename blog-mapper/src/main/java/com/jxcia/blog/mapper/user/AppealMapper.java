package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Appeal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AppealMapper {

    /**
     * 插入申诉记录
     * @param appeal
     */
    @Insert("insert into appeal (user_id, type, object_id, message, status, result, result_admin_id, create_time, finish_time) " +
            "value (#{userId}, #{type}, #{objectId}, #{message}, #{status}, #{result}, #{resultAdminId}, #{createTime}, #{finishTime})")
    void insert(Appeal appeal);

    /**
     * 根据用户编号查询申诉列表
     * @param userId 用户编号
     * @return 申诉列表
     */
    @Select("select * from appeal where user_id = #{userId}")
    List<Appeal> getByUserId(Integer userId);
}
