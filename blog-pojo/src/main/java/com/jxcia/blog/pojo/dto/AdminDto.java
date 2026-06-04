package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class AdminDto {
    // 管理员编号
    private Integer id;
    // 管理员昵称
    private String nickname;
    // 管理员邮箱
    private String email;
    // 管理员密码
    private String password;
    // 管理员头像
    private String icon;
    // 管理员账号状态 0禁用 1启用
    private Integer status;
}
