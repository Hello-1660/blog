package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminRegisterDto {
    // 管理员昵称
    @NotBlank
    private String nickname;
    // 管理员邮箱
    @Email
    private String email;
    // 管理员密码
    @NotBlank
    private String password;
    // 管理员确认密码
    @NotBlank
    private String confirmPassword;
}
