package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.UserExceptionConstant;
import com.jxcia.blog.common.constant.UserLoginExceptionConstant;
import com.jxcia.blog.common.constant.UserRegisterExceptionConstant;
import com.jxcia.blog.common.exception.UserLoginException;
import com.jxcia.blog.common.exception.UserNotExistsException;
import com.jxcia.blog.common.exception.UserRegisterException;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import com.jxcia.blog.pojo.vo.UserVo;
import com.jxcia.blog.service.mapper.user.UserMapper;
import com.jxcia.blog.service.service.user.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册信息
     * @return 用户登录信息回调
     */
    @Override
    public UserRegisterVo register(UserRegisterDto userRegisterDto) {
        // 两次密码不一致
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword()))
            throw new UserRegisterException(UserRegisterExceptionConstant.CONFIRM_PASSWORD_NOT_EQUALS);

        User user = new User();
        user.setEmail(userRegisterDto.getEmail());

        // 邮箱已注册
        List<User> userList = userMapper.findByUser(user);
        if (!userList.isEmpty())
            throw new UserRegisterException(UserRegisterExceptionConstant.EMAIL_EXISTENT);

        user.setNickname(userRegisterDto.getNickname());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setCreateTime(LocalDateTime.now());

        // 插入数据
        userMapper.insert(user);


        UserRegisterVo userRegisterVo = new UserRegisterVo();
        BeanUtils.copyProperties(user, userRegisterVo);

        return userRegisterVo;
    }

    /**
     * 用户登录
     *
     * @param userLoginDto 登录数据
     * @return token
     */
    @Override
    public String login(UserLoginDto userLoginDto) {
        User user = userMapper.findByEmail(userLoginDto.getEmail());
        // 账号不存在
        if (user == null) throw new UserLoginException(UserLoginExceptionConstant.USER_NOT_FIND);

        // 密码错误
        boolean matches = passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword());
        if (!matches) throw new UserLoginException(UserLoginExceptionConstant.PASSWORD_ERROR);

        // 返回 token
        return jwtTokenUtil.generateUserToken(user);
    }

    /**
     * 获取用户详情信息
     *
     * @return 用户信息
     */
    @Override
    public UserVo getUser() {
        Integer id = SecurityContextUtil.getId();
        User user = userMapper.getUserById(id);
        if (user == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);

        return userVo;
    }
}
