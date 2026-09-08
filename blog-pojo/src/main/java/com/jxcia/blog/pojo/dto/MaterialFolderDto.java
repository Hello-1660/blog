package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class MaterialFolderDto {
    // 素材文件夹编号
    private Integer id;
    // 素材文件夹类型 0图片 1音频
    private Integer type;
    // 素材文件夹名称
    private String name;
}
