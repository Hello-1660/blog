package com.jxcia.blog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVo {
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
}
