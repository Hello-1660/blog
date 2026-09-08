package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.dto.ReportPageDto;
import com.jxcia.blog.pojo.entity.Report;
import com.jxcia.blog.pojo.vo.ReportPageVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReportMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into report (user_id, object_type, object_id, message, status, create_time) " +
            "values (#{userId}, #{objectType}, #{objectId}, #{message}, 0, #{createTime})")
    void insert(Report report);

    List<ReportPageVo> getPage(ReportPageDto dto);

    Integer count(ReportPageDto dto);

    @Select("select * from report where id = #{id}")
    Report getById(Integer id);

    void update(Report report);

    @Select("select count(*) from report")
    Integer countTotal();

    @Select("select count(*) from report where status = #{status}")
    Integer countByStatus(Integer status);

    /**
     * 根据用户编号获取举报列表
     * @param userId 用户编号
     * @return 举报列表
     */
    @Select("select * from report where user_id = #{userId}")
    List<Report> getByUserId(Integer userId);
}
