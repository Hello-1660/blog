package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.pojo.dto.AdminDto;
import com.jxcia.blog.pojo.dto.AdminRegisterDto;
import com.jxcia.blog.pojo.dto.AdminLoginDto;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.pojo.vo.AdminLoginVo;
import com.jxcia.blog.pojo.vo.AdminRegisterVo;
import com.jxcia.blog.pojo.vo.AdminVo;

import java.util.List;

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

    /**
     * 获取管理员详情
     * @param id 管理员编号
     * @return 管理员详情
     */
    AdminVo detail(Integer id);

    /**
     * 更新管理员信息
     * @param adminDto 管理员信息
     * @return 管理员信息
     */
    AdminVo update(AdminDto adminDto);

    /**
     * 获取管理员菜单列表
     * @return 菜单列表
     */
    List<Menu> menuList();
}
