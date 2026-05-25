package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginDto {
    // 管理员邮箱
    @NotBlank
    private String email;
    // 管理员密码
    @NotBlank
    private String password;
}
