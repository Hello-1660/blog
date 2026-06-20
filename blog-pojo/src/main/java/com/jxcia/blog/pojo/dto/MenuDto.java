package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class MenuDto {
    private Integer id;
    private Integer pId;
    private String name;
    private String webNme;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer level;
}
