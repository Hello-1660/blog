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
public class MaterialFolder {
    // 素材文件夹编号
    private Integer id;
    // 素材文件夹类型 0图片 1音频
    private Integer type;
    // 用户编号
    private Integer userId;
    // 素材文件夹名称
    private String name;
    // 创建时间
    private LocalDateTime createTime;
}
