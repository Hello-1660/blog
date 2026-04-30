package com.jxcia.blog.service.service.user;

import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import jakarta.validation.Valid;

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
}
