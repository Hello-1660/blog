package com.jxcia.blog.pojo.dto;

import lombok.Data;
import java.util.List;

@Data
public class RoleMenuDto {
    private Integer roleId;
    private List<Integer> menuIds;
}
