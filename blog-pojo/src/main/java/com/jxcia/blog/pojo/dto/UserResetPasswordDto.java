package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class UserResetPasswordDto {
    // 用户邮箱
    private String email;
    // 用户密码
    private String password;
    // 用户确认密码
    private String confirmPassword;
    // 验证码
    private String verificationCode;
}
