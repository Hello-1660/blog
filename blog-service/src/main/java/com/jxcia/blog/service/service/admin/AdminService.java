package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.AdminRegisterDto;
import com.jxcia.blog.pojo.entity.AdminLoginDto;
import com.jxcia.blog.pojo.vo.AdminLoginVo;
import com.jxcia.blog.pojo.vo.AdminRegisterVo;

/**
 * 管理员 service
 */
public interface AdminService {

    /**
     * 管理员登录
     * @param adminLoginDto 管理员登录信息
     * @return 管理员登录信息
     */
    AdminLoginVo login(AdminLoginDto adminLoginDto);

    /**
     * 管理员注册
     * @param adminRegisterDto 管理员注册信息
     * @return 管理员信息
     */
    AdminRegisterVo save(AdminRegisterDto adminRegisterDto);
}
