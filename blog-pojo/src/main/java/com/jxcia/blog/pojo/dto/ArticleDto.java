package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticleDto {
    // 文章封面
    private String icon;
    // 文章标题
    @NotBlank
    private String title;
    // 文章内容
    private String content;
    // 文章排序 0不置顶 1置顶
    private Integer sort;
    // 文章状态 0私有 1公开 2封禁
    private Integer status;
    // 文章分类编号
    @NotNull
    private Integer categoryId;
}
