package com.jxcia.blog.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class ArticleMsgVo {
    // 用户是否点赞
    private boolean isLiked;
    // 点赞数
    private Integer LikedNum;
    // 评论数
    private Integer commentNum;
    // 用户添加进收藏夹编号列表
    private List<Integer> favoriteIdList;
}
