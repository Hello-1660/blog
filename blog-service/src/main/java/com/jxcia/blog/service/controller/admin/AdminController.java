package com.jxcia.blog.service.controller.admin;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.AdminDto;
import com.jxcia.blog.pojo.dto.AdminRegisterDto;
import com.jxcia.blog.pojo.dto.AdminLoginDto;
import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.vo.AdminLoginVo;
import com.jxcia.blog.pojo.vo.AdminRegisterVo;
import com.jxcia.blog.pojo.vo.AdminVo;
import com.jxcia.blog.service.service.admin.AdminService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 管理员登录
     * @param adminLoginDto 管理员登录信息
     * @return 管理员信息
     */
    @PostMapping("/login")
    public Result<AdminLoginVo> login(@RequestBody AdminLoginDto adminLoginDto) {
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
    public Result<AdminVo> update(@RequestBody AdminDto adminDto) {
        log.info("admin update: {}", adminDto);
        return Result.success(adminService.update(adminDto));
    }
}
