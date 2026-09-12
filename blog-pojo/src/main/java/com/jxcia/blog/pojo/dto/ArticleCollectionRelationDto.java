package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class ArticleCollectionRelationDto {
    // 文章编号
    private Integer articleId;
    // 集合编号
    private Integer collectionId;
}
