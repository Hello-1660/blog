package com.jxcia.blog.pojo.vo;

import lombok.Data;

@Data
public class UserIdentifyVo {
    // 用户编号
    private Integer id;
    // 身份名称
    private String name;
    // 身份描述
    private String description;
    // 身份类型
    private String typeValue;
}
