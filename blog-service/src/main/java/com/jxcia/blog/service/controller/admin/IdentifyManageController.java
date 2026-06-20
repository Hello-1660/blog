package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.IdentifyAssignDto;
import com.jxcia.blog.pojo.vo.UserIdentifyVo;
import com.jxcia.blog.service.service.admin.IdentifyManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/identify")
@Slf4j
public class IdentifyManageController {
    @Autowired
    private IdentifyManageService identifyManageService;

    @GetMapping("/list")
    public Result<List<UserIdentifyVo>> list() {
        return Result.success(identifyManageService.list());
    }

    @GetMapping("/user/{userId}")
    public Result<UserIdentifyVo> getUserIdentify(@PathVariable Integer userId) {
        return Result.success(identifyManageService.getUserIdentify(userId));
    }

    @PostMapping("/assign")
    public Result<Void> assign(@RequestBody @Valid IdentifyAssignDto dto) {
        identifyManageService.assign(dto);
        return Result.success();
    }

    @DeleteMapping("/remove/{userId}")
    public Result<Void> remove(@PathVariable Integer userId) {
        identifyManageService.remove(userId);
        return Result.success();
    }
}
