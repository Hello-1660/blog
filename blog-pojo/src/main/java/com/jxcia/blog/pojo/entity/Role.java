package com.jxcia.blog.pojo.entity;

import lombok.Data;

@Data
public class Role {
    // 角色编号
    private Integer id;
    // 角色名称
    private String name;
    // 角色描述
    private String description;
    // 角色创建日期
    private String createTime;
    // 角色状态
    private Integer status;
}
