package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.LikeComment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserLikeCommentMapper {

    /**
     * 根据用户评论编号删除用户点赞评论记录
     * @param commentId 评论编号
     */
    @Delete("delete from user_like_comment where user_comment_id = #{commendId}")
    void deleteByCommentId(Long commentId);

    /**
     *
     * 根据用户编号和文章编号来查询点赞记录
     * @param likeComment 用户点赞记录
     * @return 查询到的用户点赞记录
     */
    @Select("select * from user_like_comment where user_id = #{userId} and user_comment_id = #{userCommentId}")
    LikeComment getByUserLikeComment(LikeComment likeComment);

    /**
     * 根据用户点赞评论编号删除记录
     * @param id 用户点赞品评论编号
     */
    @Delete("delete from user_like_comment where id = #{id}")
    void delete(Long id);

    /**
     * 插入用户点赞评论记录
     * @param blc 用户点赞评论
     */
    @Insert("insert into user_like_comment (user_id, user_comment_id, create_time)" +
            "value (#{userId}, #{userCommentId}, #{createTime})")
    void insert(LikeComment blc);
}
