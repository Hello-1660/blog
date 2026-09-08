package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Appeal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppealMapper {

    /**
     * 插入申诉记录
     * @param appeal
     */
    @Insert("insert into appeal (user_id, type, object_id, message, status, result, result_admin_id, create_time, finish_time) " +
            "value (#{userId}, #{type}, #{objectId}, #{message}, #{status}, #{result}, #{resultAdminId}, #{createTime}, #{finishTime})")
    void insert(Appeal appeal);
}
