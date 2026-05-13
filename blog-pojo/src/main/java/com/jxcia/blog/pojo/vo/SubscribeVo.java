package com.jxcia.blog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscribeVo {
    // 用户关注编号
    private Integer id;
    // 用户编号
    private Integer subUserId;
    // 用户昵称
    private String nickname;
    // 用户头像
    private String icon;
    // 是否置顶 0不置顶 1置顶
    private Integer sort;
    // 关注时间
    private LocalDateTime createTime;
}
