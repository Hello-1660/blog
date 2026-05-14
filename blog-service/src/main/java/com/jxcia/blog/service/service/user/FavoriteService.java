package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.FavoriteDto;
import com.jxcia.blog.pojo.entity.Favorite;
import com.jxcia.blog.pojo.entity.FavoriteArticle;

import java.util.List;

/**
 * 收藏夹 service
 */
public interface FavoriteService {

    /**
     * 新增收藏夹
     * @param favoriteDto 收藏夹
     */
    void save(FavoriteDto favoriteDto);

    /**
     * 删除收藏夹
     * @param favoriteId 收藏夹编号
     */
    void delete(Long favoriteId);

    /**
     * 新增文章
     * @param favoriteArticle 文章信息
     */
    void addArticle(FavoriteArticle favoriteArticle);

    /**
     * 更新收藏夹
     * @param favorite 收藏夹信息
     */
    void update(Favorite favorite);

    /**
     * 查看收藏夹列表
     * @return 收藏夹列表
     */
    List<Favorite> favoriteList();
}
