package com.jxcia.blog.service.service.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;

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
}
