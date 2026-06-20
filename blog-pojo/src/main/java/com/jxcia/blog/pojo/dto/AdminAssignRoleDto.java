package com.jxcia.blog.pojo.dto;

import lombok.Data;
import java.util.List;

@Data
public class AdminAssignRoleDto {
    private Integer adminId;
    private List<Integer> roleIds;
}
