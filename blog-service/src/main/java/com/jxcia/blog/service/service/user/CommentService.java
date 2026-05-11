package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.CommentDto;

/**
 * 评论 service
 */
public interface CommentService {

    /**
     * 添加评论
     * @param commentDto 用户评论信息
     */
    void save(CommentDto commentDto);
}
