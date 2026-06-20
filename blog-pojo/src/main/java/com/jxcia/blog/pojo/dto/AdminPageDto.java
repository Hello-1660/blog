package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class AdminPageDto {
    private Integer page;
    private Integer size;
    private String keyword;
}
