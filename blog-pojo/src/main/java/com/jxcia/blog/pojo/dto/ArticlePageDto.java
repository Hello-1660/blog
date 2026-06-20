package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class ArticlePageDto {
    private Integer page;
    private Integer size;
    private String keyword;
    private Integer status;
    private Integer categoryId;
}
