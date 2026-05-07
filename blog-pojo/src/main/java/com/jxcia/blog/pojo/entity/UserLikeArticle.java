package com.jxcia.blog.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserLikeArticle {
    // 用户点赞文章编号
    private Integer id;
    // 用户编号
    private Integer userId;
    // 文章编号
    private Integer articleId;
    // 点赞日期
    private LocalDateTime likeTime;
}
