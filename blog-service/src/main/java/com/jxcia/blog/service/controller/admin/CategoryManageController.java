package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.CategoryDto;
import com.jxcia.blog.pojo.entity.Category;
import com.jxcia.blog.service.service.admin.CategoryManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryManageController {
    @Autowired
    private CategoryManageService categoryManageService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryManageService.list());
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid CategoryDto dto) {
        categoryManageService.save(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid CategoryDto dto) {
        categoryManageService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        categoryManageService.delete(id);
        return Result.success();
    }
}
