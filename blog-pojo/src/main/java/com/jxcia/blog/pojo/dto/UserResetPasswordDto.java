package com.jxcia.blog.pojo.dto;

import com.jxcia.blog.pojo.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@PasswordMatch
public class UserResetPasswordDto {
    // 用户邮箱
    @Email
    @NotBlank
    private String email;
    // 用户密码
    @NotBlank
    private String password;
    // 用户确认密码
    @NotBlank
    private String confirmPassword;
    // 验证码
    @NotBlank
    private String verificationCode;
}
