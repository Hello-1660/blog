package com.jxcia.blog.pojo.entity;

import lombok.Data;

@Data
public class FavoriteArticle {
    // 收藏夹文章编号
    private Long id;
    // 收藏夹编号
    private Long favoriteId;
    // 文章编号
    private Integer articleId;
}
