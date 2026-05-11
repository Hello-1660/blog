package com.jxcia.blog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    // 评论编号
    private Integer id;
    // 评论用户编号
    private Integer userId;
    // 评论文章编号
    private Integer articleId;
    // 父评论编号
    private Integer fId;
    // 评论内容
    private String content;
    // 评论排序
    private Integer sort;
    // 评论创建时间
    private LocalDateTime createTime;
}
