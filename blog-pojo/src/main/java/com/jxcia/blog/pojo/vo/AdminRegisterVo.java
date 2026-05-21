package com.jxcia.blog.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRegisterVo {
    // 管理员邮箱
    private String email;
    // 管理员密码
    private String password;
}
