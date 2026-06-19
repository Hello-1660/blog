package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.mapper.user.ReportMapper;
import com.jxcia.blog.pojo.entity.Report;
import com.jxcia.blog.service.service.user.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Override
    public void report(Integer objectType, Integer objectId, String message) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException("请先登录");

        reportMapper.insert(Report.builder()
                .userId(userId)
                .objectType(objectType)
                .objectId(objectId)
                .message(message != null ? message : "")
                .createTime(LocalDateTime.now())
                .build());
    }
}
