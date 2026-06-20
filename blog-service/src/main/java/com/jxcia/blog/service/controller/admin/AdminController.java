package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.blog.security.metadata.DynamicSecurityMetadataSource;
import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.*;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.pojo.vo.*;
import com.jxcia.blog.service.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员 controller
 */
@RestController
@RequestMapping("/admin")
@Slf4j
@Validated
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;


    /**
     * 管理员登录
     * @param adminLoginDto 管理员登录信息
     * @return 管理员信息
     */
    @PostMapping("/login")
    public Result<AdminLoginVo> login(@RequestBody @Valid AdminLoginDto adminLoginDto) {
        log.info("admin login: {}", adminLoginDto);
        return Result.success(adminService.login(adminLoginDto));
    }

    /**
     * 管理员注册
     * @param adminRegisterDto 管理员注册信息
     * @return 管理员信息
     */
    @PostMapping("/save")
    public Result<AdminRegisterVo> save(@RequestBody @Valid AdminRegisterDto adminRegisterDto) {
        log.info("admin save: {}", adminRegisterDto);
        return Result.success(adminService.save(adminRegisterDto));
    }

    /**
     * 获取管理员账号详细
     * @param id 管理员编号
     * @return 管理员详细
     */
    @GetMapping("/detail/{id}")
    public Result<AdminVo> detail(@PathVariable Integer id) {
        log.info("admin detail: {}", id);
        return Result.success(adminService.detail(id));
    }

    /**
     * 更新管理员账号
     * @param adminDto 管理员信息
     * @return 管理员信息
     */
    @PostMapping("/update")
    public Result<AdminVo> update(@RequestBody @Valid AdminDto adminDto) {
        log.info("admin update: {}", adminDto);
        return Result.success(adminService.update(adminDto));
    }

    /**
     * 获取管理员展示菜单
     * @return 菜单列表
     */
    @GetMapping("/menus")
    public Result<List<Menu>> menus() {
        log.info("admin menus");
        return Result.success(adminService.menuList());
    }

    @GetMapping("/permission/refresh")
    public Result<String> refreshPermission() {
        dynamicSecurityMetadataSource.clearDataSource();
        return Result.success();
    }

    @GetMapping("/list")
    public Result<PageResult<AdminPageVo>> list(@Valid AdminPageDto dto) {
        return Result.success(adminService.list(dto));
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        adminService.delete(id);
        return Result.success();
    }

    @PostMapping("/toggleStatus/{id}")
    public Result<Void> toggleStatus(@PathVariable Integer id) {
        adminService.toggleStatus(id);
        return Result.success();
    }

    @PostMapping("/assignRole")
    public Result<Void> assignRole(@RequestBody @Valid AdminAssignRoleDto dto) {
        adminService.assignRole(dto);
        return Result.success();
    }

    @GetMapping("/roles/{id}")
    public Result<List<Integer>> getRoleIds(@PathVariable Integer id) {
        return Result.success(adminService.getRoleIds(id));
    }
}
