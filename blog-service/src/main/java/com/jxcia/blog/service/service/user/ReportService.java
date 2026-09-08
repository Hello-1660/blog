package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.ReportDto;
import com.jxcia.blog.pojo.entity.Report;

import java.util.List;

public interface ReportService {
    /**
     * 用户提交举报
     * @param reportDto 用户编号
     */
    void report(ReportDto reportDto);

    /**
     * 用户举报列表
     * @param userId 用户编号
     * @return 举报列表
     */
    List<Report> list(Integer userId);
}
