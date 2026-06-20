package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.UserPageDto;
import com.jxcia.blog.pojo.vo.UserPageVo;
import com.jxcia.blog.service.service.admin.UserManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class UserManageController {
    @Autowired
    private UserManageService userManageService;

    @GetMapping("/list")
    public Result<PageResult<UserPageVo>> list(@Valid UserPageDto dto) {
        log.info("admin user list: {}", dto);
        return Result.success(userManageService.list(dto));
    }

    @GetMapping("/detail/{id}")
    public Result<UserPageVo> detail(@PathVariable Integer id) {
        return Result.success(userManageService.detail(id));
    }

    @PostMapping("/toggleStatus/{id}")
    public Result<Void> toggleStatus(@PathVariable Integer id) {
        userManageService.toggleStatus(id);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userManageService.delete(id);
        return Result.success();
    }
}
