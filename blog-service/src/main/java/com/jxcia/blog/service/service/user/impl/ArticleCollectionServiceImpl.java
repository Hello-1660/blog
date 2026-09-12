package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.ArticleStatusConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.mapper.user.ArticleCollectionMapper;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.pojo.dto.ArticleCollectionDto;
import com.jxcia.blog.pojo.dto.ArticleCollectionRelationDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.ArticleCollection;
import com.jxcia.blog.pojo.entity.ArticleCollectionRelation;
import com.jxcia.blog.pojo.entity.ArticleListCollection;
import com.jxcia.blog.pojo.vo.ArticleCollectionVo;
import com.jxcia.blog.service.service.user.ArticleCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleCollectionServiceImpl implements ArticleCollectionService {
    @Autowired
    private ArticleCollectionMapper articleCollectionMapper;
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 获取集合信息
     * @param id 集合编号
     * @return 集合信息
     */
    @Override
    public ArticleCollectionVo detail(Integer id) {
        ArticleCollection articleCollection = articleCollectionMapper.getById(id);

        if (articleCollection == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);

        // TODO 返回文章集合第一篇文章的封面
        return null;
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

    /**
     * 获取文章集合中的文章
     * @param id 文章集合编号
     * @return 文章列表
     */
    @Override
    public List<Article> list(Integer id) {
        Integer userId = SecurityContextUtil.getId();

        // 集合
        ArticleCollection collection = articleCollectionMapper.getById(id);
        if (collection == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);


        // 文章列表
        List<Article> articleList = articleCollectionMapper.getArticleListByCollectionId(id);

        // 作者本人直接返回全部作品
        if (collection.getUserId().equals(userId)) return articleList;

        // 其他用户只能浏览已发布作品
        if (!articleList.isEmpty())
            return articleList.stream()
                    .filter(a -> Integer.valueOf(ArticleStatusConstant.PUBLIC).equals(a.getStatus()))
                    .toList();

        // 集合为空直接返回
        return articleList;
    }

    /**
     * 添加文章
     * @param userId 用户编号
     * @param articleCollectionRelationDto 添加文章信息
     */
    @Override
    public void add(Integer userId, ArticleCollectionRelationDto articleCollectionRelationDto) {
        ArticleCollection collection = articleCollectionMapper.getById(articleCollectionRelationDto.getCollectionId());
        Article article = articleMapper.getByArticleId(articleCollectionRelationDto.getArticleId());

        if (collection == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);
        if (article == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);

        // 不能操作非自己的文章和集合
        if (!article.getUserId().equals(userId) || !collection.getUserId().equals(userId))
            throw new ArticleException(ArticleExceptionConstant.ILLEGAL_OPERATION);

        // 不能重复插入
        ArticleCollectionRelation acr = articleCollectionMapper.getACRByArticleIdAndCollectionId(articleCollectionRelationDto);
        if (acr != null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_REPEAT_INSERT_COLLECTION);

        ArticleCollectionRelation articleCollectionRelation = ArticleCollectionRelation
                .builder()
                .collectionId(articleCollectionRelationDto.getCollectionId())
                .articleId(article.getId())
                .addTime(LocalDateTime.now())
                .build();
        // 插入数据
        articleCollectionMapper.insertACRByACR(articleCollectionRelation);
    }

    /**
     * 删除文章集合中的文章
     * @param articleListCollection 文章集合信息
     */
    @Override
    public void remove(Integer userId, ArticleListCollection articleListCollection) {
        ArticleCollection collection = articleCollectionMapper.getById(articleListCollection.getCollectionId());
        if (collection == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_COLLECTION_NOT_FOUND);
        if (!collection.getUserId().equals(userId)) throw new ArticleException(ArticleExceptionConstant.ILLEGAL_OPERATION);

        articleCollectionMapper.removeACRByACRList(articleListCollection);
    }
}
