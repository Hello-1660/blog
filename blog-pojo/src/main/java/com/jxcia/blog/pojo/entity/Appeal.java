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
public class Appeal {
    // 申诉编号
    private Integer id;
    // 用户编号
    private Integer userId;
    // 申诉类型 0账号 1文章
    private Integer type;
    // 申诉对象编号
    private Integer objectId;
    // 申诉内容
    private String message;
    // 申述状态 0进行中 1已完成
    private Integer status;
    // 申诉结果
    private String result;
    // 申诉处理管理员编号
    private Integer resultAdminId;
    // 申诉创建时间
    private LocalDateTime createTime;
    // 申诉完成时间
    private LocalDateTime finishTime;
}
