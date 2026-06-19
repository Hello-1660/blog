package com.jxcia.blog.pojo.entity;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class Report {
    private Integer id;
    /** 举报人ID */
    private Integer userId;
    /** 1=评论 2=文章 3=用户 */
    private Integer objectType;
    /** 目标ID */
    private Integer objectId;
    /** 举报原因 */
    private String message;
    /** 0=待处理 1=已处理 */
    private Integer status;
    private String result;
    private Integer resultAdminId;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
