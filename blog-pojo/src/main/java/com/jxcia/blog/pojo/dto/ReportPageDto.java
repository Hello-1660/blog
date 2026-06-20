package com.jxcia.blog.pojo.dto;

import lombok.Data;

@Data
public class ReportPageDto {
    private Integer page;
    private Integer size;
    private Integer status;
    private Integer objectType;
}
