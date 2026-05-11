package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.UserLikeArticleVo;
import com.jxcia.blog.pojo.vo.UserLoginVo;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import com.jxcia.blog.pojo.vo.UserVo;
import com.jxcia.blog.service.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 用户登录
     * @param userLoginDto 用户登录数据
     * @return token
     */
    @PostMapping("/login")
    public Result<UserLoginVo> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        log.info("user login: {}", userLoginDto.getEmail());

        return Result.success(userService.login(userLoginDto));
    }

    /**
     * 查看用户详细
     * @return 用户详细
     */
    @GetMapping("/detail")
    public Result<UserVo> detail() {
        log.info("user detail");

        return Result.success(userService.getUser());
    }

    /**
     * 获取用户文章列表
     * @return 文章列表
     */
    @GetMapping("/articleList")
    public Result<List<Article>> articleList() {
        log.info("article list");

        return Result.success(userService.getArticleList());
    }

    /**
     * 用户浏览文章
     * @param articleId 文章编号
     * @return 无
     */
    @PostMapping("/browse")
    public Result<Void> browse(@NotNull Integer articleId) {
        log.info("browse article: {}", articleId);

        userService.browse(articleId);

        return Result.success();
    }

    /**
     * 用户喜欢列表
     * @return 文章列表
     */
    @GetMapping("/likeList")
    public Result<List<UserLikeArticleVo>> likeList() {
        log.info("like list");

        return Result.success(userService.likeList());
    }

    /**
     * 用户点赞文章
     * @param articleId 文章编号
     * @return 无
     */
    @PostMapping("/likeArticle")
    public Result<Void> likeArticle(@NotNull Integer articleId) {
        log.info("like article: {}", articleId);

        userService.likeArticle(articleId);

        return Result.success();
    }

    /**
     * 根据用户编号查询用户
     * @param id 用户编号
     * @return 用户信息
     */
    @GetMapping("/visit/{id}")
    public Result<UserVo> visit(@PathVariable @NotNull Integer id) {
        log.info("visit article: {}", id);

        return Result.success(userService.getUserById(id));
    }
}
