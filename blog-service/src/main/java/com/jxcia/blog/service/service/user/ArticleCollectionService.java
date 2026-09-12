package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.ArticleCollectionDto;
import com.jxcia.blog.pojo.dto.ArticleCollectionRelationDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.ArticleCollection;
import com.jxcia.blog.pojo.entity.ArticleListCollection;
import com.jxcia.blog.pojo.vo.ArticleCollectionVo;

import java.util.List;

public interface ArticleCollectionService {
    /**
     * 获取集合信息
     * @param id 集合编号
     * @return 集合信息
     */
    ArticleCollectionVo detail(Integer id);

    /**
     * 创建文章集合
     * @param articleCollectionDto 创建文章集合
     */
    void save(Integer userId, ArticleCollectionDto articleCollectionDto);

    /**
     * 获取文章集合中的文章
     * @param id 文章集合编号
     * @return 文章列表
     */
    List<Article> list(Integer id);

    /**
     * 添加文章
     * @param userId 用户编号
     * @param articleCollectionRelationDto 添加文章信息
     */
    void add(Integer userId, ArticleCollectionRelationDto articleCollectionRelationDto);

    /**
     * 删除文章集合中的文章
     * @param articleListCollection 文章集合信息
     */
    void remove(Integer userId, ArticleListCollection articleListCollection);

    /**
     * 修改文章集合
     * @param userId 用户编号
     * @param articleCollection 文章集合信息
     */
    void update(Integer userId, ArticleCollection articleCollection);

    /**
     * 删除文章集合
     * @param userId 用户编号
     * @param id 集合编号
     */
    void delete(Integer userId, Integer id);
}
