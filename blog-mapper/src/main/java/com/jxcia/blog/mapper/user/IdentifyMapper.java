package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.UserIdentify;
import com.jxcia.blog.pojo.vo.UserIdentifyVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IdentifyMapper {

    @Select("select ui.name as name, ui.description as description, it.type_value as typeValue " +
            "from identify_user_relation iur " +
            "left join user_identify ui on iur.identify_id = ui.id " +
            "left join identify_type it on ui.type = it.id " +
            "where iur.user_id = #{userId} and it.status = 1")
    UserIdentifyVo getIdentifyVoByUserId(Integer userId);

    @Select("select ui.id, ui.name, ui.description, ui.type, it.type_value as typeValue " +
            "from user_identify ui " +
            "left join identify_type it on ui.type = it.id " +
            "where it.status = 1")
    List<UserIdentifyVo> getAll();

    @Select("select * from user_identify where id = #{id}")
    UserIdentify getById(Integer id);

    @Insert("insert into identify_user_relation (user_id, identify_id) values (#{userId}, #{identifyId})")
    void insertRelation(@Param("userId") Integer userId, @Param("identifyId") Integer identifyId);

    @Delete("delete from identify_user_relation where user_id = #{userId}")
    void deleteRelationByUserId(Integer userId);

    @Select("select iur.identify_id from identify_user_relation iur where iur.user_id = #{userId}")
    Integer getIdentifyIdByUserId(Integer userId);
}
