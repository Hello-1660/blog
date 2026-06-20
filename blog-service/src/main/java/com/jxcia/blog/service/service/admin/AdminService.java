package com.jxcia.blog.service.service.admin;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.*;
import com.jxcia.blog.pojo.entity.Menu;
import com.jxcia.blog.pojo.vo.AdminLoginVo;
import com.jxcia.blog.pojo.vo.AdminPageVo;
import com.jxcia.blog.pojo.vo.AdminRegisterVo;
import com.jxcia.blog.pojo.vo.AdminVo;

import java.util.List;

public interface AdminService {

    AdminLoginVo login(AdminLoginDto adminLoginDto);

    AdminRegisterVo save(AdminRegisterDto adminRegisterDto);

    AdminVo detail(Integer id);

    AdminVo update(AdminDto adminDto);

    List<Menu> menuList();

    PageResult<AdminPageVo> list(AdminPageDto dto);

    void delete(Integer id);

    void toggleStatus(Integer id);

    void assignRole(AdminAssignRoleDto dto);

    List<Integer> getRoleIds(Integer adminId);
}
