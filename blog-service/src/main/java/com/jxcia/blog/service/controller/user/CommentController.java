package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.CommentDto;
import com.jxcia.blog.pojo.vo.CommentWithUserVo;
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
    @AuthRequired
    @PostMapping("/save")
    public Result<CommentWithUserVo> save(@RequestBody @Valid CommentDto commentDto) {
        log.info("comment save: {}", commentDto);

        return Result.success(commentService.save(commentDto));
    }

    /**
     * 删除用户评论
     * @param commentId 评论编号
     * @return 无
     */
    @AuthRequired
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
    @AuthRequired
    @PostMapping("/like")
    public Result<Void> like(@NotNull Long commentId) {
        log.info("comment like: {}", commentId);

        commentService.like(commentId);

        return Result.success();
    }

    /**
     * 作者置顶/取消置顶评论
     * @param commentId 评论编号
     * @return true 已置顶，false 已取消
     */
    @AuthRequired
    @PostMapping("/pin/{commentId}")
    public Result<Boolean> pin(@PathVariable Long commentId) {
        log.info("comment pin: {}", commentId);

        return Result.success(commentService.pin(commentId));
    }

    /**
     * 分页查看文章评论
     * @param articleId 文章编号
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 文章评论分页数据
     */
    @Anonymous
    @GetMapping("/detail/{articleId}")
    public Result<PageResult<CommentWithUserVo>> detail(
            @PathVariable Integer articleId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("comment detail: {}, page: {}, size: {}", articleId, pageNum, pageSize);

        return Result.success(commentService.detail(articleId, pageNum, pageSize));
    }
}
