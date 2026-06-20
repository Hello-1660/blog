package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportPageVo {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer objectType;
    private Integer objectId;
    private String message;
    private Integer status;
    private String result;
    private String resultAdminName;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
