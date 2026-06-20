package com.jxcia.blog.pojo.vo;

import com.jxcia.blog.pojo.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDetailVo {
    private Integer id;
    private String name;
    private String description;
    private Integer status;
    private String createTime;
    private List<Permission> permissions;
}
