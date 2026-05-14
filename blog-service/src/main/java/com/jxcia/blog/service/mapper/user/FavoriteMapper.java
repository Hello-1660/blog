package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.pojo.entity.FavoriteArticle;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    /**
     * 插入收藏夹记录
     * @param favorite 收藏夹
     */
    @Insert("insert into favorite (user_id, name, create_time, status) " +
            "value (#{userId}, #{name}, #{createTime}, #{status})")
    void insert(Favorite favorite);

    /**
     * 根据收藏夹信息查询收藏夹记录
     * @param favoriteDto 收藏夹信息
     * @return 收藏夹记录
     */
    @Select("select * from favorite where name = #{name}")
    List<Favorite> getByFavoriteDto(FavoriteDto favoriteDto);

    /**
     * 删除收藏夹
     * @param favoriteId 收藏夹编号
     */
    @Delete("delete from favorite where id = #{favoriteId}")
    void deleteById(Long favoriteId);

    /**
     * 根据收藏夹编号查询收藏夹记录
     * @param favoriteId 收藏夹编号
     * @return 收藏夹记录
     */
    @Select("select * from favorite where id = #{favoriteId}")
    Favorite getById(Long favoriteId);

    /**
     * 根据收藏夹文章信息获取收藏夹文章
     * @param favoriteArticle 收藏夹文章信息
     * @return 收藏夹文章
     */
    @Select("select * from favorite_article_relation where favorite_id = #{favoriteId} and article_id = #{articleId}")
    FavoriteArticle getFavoriteArticleByFavoriteArticle(FavoriteArticle favoriteArticle);

    /**
     * 新增收藏夹文章
     * @param favoriteArticle 收藏夹文章
     */
    @Insert("insert into favorite_article_relation (favorite_id, article_id) " +
            "value (#{favoriteId}, #{articleId})")
    void insertFavoriteArticle(FavoriteArticle favoriteArticle);

    /**
     * 根据收藏夹文章信息删除收藏夹文章记录
     * @param favoriteArticle 收藏夹文章信息
     */
    @Delete("delete from favorite_article_relation " +
            "where favorite_id = #{favoriteId} and article_id = #{articleId}")
    void deleteFavoriteArticleByFavoriteArticle(FavoriteArticle favoriteArticle);

    /**
     * 修改收藏夹记录
     * @param favorite 收藏夹信息
     */
    void update(Favorite favorite);

    /**
     * 根据用户编号获取收藏夹列表
     * @param userId 用户编号
     * @return 收藏夹列表
     */
    @Select("select * from favorite where user_id = #{userId}")
    List<Favorite> getListByUserId(Integer userId);

    /**
     * 根据收藏夹编号查询文章
     * @param favoriteId 收藏夹编号
     * @return 文章列表
     */
    @Select("select a.* " +
            "from favorite_article_relation far " +
            "left join article a on far.article_id = a.id")
    List<Article> getArticleByFavoriteId(Integer favoriteId);
}
