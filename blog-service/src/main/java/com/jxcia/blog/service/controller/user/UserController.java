package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import com.jxcia.blog.service.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterDto 用户注册数据
     * @return 用户注册返回数据（登录信息）
     */
    @PostMapping("/save")
    public Result<UserRegisterVo> save(@RequestBody @Valid UserRegisterDto userRegisterDto) {
        log.info("user register: {}", userRegisterDto.getEmail());

        return Result.success(userService.register(userRegisterDto));
    }
}
