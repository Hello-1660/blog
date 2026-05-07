package com.jxcia.blog.pojo.entity;

import lombok.Data;

@Data
public class ArticleWithBrowseCount {
    private Integer articleId;
    private Long browseCount;
}
