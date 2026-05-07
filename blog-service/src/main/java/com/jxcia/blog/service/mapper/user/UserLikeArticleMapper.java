package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.UserLikeArticle;
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
}
