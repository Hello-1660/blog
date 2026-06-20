package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.EmailManageDto;
import com.jxcia.blog.service.service.admin.EmailManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/email")
@Slf4j
public class EmailManageController {
    @Autowired
    private EmailManageService emailManageService;

    @PostMapping("/send")
    public Result<Void> send(@RequestBody @Valid EmailManageDto dto) {
        log.info("admin send email: {}", dto);
        emailManageService.send(dto);
        return Result.success();
    }
}
