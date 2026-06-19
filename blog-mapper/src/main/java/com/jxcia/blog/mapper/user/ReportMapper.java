package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Report;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface ReportMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into report (user_id, object_type, object_id, message, status, create_time) " +
            "values (#{userId}, #{objectType}, #{objectId}, #{message}, 0, #{createTime})")
    void insert(Report report);
}
