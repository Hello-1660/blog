package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.pojo.vo.CommentWithUserVo;

import java.util.List;

/**
 * 评论 service
 */
public interface CommentService {

    /**
     * 添加评论
     * @param commentDto 用户评论信息
     */
    void save(CommentDto commentDto);

    /**
     * 删除用户评论
     * @param commentId 评论编号
     */
    void delete(Long commentId);

    /**
     * 用户点赞评论
     * @param commentId 用户评论编号
     */
    void like(Long commentId);

    /**
     * 查看文章评论
     * @param articleId 文章编号
     * @return 文章评论列表
     */
    List<CommentWithUserVo> detail(Integer articleId);
}
