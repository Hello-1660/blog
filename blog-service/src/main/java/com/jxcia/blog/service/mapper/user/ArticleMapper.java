package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文章 mapper
 */
@Mapper
public interface ArticleMapper {

    /**
     * 根据 articleDto 分页查询文章
     * @param articleDto 查询文章信息
     * @return 文章列表
     */
    List<Article> getByArticleDto(ArticleSearchDto articleDto);

    /**
     * 插入文章
     * @param article 文章
     */
    @Insert("insert into article (user_id, icon, title, content, create_time, update_time, sort, status, category_id) " +
            "value (#{userId}, #{icon}, #{title}, #{content}, #{createTime}, #{updateTime}, #{sort}, #{status}, #{categoryId})")
    void insert(Article article);

    /**
     * 根据用户编号查询文章
     * @param userId 用户编号
     * @return 文章列表
     */
    @Select("select * from article where user_id = #{userId} order by create_time desc")
    List<Article> getByUserId(Integer userId);

    /**
     * 根据文章编号列表查询文章
     * @param articleIdList 文章编号列表
     * @return 文章列表
     */
    List<Article> getByArticleIds(List<Integer> articleIdList);

    /**
     * 根据文章编号删除文章
     * @param articleId 文章编号
     */
    @Delete("delete from article where id = #{articleId}")
    void deleteByArticleId(Integer articleId);

    /**
     * 根据用户编号查询文章
     * @param articleId 文章编号
     * @return 文章
     */
    @Select("select * from article where id = #{articleId}")
    Article getByArticleId(Integer articleId);
}
