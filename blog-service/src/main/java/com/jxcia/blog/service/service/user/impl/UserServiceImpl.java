package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
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
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) throw new RuntimeException("password not equals");

        User user = new User();
        user.setEmail(userRegisterDto.getEmail());

        // 邮箱已注册
        List<User> userList = userMapper.findByUser(user);
        if (!userList.isEmpty()) throw new RuntimeException("email exist");

        user.setNickname(userRegisterDto.getNickname());
        user.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        user.setCreateTime(LocalDateTime.now());

        // 插入数据
        userMapper.insert(user);


        UserRegisterVo userRegisterVo = new UserRegisterVo();
        BeanUtils.copyProperties(user, userRegisterVo);

        return userRegisterVo;
    }
}
