package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.service.service.user.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @AuthRequired
    @PostMapping
    public Result<Void> report(@RequestParam Integer objectType,
                                @RequestParam Integer objectId,
                                @RequestParam(required = false, defaultValue = "") String message) {
        log.info("report: type={}, objectId={}, message={}", objectType, objectId, message);
        reportService.report(objectType, objectId, message);
        return Result.success();
    }
}
