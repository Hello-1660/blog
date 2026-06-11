package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.entity.Category;
import com.jxcia.blog.service.service.user.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口控制器
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取文章分类
     * @return 文章分类列表
     */
    @Anonymous
    @GetMapping("/list")
    public Result<List<Category>> list() {
        log.info("category list");

        return Result.success(categoryService.list());
    }
}
