package com.jxcia.blog.pojo.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteArticle {
    // 收藏夹文章编号
    private Long id;
    // 收藏夹编号
    @NotNull
    private Long favoriteId;
    // 文章编号
    @NotNull
    private Integer articleId;
}
