package com.jxcia.blog.pojo.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Subscribe {
    // 用户关注编号
    private Integer id;
    // 用户编号
    private Integer userId;
    // 关注用户编号
    private Integer subUserId;
    // 是否置顶 0不置顶 1置顶
    private Integer sort;
    // 关注时间
    private LocalDateTime createTime;
}
