package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.FavoriteConstant;
import com.jxcia.blog.common.constant.FavoriteExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.*;
import com.jxcia.blog.mapper.user.UserMapper;
import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.pojo.entity.FavoriteArticle;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.mapper.user.FavoriteMapper;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.service.service.user.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收藏夹 serviceImpl
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 新增收藏夹
     *
     * @param favoriteDto 收藏夹
     */
    @Override
    public void save(FavoriteDto favoriteDto) {
        Integer userId = SecurityContextUtil.getId();

        List<Favorite> f = favoriteMapper.getByFavoriteDto(favoriteDto);
        if (!f.isEmpty()) throw new FavoriteException(FavoriteExceptionConstant.NAME_CANNOT_EQUALS);

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .name(favoriteDto.getName())
                .createTime(LocalDateTime.now())
                .status(favoriteDto.getStatus() != null ? favoriteDto.getStatus() : 0)
                .build();

        favoriteMapper.insert(favorite);
    }

    /**
     * 删除收藏夹
     *
     * @param favoriteId 收藏夹编号
     */
    @Transactional
    @Override
    public void delete(Long favoriteId) {
        Integer userId = SecurityContextUtil.getId();

        Favorite favorite = favoriteMapper.getById(favoriteId);

        if (favorite == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);

        if (!favorite.getUserId().equals(userId))
            throw new FavoriteException(FavoriteExceptionConstant.CANNOT_OTHER_USER_DELETE_FAVORITE);

        // 删除收藏夹
        favoriteMapper.deleteById(favoriteId);
        // 删除收藏夹文章关系
        favoriteMapper.deleteFavoriteArticleByFavoriteId(favoriteId);
    }

    /**
     * 新增文章
     *
     * @param favoriteArticle 文章信息
     */
    @Override
    public void addArticle(FavoriteArticle favoriteArticle) {
        Article article = articleMapper.getById(favoriteArticle.getArticleId());
        if (article == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);

        Favorite favorite = favoriteMapper.getById(favoriteArticle.getFavoriteId());
        if (favorite == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);

        // 查询添加记录
        FavoriteArticle fa = favoriteMapper.getFavoriteArticleByFavoriteArticle(favoriteArticle);

        // 如果存在就移除，没有就新增
        if (fa == null) {
            favoriteMapper.insertFavoriteArticle(favoriteArticle);
        } else {
            favoriteMapper.deleteFavoriteArticleByFavoriteArticle(favoriteArticle);
        }
    }

    /**
     * 更新收藏夹
     *
     * @param favorite 收藏夹信息
     */
    @Override
    public void update(Favorite favorite) {
        Integer userId = SecurityContextUtil.getId();
        if (favorite.getId() == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);

        favorite.setUserId(userId);
        favoriteMapper.update(favorite);
    }

    /**
     * 查看收藏夹列表
     *
     * @return 收藏夹列表
     */
    @Override
    public List<Favorite> favoriteList(Integer id) {
        if (id == null) {
            // 查看自己
            id = SecurityContextUtil.getId();
            if (id == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);
            return favoriteMapper.getListByUserId(id);
        } else {
            // 查看其他用户
            List<Favorite> favoriteList = favoriteMapper.getListByUserId(id);
            // 返回公共列表
            return favoriteList.stream().filter(f -> f.getStatus() != null && f.getStatus() == FavoriteConstant.PUBLIC).toList();
        }
    }

    /**
     * 查看收藏夹文章列表
     *
     * @param favoriteId 收藏夹编号
     * @return 收藏夹文章列表
     */
    @Override
    public List<Article> listArticle(Integer favoriteId) {
        if (favoriteId == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);

        return favoriteMapper.getArticleByFavoriteId(favoriteId);
    }

    /**
     * 移除收藏夹中的文章
     *
     * @param favoriteId 收藏夹编号
     * @param articleId 文章编号
     */
    @Override
    public void removeArticle(Long favoriteId, Integer articleId) {
        Integer userId = SecurityContextUtil.getId();

        Favorite favorite = favoriteMapper.getById(favoriteId);
        if (favorite == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);
        if (!favorite.getUserId().equals(userId))
            throw new FavoriteException(FavoriteExceptionConstant.CANNOT_OTHER_USER_DELETE_FAVORITE);

        FavoriteArticle fa = new FavoriteArticle();
        fa.setFavoriteId(favoriteId);
        fa.setArticleId(articleId);
        favoriteMapper.deleteFavoriteArticleByFavoriteArticle(fa);
    }

    /**
     * 移除收藏夹中所有文章
     *
     * @param favoriteId 收藏夹编号
     */
    @Override
    public void removeAllArticles(Long favoriteId) {
        Integer userId = SecurityContextUtil.getId();

        Favorite favorite = favoriteMapper.getById(favoriteId);
        if (favorite == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);
        if (!favorite.getUserId().equals(userId))
            throw new FavoriteException(FavoriteExceptionConstant.CANNOT_OTHER_USER_DELETE_FAVORITE);

        favoriteMapper.deleteFavoriteArticleByFavoriteId(favoriteId);
    }
}
