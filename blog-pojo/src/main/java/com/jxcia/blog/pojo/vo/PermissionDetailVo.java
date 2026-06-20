package com.jxcia.blog.pojo.vo;

import com.jxcia.blog.pojo.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDetailVo {
    private Integer id;
    private String name;
    private String url;
    private String description;
    private LocalDateTime createTime;
    private List<Role> roles;
}
