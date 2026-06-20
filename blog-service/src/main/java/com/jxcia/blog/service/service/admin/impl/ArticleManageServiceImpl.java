package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.ArticleStatusConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.pojo.dto.ArticlePageDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.ArticlePageVo;
import com.jxcia.blog.service.service.admin.ArticleManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleManageServiceImpl implements ArticleManageService {
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageResult<ArticlePageVo> list(ArticlePageDto dto) {
        com.github.pagehelper.PageHelper.startPage(dto.getPage(), dto.getSize());
        List<ArticlePageVo> list = articleMapper.getPage(dto);
        long total = ((com.github.pagehelper.Page<ArticlePageVo>) list).getTotal();
        return new PageResult<>(total, list);
    }

    @Override
    public void toggleStatus(Integer id) {
        Article article = articleMapper.getById(id);
        if (article == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);
        article.setStatus(article.getStatus() == ArticleStatusConstant.PUBLIC ? ArticleStatusConstant.DISABLED : ArticleStatusConstant.PUBLIC);
        articleMapper.update(article);
    }

    @Override
    public void delete(Integer id) {
        articleMapper.deleteByArticleId(id);
    }
}
