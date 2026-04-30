package com.jxcia.blog.service.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.service.mapper.user.ArticleMapper;
import com.jxcia.blog.service.service.user.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文章 serviceImpl
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 文章搜索
     *
     * @param articleDto 文章搜索信息
     * @return 文章分页数据
     */
    @Override
    public PageResult<Article> search(ArticleSearchDto articleDto) {
        // 开启分页
        PageHelper.startPage(articleDto.getPageNum(), articleDto.getPageSize());
        // 查询数据
        List<Article> articleList = articleMapper.getByArticleDto(articleDto);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}
