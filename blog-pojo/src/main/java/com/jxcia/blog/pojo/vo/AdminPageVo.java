package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPageVo {
    private Integer id;
    private String nickname;
    private String email;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
    private String roles;
}
