package com.jxcia.blog.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVisitVo {
    // 用户编号
    private Integer id;
    // 用户邮箱
    private String email;
    // 用户昵称
    private String nickname;
    // 用户头像
    private String icon;
    // 用户简介
    private String description;
    // 用户主题
    private Integer themeId;
    // 用户创建时间
    private LocalDateTime createTime;
    // 用户喜欢列表展示状态
    private Integer likeShowStatus;
    // 用户关注列表
    private List<SubscribeVo> subscribeList;
    // 用户喜欢列表
    private List<UserLikeArticleVo> userLikeArticleList;
}
