package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ReportStatusConstant;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.pojo.dto.ReportDto;
import com.jxcia.blog.pojo.entity.Report;
import com.jxcia.blog.mapper.user.ReportMapper;
import com.jxcia.blog.service.service.user.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 举报 serviceImpl
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportMapper reportMapper;

    /**
     * 添加举报信息
     *
     * @param reportDto 举报信息
     */
    @Override
    public void save(ReportDto reportDto) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        Report report = Report.builder()
                .objectType(reportDto.getObjectType())
                .objectId(reportDto.getObjectId())
                .message(reportDto.getMessage())
                .userId(userId)
                .status(ReportStatusConstant.PROCESS)
                .createTime(LocalDateTime.now())
                .build();

        reportMapper.insert(report);
    }
}
