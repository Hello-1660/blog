package com.jxcia.blog.mapper.user;

import com.jxcia.blog.pojo.dto.UserHistoryDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.ArticleBrowse;
import com.jxcia.blog.pojo.entity.ArticleWithBrowseCount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleBrowseLogMapper {

    /**
     * 获取文章浏览量
     * @param articleList 文章列表，通过文章列表编号查询
     * @return 文章编号及浏览量
     */
    List<ArticleWithBrowseCount> getArticleBrowseCountByArticleList(List<Article> articleList);

    /**
     * 插入用户浏览记录
     * @param articleBrowse 浏览信息
     */
    @Insert("insert into user_article_browse_log (user_id, article_id, create_time) " +
            "value (#{userId}, #{articleId}, #{createTime})")
    void insert(ArticleBrowse articleBrowse);

    /**
     * 根据文章编号和用户编号删除浏览记录
     * @param articleIdList 文章编号
     * @param userId 用户编号
     */
    void updateUserIdByArticleId(List<Integer> articleIdList, Integer userId);

    /**
     * 根据用户编号查询文章编号列表
     * @param userHistoryDto 用户编号
     * @return 文章编号列表
     */
    List<Integer> getIdsByUserHistoryDto(UserHistoryDto userHistoryDto);
}
