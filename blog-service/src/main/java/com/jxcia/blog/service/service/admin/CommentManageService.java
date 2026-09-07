package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Comment;

public interface CommentManageService {

    /**
     * 根据评论编号获取评论详情
     * @param id 评论编号
     * @return 评论详情
     */
    Comment getCommentById(Long id);
}
