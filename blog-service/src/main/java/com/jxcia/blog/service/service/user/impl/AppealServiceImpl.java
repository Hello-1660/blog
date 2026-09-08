package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.common.constant.AppealStatusConstant;
import com.jxcia.blog.mapper.user.AppealMapper;
import com.jxcia.blog.pojo.dto.AppealDto;
import com.jxcia.blog.pojo.entity.Appeal;
import com.jxcia.blog.service.service.user.AppealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppealServiceImpl implements AppealService {
    @Autowired
    private AppealMapper appealMapper;

    /**
     * 申诉
     * @param appealDto 申诉内容
     */
    @Override
    public void save(AppealDto appealDto) {
        Appeal appeal = Appeal.builder()
                .userId(appealDto.getUserId())
                .type(appealDto.getType())
                .objectId(appealDto.getObjectId())
                .message(appealDto.getMessage())
                .status(AppealStatusConstant.RUNNING)
                .createTime(LocalDateTime.now())
                .build();

        appealMapper.insert(appeal);
    }
}
