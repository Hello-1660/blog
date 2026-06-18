package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.pojo.vo.CommentMsgVo;
import com.jxcia.blog.pojo.vo.CommentWithUserVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper {

    /**
     * 插入评论
     * @param comment 评论信息
     */
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user_comment (user_id, article_id, f_id, content, sort, create_time) " +
            "value (#{userId}, #{articleId}, #{fId}, #{content}, #{sort}, #{createTime})")
    void insert(Comment comment);

    /**
     * 根据评论编号查询评论（含用户信息）
     * @param commentId 评论编号
     * @return 评论及用户信息
     */
    @Select("select uc.id as id, uc.user_id as userId, u.nickname as nickname, u.icon as icon, " +
            "uc.article_id as articleId, uc.f_id as fId, uc.content as content, uc.sort as sort, uc.create_time as createTime " +
            "from user_comment uc " +
            "left join user u on uc.user_id = u.id " +
            "where uc.id = #{commentId}")
    CommentWithUserVo getCommentWithUserVoById(Long commentId);

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
     * 删除父评论的所有子评论
     * @param fId 父评论编号
     */
    @Delete("delete from user_comment where f_id = #{fId}")
    void deleteByFId(Long fId);

    /**
     * 根据父评论编号查询所有子评论 ID
     * @param fId 父评论编号
     * @return 子评论 ID 列表
     */
    @Select("select id from user_comment where f_id = #{fId}")
    List<Long> getIdsByFId(Long fId);

    /**
     * 更新评论排序值（置顶/取消置顶）
     * @param id 评论编号
     * @param sort 排序值，null 为取消置顶
     */
    @Update("update user_comment set sort = #{sort} where id = #{id}")
    void updateSort(@Param("id") Long id, @Param("sort") Integer sort);

    /**
     * 根据文章编号查询评论
     * @param articleId 文章编号
     * @return 文章评论列表
     */
    @Select("select uc.id as id, uc.user_id as userId, u.nickname as nickname, u.icon as icon, " +
            "uc.article_id as articleId, uc.f_id as fId, uc.content as content, uc.sort as sort, uc.create_time as createTime " +
            "from user_comment uc " +
            "left join user u on uc.user_id = u.id " +
            "where uc.article_id = #{articleId} " +
            "order by case when uc.sort > 0 then 0 else 1 end, " +
            "uc.sort desc, " +
            "case when uc.f_id is null then 0 else 1 end, " +
            "case when uc.f_id is null then uc.create_time end desc, " +
            "uc.create_time asc")
    List<CommentWithUserVo> getCommentWithUserVoByArticleId(Integer articleId);

    /**
     * 根基文章编号查询文章评论记录数
     * @param id 文章编号
     * @return 文章评论记录数量
     */
    @Select("select count(*) from user_comment where article_id = #{id}")
    Integer getCountByArticleId(Integer id);

    /**
     * 根据评论编号列表查询用户评论点赞消息
     * @param cIdList 评论编号列表
     * @return 用户评论点赞消息
     */
    List<CommentMsgVo> getCommentLikeNumByCommentIds(@Param("cIdList") List<Long> cIdList, @Param("userId") Integer userId);
}
