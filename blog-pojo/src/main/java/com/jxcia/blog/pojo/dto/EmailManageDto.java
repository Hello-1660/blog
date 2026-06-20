package com.jxcia.blog.pojo.dto;

import lombok.Data;
import java.util.List;

@Data
public class EmailManageDto {
    private List<Integer> receiverIds;
    private String title;
    private String content;
    private Boolean internal;
    private Boolean external;
}
