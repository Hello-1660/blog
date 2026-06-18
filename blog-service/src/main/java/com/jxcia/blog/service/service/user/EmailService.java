package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.entity.Email;

import java.util.List;

/**
 * 邮箱 service
 */
public interface EmailService {

    /**
     * 推送粉丝
     * @param articleId 推送文章编号
     */
    void massSendFans(Integer articleId);

    /**
     * 阅读邮件
     * @param id 邮件编号
     * @return 邮件
     */
    Email read(Integer id);

    /**
     * 全部已读
     */
    void allRead();

    /**
     * 获取当前用户的邮件列表
     * @return 邮件列表
     */
    List<Email> list();

    /**
     * 批量删除邮件
     * @param ids 邮件列表
     */
    void deleteByEmailList(List<Integer> ids);
}
