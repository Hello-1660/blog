package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCollectionVo {
    // 用户文章编号
    private Integer id;
    // 文章集合名称
    private String name;
    // 文章集合创建用户编号
    private Integer userId;
    // 文章集合是否置顶 0不置顶 1置顶
    private Integer sort;
    // 文章集合创建时间w
    private LocalDateTime createTime;
    // 文章集合封面
    private String icon;
}
