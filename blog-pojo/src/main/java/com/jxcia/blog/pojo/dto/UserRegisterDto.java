package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegisterDto {
    // 用户昵称
    @NotBlank
    private String nickname;
    // 用户邮箱
    @Email
    private String email;
    // 用户密码
    @NotBlank
    private String password;
    // 用户确认密码
    @NotBlank
    private String confirmPassword;
}
