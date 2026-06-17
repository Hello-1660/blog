package com.jxcia.blog.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserHistoryDto {
    // 用户编号
    private Integer userId;
    // 浏览日期
    private LocalDateTime createTime;
    // 查询页数
    private Integer pageNum;
    // 每页查询数量
    private Integer pageSize;
}
