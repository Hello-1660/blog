package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.vo.UserIdentifyVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户身份 mapper
 */
@Mapper
public interface IdentifyMapper {

    @Select("select ui.name as name, ui.description as description, it.type_value as typeValue " +
            "from identify_user_relation iur " +
            "left join user_identify ui on iur.identify_id = ui.id " +
            "left join identify_type it on ui.type = it.id " +
            "where iur.user_id = #{userId} and status = 1")
    UserIdentifyVo getIdentifyVoByUserId(Integer userId);
}
