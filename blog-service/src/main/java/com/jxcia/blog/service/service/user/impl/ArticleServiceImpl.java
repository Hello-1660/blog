package com.jxcia.blog.service.service.user.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.ArticleStatusConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.mapper.user.*;
import com.jxcia.blog.pojo.dto.ArticleDto;
import com.jxcia.blog.pojo.dto.ArticleSearchDto;
import com.jxcia.blog.pojo.dto.ArticleUpdateDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.pojo.entity.UserLikeArticle;
import com.jxcia.blog.pojo.vo.ArticleMsgVo;
import com.jxcia.blog.pojo.vo.ArticleSearchVo;
import com.jxcia.blog.pojo.vo.ArticleVo;
import com.jxcia.blog.pojo.vo.HotArticleVo;
import com.jxcia.blog.service.service.user.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章 serviceImpl
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserLikeArticleMapper userLikeArticleMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 文章搜索
     *
     * @param articleDto 文章搜索信息
     * @return 文章分页数据
     */
    @Override
    public PageResult<ArticleSearchVo> search(ArticleSearchDto articleDto) {
        // 开启分页
        PageHelper.startPage(articleDto.getPageNum(), articleDto.getPageSize());
        // 查询数据
        articleDto.setStatus(ArticleStatusConstant.PUBLIC);
        List<ArticleSearchVo> articleList = articleMapper.getByArticleDto(articleDto);
        PageInfo<ArticleSearchVo> pageInfo = new PageInfo<>(articleList);

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
        // 删除所有点赞记录
        userLikeArticleMapper.deleteByArticleId(articleId);
        // 删除评论
        commentMapper.deleteByArticleId(articleId);
        // 删除文章
        articleMapper.deleteByArticleId(articleId);
    }

    /**
     * 根据文章编号查询文章
     *
     * @param id 文章编号
     * @return 文章
     */
    @Override
    public ArticleVo getById(Integer id) {
        // 获取文章
        if (id == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);
        Article article = articleMapper.getById(id);
        if (article == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);



        // 获取文章返回对象
        User user = userMapper.getUserById(article.getUserId());
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setUserNickname(user.getNickname());
        articleVo.setUserIcon(user.getIcon());

        // 查询当前用户是否点赞该文章
        Integer userId = SecurityContextUtil.getId();

        // 用户可以查看自己的文章，其他用户只能查看公开文章
        if (articleVo.getUserId().equals(userId)) {
            return articleVo;
        } else {
            if (articleVo.getStatus().equals(ArticleStatusConstant.PUBLIC)) {
                return articleVo;
            } else if (articleVo.getStatus().equals(ArticleStatusConstant.PRIVATE)) {
                throw new ArticleException(ArticleExceptionConstant.ARTICLE_IS_PRIVATE);
            } else if (articleVo.getStatus().equals(ArticleStatusConstant.DISABLED)) {
                throw new ArticleException(ArticleExceptionConstant.ARTICLE_IS_DISABLE);
            }
            throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);
        }
    }

    /**
     * 更新文章
     *
     * @param dto 更新文章信息
     */
    @Override
    public void update(ArticleUpdateDto dto) {
        if (dto.getId() == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);

        Article article = articleMapper.getById(dto.getId());
        if (article == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);

        // 所有权检查
        Integer userId = SecurityContextUtil.getId();
        if (!article.getUserId().equals(userId))
            throw new UserException(UserExceptionConstant.CANNOT_DELETE_OTHER_USER_ARTICLE);

        article.setIcon(dto.getIcon());
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setSort(dto.getSort());
        article.setStatus(dto.getStatus());
        article.setCategoryId(dto.getCategoryId());
        article.setUpdateTime(LocalDateTime.now());

        articleMapper.update(article);
    }

    /**
     * 文章互动信息
     *
     * @param id 文章编号
     * @return 文章信息
     */
    @Override
    public ArticleMsgVo articleMsg(Integer id) {
        ArticleMsgVo articleMsgVo = new ArticleMsgVo();
        articleMsgVo.setLiked(false);
        articleMsgVo.setFavoriteIdList(new ArrayList<>());

        // 查询点赞数量
        articleMsgVo.setLikedNum(userLikeArticleMapper.getCountByArticleId(id));
        // 查询评论数量
        articleMsgVo.setCommentNum(commentMapper.getCountByArticleId(id));

        // 用户没有登录直接返回
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) return articleMsgVo;

        // 查询当前用户是否点赞该文章
        UserLikeArticle ula = userLikeArticleMapper.getByUserLikeArticle(
                UserLikeArticle.builder().userId(userId).articleId(id).build()
        );
        articleMsgVo.setLiked(ula != null);

        // 查询用户收藏到那几个收藏夹
        articleMsgVo.setFavoriteIdList(favoriteMapper.getIdsByArticleIdAndUserId(id, userId));

        return articleMsgVo;
    }

    /**
     * 推荐文章列表
     * @return 推荐文章列表
     */
    @Override
    public List<HotArticleVo> hotDetail() {
        return articleMapper.getHotArticleByArticleSearchDto();
    }

    @Override
    public List<HotArticleVo> followedArticles() {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) return List.of();
        return articleMapper.getFollowedArticles(userId);
    }

    /**
     * 新增文章
     * @param articleDto 文章信息
     */
    @Override
    public Integer save(ArticleDto articleDto) {
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
        return article.getId();
    }
}
