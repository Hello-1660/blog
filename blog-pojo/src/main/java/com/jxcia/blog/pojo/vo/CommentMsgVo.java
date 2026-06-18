package com.jxcia.blog.pojo.vo;

import lombok.Data;

@Data
public class CommentMsgVo {
    // 评论编号
    private Long id;
    // 评论点赞量
    private Integer likeNum;
    // 当前用户是否点赞过 (0/1)
    private Integer isLiked;
}
