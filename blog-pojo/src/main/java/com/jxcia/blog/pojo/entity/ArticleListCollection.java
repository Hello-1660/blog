package com.jxcia.blog.pojo.entity;

import lombok.Data;

import java.util.List;

@Data
public class ArticleListCollection {
    List<Integer> articleIds;
    Integer collectionId;
}
