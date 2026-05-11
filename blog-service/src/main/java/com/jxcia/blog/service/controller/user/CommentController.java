package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.service.service.user.CommentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论 controller
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加用户评论
     * @param commentDto 用户评论信息
     * @return 无
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid CommentDto commentDto) {
        log.info("comment save: {}", commentDto);

        commentService.save(commentDto);

        return Result.success();
    }
}
