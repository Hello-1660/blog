package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import com.jxcia.blog.pojo.vo.UserVo;
import jakarta.validation.Valid;

/**
 * 用户 service
 */
public interface UserService {

    /**
     * 用户注册
     * @param userRegisterDto 用户注册信息
     * @return 用户登录信息回调
     */
    UserRegisterVo register(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     * @param userLoginDto 登录数据
     * @return token
     */
    String login(@Valid UserLoginDto userLoginDto);

    /**
     * 获取用户详情信息
     * @return 用户信息
     */
    UserVo getUser();
}
