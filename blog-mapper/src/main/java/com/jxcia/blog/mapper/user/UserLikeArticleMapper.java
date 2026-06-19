package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.entity.UserLikeArticle;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserLikeArticleMapper {

    /**
     * 查询用户点赞文章编号列表
     * @param userId 用户编号
     * @return 文章编号列表
     */
    @Select("select * from user_like_article where user_id = #{userId}")
    List<UserLikeArticle> getArticleIdsByUserId(Integer userId);

    /**
     * 插入用户点赞文章记录
     * @param userLikeArticle 用户点赞文章记录
     */
    @Insert("insert into user_like_article (user_id, article_id, like_time)" +
            "value (#{userId}, #{articleId}, #{likeTime})")
    void insert(UserLikeArticle userLikeArticle);

    /**
     * 根据用户点赞记录查询用户点赞记录
     * @param userLikeArticle 用户点赞记录
     * @return 用户点赞记录
     */
    @Select("select * from user_like_article where user_id = #{userId} and article_id = #{articleId}")
    UserLikeArticle getByUserLikeArticle(UserLikeArticle userLikeArticle);

    /**
     * 根据用户点赞记录删除用户点赞记录
     * @param userLikeArticle 用户点赞记录
     */
    @Delete("delete from user_like_article where user_id = #{userId} and article_id = #{articleId}")
    void delete(UserLikeArticle userLikeArticle);

    @Delete("delete from user_like_article where article_id = #{articleId}")
    void deleteByArticleId(Integer articleId);

    /**
     * 根据文章编号查询文章点赞记录数量
     * @param id 文章编号
     * @return 文章点赞记录数量
     */
    @Select("select count(*) from user_like_article where article_id = #{id}")
    Integer getCountByArticleId(Integer id);
}
