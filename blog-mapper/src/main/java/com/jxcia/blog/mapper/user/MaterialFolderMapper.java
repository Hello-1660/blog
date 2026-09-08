package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.dto.MaterialFolderDto;
import com.jxcia.blog.pojo.entity.MaterialFolder;
import org.apache.ibatis.annotations.*;

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

    /**
     * 删除素材文件夹
     * @param id 文件夹编号
     * @param userId 用户编号
     */
    @Delete("delete from material_folder where id = #{id} and user_id = #{userId}")
    void deleteById(Integer id, Integer userId);
}
