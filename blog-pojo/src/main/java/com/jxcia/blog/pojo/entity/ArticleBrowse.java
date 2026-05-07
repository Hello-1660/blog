package com.jxcia.blog.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ArticleBrowse {
    // 文章浏览记录编号
    private Integer id;
    // 用户编号
    private Integer userId;
    // 文章编号
    private Integer articleId;
    // 创建日期
    private LocalDateTime createTime;
}
