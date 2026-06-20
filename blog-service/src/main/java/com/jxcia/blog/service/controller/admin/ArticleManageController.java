package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.ArticlePageDto;
import com.jxcia.blog.pojo.vo.ArticlePageVo;
import com.jxcia.blog.service.service.admin.ArticleManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/article")
@Slf4j
public class ArticleManageController {
    @Autowired
    private ArticleManageService articleManageService;

    @GetMapping("/list")
    public Result<PageResult<ArticlePageVo>> list(@Valid ArticlePageDto dto) {
        return Result.success(articleManageService.list(dto));
    }

    @PostMapping("/toggleStatus/{id}")
    public Result<Void> toggleStatus(@PathVariable Integer id) {
        articleManageService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        articleManageService.delete(id);
        return Result.success();
    }
}
