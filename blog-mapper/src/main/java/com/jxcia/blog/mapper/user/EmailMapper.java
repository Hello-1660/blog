package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Email;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmailMapper {
    /**
     * 根据用户编号查询邮件列表
     * @param userId 用户编号
     * @return 邮箱列表
     */
    @Select("select * from email where receiver_id = #{userId}")
    List<Email> getListByUserId(Integer userId);

    /**
     * 批量插入邮件数据
     * @param emailList 邮件列表
     */
    void insertByEmailList(List<Email> emailList);
}
