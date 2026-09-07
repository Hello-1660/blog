package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    // 用户昵称
    private String nickname;
    // 用户头像
    private String icon;
    // 用户邮箱
    @Email
    private String email;
    // 用户简介
    private String description;
    // 用户主题编号
    private Integer themeId;
    // 用户喜欢展示界面 0不展示 1展示
    private Integer likeShowStatus;
}
