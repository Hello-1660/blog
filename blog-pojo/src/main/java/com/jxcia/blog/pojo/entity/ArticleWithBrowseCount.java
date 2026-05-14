package com.jxcia.blog.pojo.entity;

import lombok.Data;

@Data
public class ArticleWithBrowseCount {
    // 文章编号
    private Integer articleId;
    // 文章浏览量
    private Long browseCount;
}
