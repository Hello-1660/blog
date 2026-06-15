package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.service.service.user.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 邮件 controller
 */
@RestController
@RequestMapping("/email")
@Slf4j
public class EmailController {
    @Autowired
    private EmailService emailService;


    /**
     * 推送粉丝文章
     * @param articleId 文章编号
     * @return json
     */
    @AuthRequired
    @GetMapping("/massSendFans/{articleId}")
    public Result<Void> massSendFans(@PathVariable Integer articleId) {
        log.info("mass send fans article id: {}", articleId);
        emailService.massSendFans(articleId);
        return Result.success();
    }
}
