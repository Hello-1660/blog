package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.AppealDto;

public interface AppealService {
    /**
     * 申诉
     * @param appealDto 申诉内容
     */
    void save(AppealDto appealDto);
}
