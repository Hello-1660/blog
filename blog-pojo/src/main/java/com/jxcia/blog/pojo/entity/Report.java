package com.jxcia.blog.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Report {
    // 举报编号
    private Integer id;
    // 举报类型
    private Integer objectType;
    // 举报内容编号
    private Integer objectId;
    // 举报信息
    private String message;
    // 举报用户编号
    private Integer userId;
    // 举报状态
    private Integer status;
    // 举报结果
    private String result;
    // 处理举报管理员编号
    private Integer resultAdminId;
    // 举报日期
    private LocalDateTime createTime;
    // 举报完成日期
    private LocalDateTime finishTime;
}
