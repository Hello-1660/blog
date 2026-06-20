package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.PermissionDto;
import com.jxcia.blog.pojo.dto.RolePermissionDto;
import com.jxcia.blog.pojo.entity.Permission;
import com.jxcia.blog.pojo.vo.PermissionDetailVo;
import com.jxcia.blog.service.service.admin.PermissionManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/permission")
@Slf4j
public class PermissionManageController {
    @Autowired
    private PermissionManageService permissionManageService;

    @GetMapping("/list")
    public Result<List<Permission>> list() {
        return Result.success(permissionManageService.list());
    }

    @GetMapping("/detail/{id}")
    public Result<PermissionDetailVo> detail(@PathVariable Integer id) {
        return Result.success(permissionManageService.detail(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid PermissionDto dto) {
        permissionManageService.save(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid PermissionDto dto) {
        permissionManageService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        permissionManageService.delete(id);
        return Result.success();
    }

    @PostMapping("/assignPermission")
    public Result<Void> assignPermission(@RequestBody @Valid RolePermissionDto dto) {
        permissionManageService.assignPermission(dto);
        return Result.success();
    }
}
