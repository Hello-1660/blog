package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ReportHandleDto;
import com.jxcia.blog.pojo.dto.ReportPageDto;
import com.jxcia.blog.pojo.vo.ReportPageVo;
import com.jxcia.blog.service.service.admin.ReportManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportManageController {
    @Autowired
    private ReportManageService reportManageService;

    @GetMapping("/list")
    public Result<PageResult<ReportPageVo>> list(@Valid ReportPageDto dto) {
        return Result.success(reportManageService.list(dto));
    }

    @GetMapping("/detail/{id}")
    public Result<ReportPageVo> detail(@PathVariable Integer id) {
        return Result.success(reportManageService.detail(id));
    }

    @PostMapping("/handle")
    public Result<Void> handle(@RequestBody @Valid ReportHandleDto dto) {
        reportManageService.handle(dto);
        return Result.success();
    }
}
