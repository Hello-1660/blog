package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Material;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MaterialMapper {

    /**
     * 插入素材记录
     * @param material
     */
    @Insert("insert into material (type, user_id, url, name, group_id, upload_time) " +
            "value (#{type}, #{userId}, #{url}, #{name}, #{groupId}, #{uploadTime})")
    void insert(Material material);

    /**
     * 根据素材编号列表和用户编号删除素材
     * 用户编号是防止误删他人素材
     * @param userId 用户编号
     * @param ids 素材编号列表
     */
    void deleteByIds(Integer userId, List<Integer> ids);
}
