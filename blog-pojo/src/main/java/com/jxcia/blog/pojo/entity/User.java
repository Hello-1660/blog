package com.jxcia.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    // 用户编号
    private Integer id;
    // 用户昵称
    private String nickname;
    // 用户头像
    private String icon;
    // 用户邮箱
    private String email;
    // 用户密码
    private String password;
    // 用户简介
    private String description;
    // 用户主题编号
    private Integer themeId;
    // 用户创建日期
    private LocalDateTime createTime;
    // 用户喜欢展示界面 0不展示 1展示
    private Integer likeShowStatus;
    // 用户账号状态 0禁用 1启用
    private Integer accountStatus;
}
