package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.CommentExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.CommentException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.service.mapper.user.ArticleMapper;
import com.jxcia.blog.service.mapper.user.CommentMapper;
import com.jxcia.blog.service.mapper.user.UserLikeCommentMapper;
import com.jxcia.blog.service.service.user.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评论 serviceImpl
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserLikeCommentMapper userLikeCommentMapper;

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

    /**
     * 删除用户评论
     *
     * @param commentId 评论编号
     */
    @Transactional
    @Override
    public void delete(Long commentId) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        Comment comment = commentMapper.get(commentId);
        if (comment == null) throw new CommentException(CommentExceptionConstant.COMMENT_NOT_FOND);
        Article article = articleMapper.getById(comment.getArticleId());

        // 只有作者和本人可以删除评论
        if (userId.equals(article.getUserId()) || userId.equals(comment.getUserId())) {
            // 删除评论
            commentMapper.delete(commentId);
            // 删除点赞信息
            userLikeCommentMapper.deleteByCommentId(commentId);
        } else {
            throw new CommentException(CommentExceptionConstant.OTHER_USER_CANNOT_DEL_COMMENT);
        }
    }
}
