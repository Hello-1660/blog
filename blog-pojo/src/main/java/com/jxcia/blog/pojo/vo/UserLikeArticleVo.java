package com.jxcia.blog.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserLikeArticleVo {
    // 用户编号
    private Integer userId;
    // 文章编号
    private Integer articleId;
    // 点赞日期
    private LocalDateTime likeTime;
    // 文章封面
    private String icon;
    // 文章标题
    private String title;
}
