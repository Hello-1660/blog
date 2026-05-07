package com.jxcia.blog.service.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleStatusConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.ArticleDto;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.UserLikeArticle;
import com.jxcia.blog.pojo.vo.HotArticleVo;
import com.jxcia.blog.service.mapper.user.ArticleBrowseLogMapper;
import com.jxcia.blog.service.mapper.user.ArticleMapper;
import com.jxcia.blog.service.mapper.user.UserLikeArticleMapper;
import com.jxcia.blog.service.mapper.user.UserMapper;
import com.jxcia.blog.service.service.user.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章 serviceImpl
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleBrowseLogMapper articleBrowseLogMapper;
    @Autowired
    private UserLikeArticleMapper userLikeArticleMapper;
    @Autowired
    private UserMapper userMapper;

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
        articleDto.setStatus(ArticleStatusConstant.PUBLIC);
        List<Article> articleList = articleMapper.getByArticleDto(articleDto);
        PageInfo<Article> pageInfo = new PageInfo<>(articleList);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 删除文章
     *
     * @param articleId 文章编号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer articleId) {
        Integer userId = SecurityContextUtil.getId();

        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);
        if (articleId == null) throw new UserException(UserExceptionConstant.ARTICLE_NOT_EXISTS);

        Article article = articleMapper.getByArticleId(articleId);

        // 不能删除其他用户文章
        if (!article.getUserId().equals(userId)) throw new UserException(UserExceptionConstant.CANNOT_DELETE_OTHER_USER_ARTICLE);

        // 删除文章记录
        articleMapper.deleteByArticleId(articleId);
        // 删除点赞记录
        userLikeArticleMapper.delete(UserLikeArticle.builder().articleId(articleId).userId(userId).build());
        // TODO 删除评论记录
        // 删除浏览记录
        articleBrowseLogMapper.deleteByArticleId(articleId);
    }

    /**
     * 推荐文章列表
     * @return 推荐文章列表
     */
    @Override
    public List<HotArticleVo> hotDetail() {
        return articleMapper.getHotArticleByArticleSearchDto();
    }

    /**
     * 新增文章
     * @param articleDto 文章信息
     */
    @Override
    public void save(ArticleDto articleDto) {
        Integer userId = SecurityContextUtil.getId();
        LocalDateTime now = LocalDateTime.now();

        Article article = Article.builder()
                .userId(userId)
                .icon(articleDto.getIcon())
                .title(articleDto.getTitle())
                .content(articleDto.getContent())
                .createTime(now)
                .updateTime(now)
                .sort(articleDto.getSort())
                .status(articleDto.getStatus())
                .categoryId(articleDto.getCategoryId())
                .build();

        articleMapper.insert(article);
    }
}
