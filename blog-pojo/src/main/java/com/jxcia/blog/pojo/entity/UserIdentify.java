package com.jxcia.blog.pojo.entity;

import lombok.Data;

@Data
public class UserIdentify {
    // 身份编号
    private Integer id;
    // 身份名称
    private String name;
    // 身份描述
    private String description;
    // 身份类型
    private Integer type;
}
