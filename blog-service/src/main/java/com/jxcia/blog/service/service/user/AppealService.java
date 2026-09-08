package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.AppealDto;
import com.jxcia.blog.pojo.entity.Appeal;

import java.util.List;

public interface AppealService {
    /**
     * 申诉
     * @param appealDto 申诉内容
     */
    void save(AppealDto appealDto);

    /**
     * 申诉列表
     * @param userId 用户编号
     * @return 申诉列表
     */
    List<Appeal> list(Integer userId);
}
