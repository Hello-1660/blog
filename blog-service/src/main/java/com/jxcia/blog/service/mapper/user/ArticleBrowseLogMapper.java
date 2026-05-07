package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.ArticleWithBrowseCount;
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
}
