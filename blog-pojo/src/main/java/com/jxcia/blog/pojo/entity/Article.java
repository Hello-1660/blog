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
public class Article {
    // 文章编号
    private Integer id;
    // 文章作者编号
    private Integer userId;
    // 文章封面
    private String icon;
    // 文章标题
    private String title;
    // 文章内容
    private String content;
    // 文章创建日期
    private LocalDateTime createTime;
    // 文章修改日期
    private LocalDateTime updateTime;
    // 文章是否置顶 0不置顶 1置顶
    private Integer sort;
    // 文章状态 0私有 1公开 2封禁
    private Integer status;
    // 文章分类编号
    private Integer categoryId;
}
