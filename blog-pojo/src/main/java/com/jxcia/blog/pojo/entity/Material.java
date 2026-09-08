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
public class Material {
    // 素材编号
    private Integer id;
    // 素材类型 0图片 1音频
    private Integer type;
    // 用户编号
    private Integer userId;
    // 素材地址
    private String url;
    // 素材名称
    private String name;
    // 分组编号
    private Integer groupId;
    // 上传时间
    private LocalDateTime uploadTime;
}
