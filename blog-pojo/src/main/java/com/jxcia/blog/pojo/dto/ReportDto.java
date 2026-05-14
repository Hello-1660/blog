package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportDto {
    @NotNull
    // 举报类型
    private Integer objectType;
    @NotNull
    // 举报内容编号
    private Integer objectId;
    @NotBlank
    // 举报信息
    private String message;
}
