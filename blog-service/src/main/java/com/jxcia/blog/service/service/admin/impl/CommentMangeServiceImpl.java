package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.exception.CommentException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.mapper.user.CommentMapper;
import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.service.service.admin.CommentManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentMangeServiceImpl implements CommentManageService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 根据评论编号获取评论详情
     *
     * @param id 评论编号
     * @return 评论详情
     */
    @Override
    public Comment getCommentById(Long id) {
        if (id == null) throw new CommentException("评论不存在");
        return commentMapper.get(id);
    }
}
