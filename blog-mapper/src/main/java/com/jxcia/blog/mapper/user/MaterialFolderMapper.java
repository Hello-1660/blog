package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.MaterialFolder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialFolderMapper {
    /**
     * 用户创建素材文件夹
     * @param folder 文件夹
     */
    @Insert("insert into material_folder (type, user_id, name, create_time) " +
            "value (#{type}, #{userId}, #{name}, #{createTime})")
    void insert(MaterialFolder folder);
}
