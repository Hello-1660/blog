package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.ReportHandleDto;
import com.jxcia.blog.pojo.dto.ReportPageDto;
import com.jxcia.blog.pojo.vo.ReportPageVo;

public interface ReportManageService {
    PageResult<ReportPageVo> list(ReportPageDto dto);
    ReportPageVo detail(Integer id);
    void handle(ReportHandleDto dto);
}
