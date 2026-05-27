package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ArticleExceptionConstant;
import com.jxcia.blog.common.constant.FavoriteExceptionConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.ArticleException;
import com.jxcia.blog.common.exception.FavoriteException;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.pojo.entity.FavoriteArticle;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.mapper.user.FavoriteMapper;
import com.jxcia.blog.service.service.user.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 新增收藏夹
     *
     * @param favoriteDto 收藏夹
     */
    @Override
    public void save(FavoriteDto favoriteDto) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        if (favoriteDto.getName() == null || favoriteDto.getName().isEmpty())
            throw new FavoriteException(FavoriteExceptionConstant.NAME_CANNOT_NULL);


        List<Favorite> f = favoriteMapper.getByFavoriteDto(favoriteDto);
        if (!f.isEmpty()) throw new FavoriteException(FavoriteExceptionConstant.NAME_CANNOT_EQUALS);

        Favorite favorite = Favorite.builder()
                .userId(userId)
                .name(favoriteDto.getName())
                .createTime(LocalDateTime.now())
                .status(favoriteDto.getStatus())
                .build();

        favoriteMapper.insert(favorite);
    }

    /**
     * 删除收藏夹
     *
     * @param favoriteId 收藏夹编号
     */
    @Override
    public void delete(Long favoriteId) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        Favorite favorite = favoriteMapper.getById(favoriteId);

        if (favorite == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);

        if (!favorite.getUserId().equals(userId))
            throw new FavoriteException(FavoriteExceptionConstant.CANNOT_OTHER_USER_DELETE_FAVORITE);

        favoriteMapper.deleteById(favoriteId);
    }

    /**
     * 新增文章
     *
     * @param favoriteArticle 文章信息
     */
    @Override
    public void addArticle(FavoriteArticle favoriteArticle) {
        if (favoriteArticle.getArticleId() == null) throw new ArticleException(ArticleExceptionConstant.ARTICLE_NOT_FOND);
        if (favoriteArticle.getFavoriteId() == null) throw new FavoriteException(FavoriteExceptionConstant.FAVORITE_NOT_FOUND);

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
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);
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
    public List<Favorite> favoriteList() {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        return favoriteMapper.getListByUserId(userId);
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
}
