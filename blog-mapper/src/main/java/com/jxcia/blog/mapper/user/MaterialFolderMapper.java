package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.dto.MaterialFolderDto;
import com.jxcia.blog.pojo.entity.MaterialFolder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MaterialFolderMapper {
    /**
     * 用户创建素材文件夹
     * @param folder 文件夹
     */
    @Insert("insert into material_folder (type, user_id, name, create_time) " +
            "value (#{type}, #{userId}, #{name}, #{createTime})")
    void insert(MaterialFolder folder);

    /**
     * 用户更新文件夹
     */
    @Update("update material_folder set name = #{dto.name} " +
            "where id = #{dto.id} and user_id = #{userId}")
    void updateById(MaterialFolderDto dto, Integer userId);
}
