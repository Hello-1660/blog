package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Email;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    /**
     * 修改邮件
     * @param email 邮件
     */
    void update(Email email);

    /**
     * 根据邮件编号查询邮件
     * @param id 邮件编号
     * @return 邮件
     */
    @Select("select * from email where id = #{id}")
    Email getById(Integer id);

    /**
     * 根据接受用户编号更新阅读状态
     * @param userId 接收方用户编号
     * @param status 阅读状态
     */
    @Update("update email set status = #{status} where receiver_id = #{userId}")
    void UpdateStatusByUserId(Integer userId, Integer status);
}
