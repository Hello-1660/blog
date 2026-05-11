package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.Comment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper {

    /**
     * 插入评论
     * @param comment 评论信息
     */
    @Insert("insert into user_comment (user_id, article_id, f_id, content, sort, create_time) " +
            "value (#{userId}, #{articleId}, #{fId}, #{content}, #{sort}, #{createTime})")
    void insert(Comment comment);

    /**
     * 根据评论编号查询评论
     * @param commentId 评论编号
     * @return 评论
     */
    @Select("select * from user_comment where id = #{commentId}")
    Comment get(Long commentId);

    /**
     * 根据评论编号删除评论
     * @param commentId 评论编号
     */
    @Delete("delete from user_comment where id = #{commentId}")
    void delete(Long commentId);
}
