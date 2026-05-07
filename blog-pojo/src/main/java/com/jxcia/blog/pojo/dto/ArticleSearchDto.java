package com.jxcia.blog.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleSearchDto {
    // 作者名称
    private String userNickname;
    // 文章标题
    private String title;
    // 分类编号
    private Integer categoryId;
    // 创建日期
    private LocalDateTime createTime;
    // 查询页数
    private Integer pageNum;
    // 每页文章数量
    private Integer pageSize;
    // 查询状态 0私有 1公开 2封禁
    private Integer status;
}
