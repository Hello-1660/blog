package com.jxcia.blog.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterVo {
    // 用户邮箱
    private String email;
    // 用户密码
    private String password;
}
