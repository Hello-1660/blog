package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.pojo.vo.CommentWithUserVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 根据文章编号查询评论
     * @param articleId 文章编号
     * @return 文章评论列表
     */
    @Select("select uc.id as id, uc.user_id as userId, u.nickname as nickname, u.icon as icon, " +
            "uc.article_id as articleId, uc.f_id as fId, uc.content as content, uc.sort as sort, uc.create_time as createTime " +
            "from user_comment uc " +
            "left join user u on uc.user_id = u.id " +
            "where uc.article_id = #{articleId}")
    List<CommentWithUserVo> getCommentWithUserVoByArticleId(Integer articleId);
}
