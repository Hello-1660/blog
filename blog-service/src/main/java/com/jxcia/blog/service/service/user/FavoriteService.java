package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.FavoriteDto;

/**
 * 收藏夹 service
 */
public interface FavoriteService {

    /**
     * 新增收藏夹
     * @param favoriteDto 收藏夹
     */
    void save(FavoriteDto favoriteDto);
}
