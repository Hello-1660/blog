package com.jxcia.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
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
    // 管理员创建日期
    private LocalDateTime createTime;
    // 管理员账号状态 0禁用 1启用
    private Integer status;
}
