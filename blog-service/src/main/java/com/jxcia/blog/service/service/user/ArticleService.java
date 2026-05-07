package com.jxcia.blog.service.service.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.ArticleDto;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.HotArticleVo;

/**
 * 文章 service
 */
public interface ArticleService {

    /**
     * 文章搜索
     * @param articleDto 文章搜索信息
     * @return 文章分页数据
     */
    PageResult<Article> search(ArticleSearchDto articleDto);

    /**
     * 推荐文章列表
     * @param ArticleSearchDto 推荐文章选择信息
     * @return 推荐文章列表
     */
    PageResult<HotArticleVo> hotDetail(ArticleSearchDto ArticleSearchDto);

    /**
     * 新增文章
     * @param articleDto 文章信息
     */
    void save(ArticleDto articleDto);
}
