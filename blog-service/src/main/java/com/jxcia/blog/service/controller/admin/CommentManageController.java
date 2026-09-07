package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Comment;
import com.jxcia.blog.service.service.admin.CommentManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
@Slf4j
public class CommentManageController {

    @Autowired
    private CommentManageService commentManageService;

    /**
     * 根据评论 id 获取评论详情
     * @param id 评论编号
     * @return 评论详情
     */
    @GetMapping("/delete/{id}")
    public Result<Comment> detail(@PathVariable Long id) {
        log.info("get comment detail by id: {}", id);

        return Result.success(commentManageService.getCommentById(id));
    }
}
