package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.ArticleCollectionDto;
import com.jxcia.blog.pojo.entity.ArticleCollection;

public interface ArticleCollectionService {
    /**
     * 获取集合信息
     * @param id 集合编号
     * @return 集合信息
     */
    ArticleCollection detail(Integer id);

    /**
     * 创建文章集合
     * @param articleCollectionDto 创建文章集合
     */
    void save(Integer userId, ArticleCollectionDto articleCollectionDto);
}
