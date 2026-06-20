package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.ReportStatusConstant;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.mapper.user.ReportMapper;
import com.jxcia.blog.pojo.dto.ReportHandleDto;
import com.jxcia.blog.pojo.dto.ReportPageDto;
import com.jxcia.blog.pojo.entity.Report;
import com.jxcia.blog.pojo.vo.ReportPageVo;
import com.jxcia.blog.service.service.admin.ReportManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportManageServiceImpl implements ReportManageService {
    @Autowired
    private ReportMapper reportMapper;

    @Override
    public PageResult<ReportPageVo> list(ReportPageDto dto) {
        com.github.pagehelper.PageHelper.startPage(dto.getPage(), dto.getSize());
        List<ReportPageVo> list = reportMapper.getPage(dto);
        long total = ((com.github.pagehelper.Page<ReportPageVo>) list).getTotal();
        return new PageResult<>(total, list);
    }

    @Override
    public ReportPageVo detail(Integer id) {
        Report report = reportMapper.getById(id);
        if (report == null) return null;
        return ReportPageVo.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .objectType(report.getObjectType())
                .objectId(report.getObjectId())
                .message(report.getMessage())
                .status(report.getStatus())
                .result(report.getResult())
                .createTime(report.getCreateTime())
                .finishTime(report.getFinishTime())
                .build();
    }

    @Override
    public void handle(ReportHandleDto dto) {
        Integer adminId = SecurityContextUtil.getId();
        reportMapper.update(Report.builder()
                .id(dto.getReportId())
                .status(ReportStatusConstant.FINISH)
                .result(dto.getResult())
                .resultAdminId(adminId)
                .finishTime(LocalDateTime.now())
                .build());
    }
}
