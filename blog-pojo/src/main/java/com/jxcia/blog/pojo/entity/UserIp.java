package com.jxcia.blog.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserIp {
    // ip地址
    private String ip;
    // 位置信息
    private String address;
}
