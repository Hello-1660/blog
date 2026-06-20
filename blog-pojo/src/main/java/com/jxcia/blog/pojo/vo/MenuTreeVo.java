package com.jxcia.blog.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuTreeVo {
    private Integer id;
    private Integer pId;
    private String name;
    private String webNme;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer level;
    private List<MenuTreeVo> children;
}
