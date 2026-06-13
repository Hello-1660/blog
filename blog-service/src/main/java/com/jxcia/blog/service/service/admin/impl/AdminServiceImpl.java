package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.enums.AccountType;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.AdminConstant;
import com.jxcia.blog.common.constant.AdminExceptionConstant;
import com.jxcia.blog.common.constant.AdminRegisterExceptionConstant;
import com.jxcia.blog.common.exception.AdminRegisterException;
import com.jxcia.blog.mapper.admin.MenuMapper;
import com.jxcia.blog.pojo.dto.AdminDto;
import com.jxcia.blog.pojo.dto.AdminRegisterDto;
import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.dto.AdminLoginDto;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.pojo.vo.AdminLoginVo;
import com.jxcia.blog.pojo.vo.AdminRegisterVo;
import com.jxcia.blog.pojo.vo.AdminVo;
import com.jxcia.blog.mapper.admin.AdminMapper;
import com.jxcia.blog.service.service.admin.AdminService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired
    private MenuMapper menuMapper;

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
        String accessToken = jwtTokenUtil.generateAdminAccessToken(admin);
        String refreshToken = jwtTokenUtil.generateRefreshToken(admin.getId(), admin.getEmail(), AccountType.ADMIN);

        return AdminLoginVo.builder()
                .id(admin.getId())
                .icon(admin.getIcon())
                .email(admin.getEmail())
                .password(adminLoginDto.getPassword())
                .icon(admin.getIcon())
                .createTime(admin.getCreateTime())
                .status(admin.getStatus())
                .token(accessToken)
                .refreshToken(refreshToken)
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

    /**
     * 更新管理员信息
     *
     * @param adminDto 管理员信息
     * @return 管理员信息
     */
    @Override
    public AdminVo update(AdminDto adminDto) {
        // 有编号就是其他管理员账号修改信息
        // 没有就是自己修改
        if (adminDto.getId() == null) adminDto.setId(SecurityContextUtil.getId());

        Admin admin = new Admin();
        BeanUtils.copyProperties(adminDto, admin);
        if (admin.getPassword() != null) admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));

        adminMapper.update(admin);

        return adminMapper.getById(admin.getId());
    }

    /**
     * 获取管理员菜单列表
     *
     * @return 菜单列表
     */
    @Override
    public List<Menu> menuList() {
        Integer adminId = SecurityContextUtil.getId();
        return menuMapper.getByAdminId(adminId);
    }
}
