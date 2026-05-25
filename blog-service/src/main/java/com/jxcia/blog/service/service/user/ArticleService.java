package com.jxcia.blog.service.service.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticleDto;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.ArticleSearchVo;
import com.jxcia.blog.pojo.vo.ArticleVo;
import com.jxcia.blog.pojo.vo.HotArticleVo;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 文章 service
 */
public interface ArticleService {

    /**
     * 文章搜索
     * @param articleDto 文章搜索信息
     * @return 文章分页数据
     */
    PageResult<ArticleSearchVo> search(ArticleSearchDto articleDto);

    /**
     * 推荐文章列表
     * @return 推荐文章列表
     */
    List<HotArticleVo> hotDetail();

    /**
     * 新增文章
     * @param articleDto 文章信息
     */
    void save(ArticleDto articleDto);

    /**
     * 删除文章
     * @param articleId 文章编号
     */
    void delete(Integer articleId);

    /**
     * 根据文章编号查询文章
     * @param id 文章编号
     * @return 文章
     */
    ArticleVo getById(Integer id);

    /**
     * 更新文章
     * @param article 更新文章信息
     * @return 文章信息
     */
    Article update(Article article);
}
