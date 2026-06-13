package com.jxcia.blog.pojo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDto {
    // 收藏夹名称
    @NotBlank
    private String name;
    // 收藏夹状态 0私有 1公有
    private Integer status;
}
