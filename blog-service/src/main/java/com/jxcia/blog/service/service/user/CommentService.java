package com.jxcia.blog.service.service.user;

import com.jxcia.blog.common.result.PageResult;
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
     * @return 新建的评论及用户信息
     */
    CommentWithUserVo save(CommentDto commentDto);

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
     * 作者置顶/取消置顶评论
     * @param commentId 评论编号
     * @return true 为已置顶，false 为已取消
     */
    boolean pin(Long commentId);

    /**
     * 分页查看文章评论
     * @param articleId 文章编号
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 文章评论分页数据
     */
    PageResult<CommentWithUserVo> detail(Integer articleId, Integer pageNum, Integer pageSize);
}
