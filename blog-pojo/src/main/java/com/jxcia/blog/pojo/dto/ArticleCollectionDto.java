package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class ArticleCollectionDto {
    // 集合名称
    private String name;
    // 是否置顶 0不置顶 1置顶
    private Integer sort;
}
