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
public class UserPageVo {
    private Integer id;
    private String nickname;
    private String email;
    private String icon;
    private Integer accountStatus;
    private LocalDateTime createTime;
    private Integer articleCount;
    private String identifyName;
    private String identifyTypeValue;
}
