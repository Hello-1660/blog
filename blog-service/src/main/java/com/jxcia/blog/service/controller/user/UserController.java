package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.dto.UserUpdateDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Email;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.pojo.vo.*;
import com.jxcia.blog.service.service.user.UserService;
import com.jxcia.blog.service.util.SampleMailUtil;
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

        return Result.success(userService.likeList(null));
    }

    /**
     * 用户点赞文章
     * @param articleId 文章编号
     * @return 无
     */
    @PostMapping("/likeArticle")
    public Result<Void> likeArticle(Integer articleId) {
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
    public Result<UserVisitVo> visit(@PathVariable Integer id) {
        log.info("visit article: {}", id);

        return Result.success(userService.getUserById(id));
    }

    /**
     * 关注用户
     * @param subUserId 关注用户编号
     * @return 无
     */
    @PostMapping("/subscribe")
    public Result<Void> subscribe(Integer subUserId) {
        log.info("subscribe user: {}", subUserId);

        userService.subscribe(subUserId);

        return Result.success();
    }

    /**
     * 查看关注列表
     * @return 关注列表
     */
    @GetMapping("/subscribeList")
    public Result<List<SubscribeVo>> subscribeList() {
        log.info("subscribe list");

        return Result.success(userService.subscribeList());
    }

    /**
     * 查看粉丝列表
     * @return 粉丝列表
     */
    @GetMapping("/fansList")
    public Result<List<SubscribeVo>> fansList() {
        log.info("fansList");

        return Result.success(userService.fansList());
    }

    /**
     * 更新用户，更新完 token 失效，需要重新登录
     * @param userUpdateDto 更新用户信息
     * @return 用户信息
     */
    @PostMapping("/update")
    public Result<UserVo> update(@RequestBody UserUpdateDto userUpdateDto) {
        log.info("update user: {}", userUpdateDto);

        return Result.success(userService.update(userUpdateDto));
    }

    /**
     * 查看邮箱列表
     * @return 邮箱列表
     */
    @GetMapping("/emailList")
    public Result<List<Email>> emailList() {
        log.info("email list");

        return Result.success(userService.emailList());
    }
}
