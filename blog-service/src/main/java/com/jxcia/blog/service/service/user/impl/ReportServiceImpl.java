package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.mapper.user.ReportMapper;
import com.jxcia.blog.pojo.dto.ReportDto;
import com.jxcia.blog.pojo.entity.Report;
import com.jxcia.blog.service.service.user.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    /**
     * 用户提交举报
     * @param reportDto
     */
    @Override
    public void report(ReportDto reportDto) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException("请先登录");

        reportMapper.insert(Report.builder()
                .userId(userId)
                .objectType(reportDto.getObjectType())
                .objectId(reportDto.getObjectId())
                .message(reportDto.getMessage() != null ? reportDto.getMessage() : "")
                .createTime(LocalDateTime.now())
                .build());
    }

    /**
     * 用户举报列表
     * @param userId 用户编号
     * @return 用户举报列表
     */
    @Override
    public List<Report> list(Integer userId) {
        return reportMapper.getByUserId(userId);
    }
}
