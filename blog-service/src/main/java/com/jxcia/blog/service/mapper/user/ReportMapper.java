package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.Report;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {

    /**
     * 插入举报记录
     * @param report 举报信息
     */
    @Insert("insert into report (object_type, object_id, message, user_id, status, result, result_admin_id, create_time, finish_time) " +
            "value (#{objectType}, #{objectId}, #{message}, #{userId}, #{status}, #{result}, #{resultAdminId}, #{createTime}, #{finishTime})")
    void insert(Report report);
}
