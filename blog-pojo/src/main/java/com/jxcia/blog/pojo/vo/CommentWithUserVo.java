package com.jxcia.blog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentWithUserVo {
    // 评论编号
    private Long id;
    // 评论用户编号
    private Integer userId;
    // 用户昵称
    private String nickname;
    // 用户头像
    private String icon;
    // 评论文章编号
    private Integer articleId;
    // 父评论编号
    private Long fId;
    // 评论内容
    private String content;
    // 评论排序
    private Integer sort;
    // 评论创建时间
    private LocalDateTime createTime;
}
