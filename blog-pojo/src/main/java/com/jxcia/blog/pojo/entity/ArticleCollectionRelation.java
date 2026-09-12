package com.jxcia.blog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleCollectionRelation {
    // 文章集合关系编号
    private Integer id;
    // 文章编号
    private Integer articleId;
    // 集合编号
    private Integer collectionId;
    // 创建时间
    private LocalDateTime addTime;
}
