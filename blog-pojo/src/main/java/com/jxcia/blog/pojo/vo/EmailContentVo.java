package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailContentVo {
    // 文本类型
    private String type;
    // 文本内容
    private String value;
    // 链接内容
    private String text;
    // 路由跳转
    private String route;
}
