package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.common.constant.AdminConstant;
import com.jxcia.blog.common.constant.AdminExceptionConstant;
import com.jxcia.blog.common.constant.AdminRegisterExceptionConstant;
import com.jxcia.blog.common.exception.AdminRegisterException;
import com.jxcia.blog.pojo.dto.AdminRegisterDto;
import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.dto.AdminLoginDto;
import com.jxcia.blog.pojo.vo.AdminLoginVo;
import com.jxcia.blog.pojo.vo.AdminRegisterVo;
import com.jxcia.blog.pojo.vo.AdminVo;
import com.jxcia.blog.service.mapper.admin.AdminMapper;
import com.jxcia.blog.service.service.admin.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 管理员 serviceImpl
 */
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 管理员登录
     *
     * @param adminLoginDto 管理员登录信息
     * @return 管理员登录信息
     */
    @Override
    public AdminLoginVo login(AdminLoginDto adminLoginDto) {
        Admin admin = adminMapper.getByEmail(adminLoginDto.getEmail());

        // 账号校验
        if (admin == null) throw new AdminRegisterException(AdminExceptionConstant.ACCOUNT_NOT_FUND);
        if (!passwordEncoder.matches(adminLoginDto.getPassword(), admin.getPassword()))
            throw new AdminRegisterException(AdminExceptionConstant.PASSWORD_ERROR);
        if (AdminConstant.DISABLE == admin.getStatus()) throw new AdminRegisterException(AdminExceptionConstant.ACCOUNT_DISABLE);

        // token
        String token = jwtTokenUtil.generateAdminToken(admin);

        return AdminLoginVo.builder()
                .id(admin.getId())
                .icon(admin.getIcon())
                .email(admin.getEmail())
                .password(adminLoginDto.getPassword())
                .icon(admin.getIcon())
                .createTime(admin.getCreateTime())
                .status(admin.getStatus())
                .token(token)
                .build();
    }

    /**
     * 管理员注册
     *
     * @param adminRegisterDto 管理员注册信息
     * @return 管理员信息
     */
    @Override
    public AdminRegisterVo save(AdminRegisterDto adminRegisterDto) {
        Admin admin = adminMapper.getByEmail(adminRegisterDto.getEmail());

        if (admin != null) throw new AdminRegisterException(AdminRegisterExceptionConstant.EMAIL_EXIST);
        if (!adminRegisterDto.getPassword().equals(adminRegisterDto.getConfirmPassword()))
            throw new AdminRegisterException(AdminRegisterExceptionConstant.CONFIRM_PASSWORD_NOT_EQUALS);

        Admin build = Admin.builder()
                .nickname(adminRegisterDto.getNickname())
                .email(adminRegisterDto.getEmail())
                .password(passwordEncoder.encode(adminRegisterDto.getPassword()))
                .createTime(LocalDateTime.now())
                .status(AdminConstant.ENABLE)
                .build();

        adminMapper.insert(build);

        return AdminRegisterVo.builder()
                .email(adminRegisterDto.getEmail())
                .password(adminRegisterDto.getPassword())
                .build();
    }

    /**
     * 获取管理员详情
     *
     * @param id 管理员编号
     * @return 管理员详情
     */
    @Override
    public AdminVo detail(Integer id) {
        return adminMapper.getById(id);
    }
}
