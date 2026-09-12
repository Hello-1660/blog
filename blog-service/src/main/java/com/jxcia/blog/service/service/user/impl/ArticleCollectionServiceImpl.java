package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.mapper.user.ArticleCollectionMapper;
import com.jxcia.blog.pojo.dto.ArticleCollectionDto;
import com.jxcia.blog.pojo.entity.ArticleCollection;
import com.jxcia.blog.service.service.user.ArticleCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ArticleCollectionServiceImpl implements ArticleCollectionService {
    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;

    /**
     * 获取集合信息
     * @param id 集合编号
     * @return 集合信息
     */
    @Override
    public ArticleCollection detail(Integer id) {
        ArticleCollection articleCollection = articleCollectionMapper.getById(id);

        if (articleCollection == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        return articleCollection;
    }

    /**
     * 创建文章集合
     * @param articleCollectionDto 创建文章集合
     */
    @Override
    public void save(Integer userId, ArticleCollectionDto articleCollectionDto) {
        ArticleCollection articleCollection = ArticleCollection.builder()
                .name(articleCollectionDto.getName())
                .userId(userId)
                .sort(articleCollectionDto.getSort())
                .createTime(LocalDateTime.now())
                .build();

        articleCollectionMapper.insert(articleCollection);
    }
}
