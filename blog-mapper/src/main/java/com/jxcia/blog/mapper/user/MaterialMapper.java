package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Material;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialMapper {

    /**
     * 插入素材记录
     * @param material
     */
    @Insert("insert into material (type, user_id, url, name, group_id, upload_time) " +
            "value (#{type}, #{userId}, #{url}, #{name}, #{groupId}, #{uploadTime})")
    void insert(Material material);
}
