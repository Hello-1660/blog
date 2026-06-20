package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePageVo {
    private Integer id;
    private String title;
    private Integer userId;
    private String authorName;
    private String categoryName;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
