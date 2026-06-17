package com.jxcia.blog.pojo.vo;

import lombok.Data;

@Data
public class UserMsgVo {
    // 用户粉丝数量
    private Integer fansNum;
    // 用户关注数量
    private Integer subscribeNum;
    // 用户账号身份信息
    private UserIdentifyVo userIdentifyVo;
}
