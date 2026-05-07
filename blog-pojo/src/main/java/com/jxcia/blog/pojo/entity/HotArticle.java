package com.jxcia.blog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HotArticle {
    // 推荐文章编号
    private Integer id;
    // 文章编号
    private Integer articleId;
    // 推荐文章排序 0置顶 1不置顶
    private Integer sort;
    // 推荐文章创建日期
    private LocalDateTime createTime;
    // 推荐文章展示状态 0展示 1不展示
    private Integer status;
}
