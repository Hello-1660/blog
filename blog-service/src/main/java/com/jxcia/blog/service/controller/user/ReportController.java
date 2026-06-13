package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ReportDto;
import com.jxcia.blog.service.service.user.ReportService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报 controller
 */
@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 提交举报
     * @param reportDto 举报信息
     * @return 无
     */
    @AuthRequired
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid ReportDto reportDto) {
        log.info("report save: {}", reportDto);

        reportService.save(reportDto);

        return Result.success();
    }
}
