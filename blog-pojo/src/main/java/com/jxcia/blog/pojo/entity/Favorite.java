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
public class Favorite {
    // 收藏夹编号
    private Long id;
    // 用户编号
    private Integer userId;
    // 收藏夹名称
    private String name;
    // 收藏夹创建日期
    private LocalDateTime createTime;
    // 收藏夹状态 0私有 1公有
    private Integer status;
}
