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
public class Email {
    // 邮件编号
    private Integer id;
    // 邮件标题
    private String title;
    // 邮件内容
    private String content;
    // 接受方编号
    private Integer receiverId;
    // 发送方编号
    private Integer senderId;
    // 邮件创建时间
    private LocalDateTime createTime;
    // 邮件状态 0未读 1已读
    private Integer status;
}
