package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class AppealDto {
    // 用户编号
    private Integer userId;
    // 申诉类型 0账号 1文章
    private Integer type;
    // 申诉对象编号
    private Integer objectId;
    // 申诉内容
    private String message;
}
