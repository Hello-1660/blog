package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleUpdateDto extends ArticleDto {
    @NotNull
    private Integer id;
}
