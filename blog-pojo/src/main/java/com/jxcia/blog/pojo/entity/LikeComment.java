package com.jxcia.blog.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeComment {
    // 用户点赞评论编号
    private Long id;
    // 用户编号
    private Integer userId;
    // 用户评论编号
    private Long userCommentId;
    // 用户评论创建时间
    private LocalDateTime createTime;
}
