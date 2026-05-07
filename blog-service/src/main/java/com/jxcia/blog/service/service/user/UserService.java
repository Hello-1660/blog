package com.jxcia.blog.service.service.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import com.jxcia.blog.pojo.vo.UserVo;
import jakarta.validation.Valid;

import java.util.List;

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

    /**
     * 获取用户文章列表
     * @return 文章列表
     */
    List<Article> getArticleList();
}
