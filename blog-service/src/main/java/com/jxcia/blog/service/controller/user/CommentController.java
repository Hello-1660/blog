package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.service.service.user.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 评论 controller
 */
@RestController
@RequestMapping("/comment")
@Slf4j
@Validated
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

    /**
     * 删除用户评论
     * @param commentId 评论编号
     * @return 无
     */
    @DeleteMapping("/delete/{commentId}")
    public Result<Void> delete(@PathVariable Long commentId) {
        log.info("comment delete: {}", commentId);

        commentService.delete(commentId);

        return Result.success();
    }

    /**
     * 用户点赞评论
     * @param commentId 用户评论编号
     * @return 无
     */
    @PostMapping("/like")
    public Result<Void> like(@NotNull Long commentId) {
        log.info("comment like: {}", commentId);

        commentService.like(commentId);

        return Result.success();
    }
}
