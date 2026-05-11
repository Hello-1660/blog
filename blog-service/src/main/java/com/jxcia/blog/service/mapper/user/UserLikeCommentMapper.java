package com.jxcia.blog.service.mapper.user;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLikeCommentMapper {

    /**
     * 根据用户评论编号删除用户点赞评论记录
     * @param commentId 评论编号
     */
    @Delete("delete from user_like_comment where user_comment_id = #{commendId}")
    void deleteByCommentId(Long commentId);
}
