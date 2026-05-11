package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentDto {
    // 评论用户编号
    @NotNull
    private Long userId;
    // 评论文章编号
    @NotNull
    private Integer articleId;
    // 父评论编号
    private Long fId;
    // 评论内容
    @NotBlank
    private String content;
}
