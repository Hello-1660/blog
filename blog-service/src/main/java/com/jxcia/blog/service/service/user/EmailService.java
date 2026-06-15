package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.vo.EmailContentVo;

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
}
