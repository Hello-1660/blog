package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.ReportDto;

/**
 * 举报 service
 */
public interface ReportService {

    /**
     * 添加举报信息
     * @param reportDto 举报信息
     */
    void save(ReportDto reportDto);
}
