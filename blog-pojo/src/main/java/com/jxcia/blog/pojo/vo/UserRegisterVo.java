package com.jxcia.blog.pojo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterVo {
    private String email;
    private String password;
}
