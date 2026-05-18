package com.jxcia.blog.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleSearchVo {
    // 文章编号
    private Integer id;
    // 文章作者编号
    private Integer userId;
    // 文章封面
    private String icon;
    // 文章标题
    private String title;
    // 文章创建日期
    private LocalDateTime createTime;
    // 作者昵称
    private String userNickName;
}
