package com.jxcia.blog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Menu {
    // 菜单编号
    private Integer id;
    // 菜单父级编号
    private Integer pId;
    // 菜单名称
    private String name;
    // 菜单级数
    private Integer level;
    // 菜单展示名称
    private String webNme;
    // 菜单图标
    private String icon;
    // 菜单排序
    private Integer sort;
    // 菜单状态 0隐藏 1显示
    private Integer status;
    // 菜单创建日期
    private LocalDateTime createTime;
}
