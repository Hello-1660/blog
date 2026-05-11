package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.constant.UserLoginExceptionConstant;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.service.mapper.user.CommentMapper;
import com.jxcia.blog.service.service.user.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 评论 serviceImpl
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    /**
     * 添加用户评论
     * @param commentDto 用户评论信息
     */
    @Override
    public void save(CommentDto commentDto) {
        Integer userId = SecurityContextUtil.getId();

        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        // 补充评论信息
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDto, comment);
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());

        commentMapper.insert(comment);
    }
}
