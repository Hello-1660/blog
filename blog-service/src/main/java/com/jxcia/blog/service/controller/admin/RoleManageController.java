package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.RoleDto;
import com.jxcia.blog.pojo.dto.RolePermissionDto;
import com.jxcia.blog.pojo.entity.Role;
import com.jxcia.blog.pojo.vo.RoleDetailVo;
import com.jxcia.blog.service.service.admin.RoleManageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/role")
@Slf4j
public class RoleManageController {
    @Autowired
    private RoleManageService roleManageService;

    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleManageService.list());
    }

    @GetMapping("/detail/{id}")
    public Result<RoleDetailVo> detail(@PathVariable Integer id) {
        return Result.success(roleManageService.detail(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid RoleDto dto) {
        roleManageService.save(dto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid RoleDto dto) {
        roleManageService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        roleManageService.delete(id);
        return Result.success();
    }

    @PostMapping("/assignPermission")
    public Result<Void> assignPermission(@RequestBody @Valid RolePermissionDto dto) {
        roleManageService.assignPermission(dto);
        return Result.success();
    }
}
