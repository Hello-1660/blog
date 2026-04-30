package com.jxcia.blog.service.mapper.user;

import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import org.apache.ibatis.annotations.Mapper;

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
}
