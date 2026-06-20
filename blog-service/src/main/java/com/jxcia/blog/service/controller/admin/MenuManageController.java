package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.MenuDto;
import com.jxcia.blog.pojo.vo.MenuTreeVo;
import com.jxcia.blog.service.service.admin.MenuManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/menu")
@Slf4j
public class MenuManageController {
    @Autowired
    private MenuManageService menuManageService;

    @GetMapping("/tree")
    public Result<List<MenuTreeVo>> tree() {
        return Result.success(menuManageService.tree());
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid MenuDto dto) {
        menuManageService.save(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid MenuDto dto) {
        menuManageService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        menuManageService.delete(id);
        return Result.success();
    }
}
