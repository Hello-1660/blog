package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.exception.UserException;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ReportDto;
import com.jxcia.blog.pojo.entity.Label;
import com.jxcia.blog.pojo.entity.Report;
import com.jxcia.blog.service.service.user.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 用户提交举报
     * @param reportDto 举报内容
     * @return 无
     */
    @PostMapping
    public Result<Void> report(ReportDto reportDto) {
        log.info("report: {}", reportDto);
        reportService.report(reportDto);
        return Result.success();
    }

    /**
     * 用户举报列表
     * @return 举报列表
     */
    @GetMapping("/list")
    public Result<List<Report>> list() {
        log.info("report list");

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserException(UserExceptionConstant.USER_NOT_LOGIN);

        return Result.success(reportService.list(userId));
    }
}
