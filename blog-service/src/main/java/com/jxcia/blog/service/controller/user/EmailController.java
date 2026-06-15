package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Email;
import com.jxcia.blog.service.service.user.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    /**
     * 阅读邮件
     * @param id 邮件编号
     * @return 邮件
     */
    @AuthRequired
    @GetMapping("/read/{id}")
    public Result<Email> read(@PathVariable Integer id) {
        log.info("read email id: {}", id);
        return Result.success(emailService.read(id));
    }

    /**
     * 全部已读
     * @return 无
     */
    @AuthRequired
    @GetMapping("/allRead")
    public Result<Void> allRead() {
        log.info("all read email");
        emailService.allRead();
        return Result.success();
    }

    /**
     * 批量删除邮件
     * @param ids 邮件列表
     * @return 无
     */
    @AuthRequired
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody List<Integer> ids) {
        log.info("delete email ids: {}", ids);
        emailService.deleteByEmailList(ids);
        return Result.success();
    }
}
