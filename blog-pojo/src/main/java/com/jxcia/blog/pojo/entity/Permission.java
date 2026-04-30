package com.jxcia.blog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Permission {
    // 权限编号
    private Integer id;
    // 权限名称
    private String name;
    // 权限路径
    private String url;
    // 权限创建日期
    private LocalDateTime createTime;
    // 权限描述
    private String description;
}
