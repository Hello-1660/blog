package com.jxcia.blog.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HotArticleVo {
    // 推荐文章编号
    private Integer id;
    // 文章作者编号
    private Integer userId;
    // 文章作者昵称
    private String userNickname;
    // 文章封面
    private String icon;
    // 文章标题
    private String title;
    // 文章创建日期
    private LocalDateTime createTime;
    // 文章热度
    private Long value;
}
