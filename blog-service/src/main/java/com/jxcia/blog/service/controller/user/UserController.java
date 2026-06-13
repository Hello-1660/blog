package com.jxcia.blog.service.controller.user;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.blog.security.annotation.AuthOptional;
import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.common.constant.VerificationCodeConstant;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.dto.UserResetPasswordDto;
import com.jxcia.blog.pojo.dto.UserUpdateDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Email;
import com.jxcia.blog.pojo.vo.*;
import com.jxcia.blog.service.service.user.UserService;
import com.jxcia.blog.service.util.IpUtil;
import com.jxcia.blog.service.util.VerificationCodeUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private VerificationCodeUtil verificationCodeUtil;

    /**
     * 用户注册
     * @param userRegisterDto 用户注册数据
     * @return 用户注册返回数据（登录信息）
     */
    @Anonymous
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
    @Anonymous
    @PostMapping("/login")
    public Result<UserLoginVo> login(@RequestBody @Valid UserLoginDto userLoginDto) {
        log.info("user login: {}", userLoginDto.getEmail());

        return Result.success(userService.login(userLoginDto));
    }

    /**
     * 查看用户详细
     * @return 用户详细
     */
    @AuthOptional
    @GetMapping({"/detail/{id}", "/detail"})
    public Result<UserVo> detail(@PathVariable(required = false) Integer id) {
        log.info("user detail: {}", id);

        return Result.success(userService.getUser(id));
    }

    /**
     * 获取用户文章列表
     * @return 文章列表
     */
    @AuthOptional
    @GetMapping({"/articleList/{id}", "/articleList"})
    public Result<List<Article>> articleList(@PathVariable(required = false) Integer id) {
        log.info("user article list: {}", id);

        return Result.success(userService.getArticleList(id));
    }

    /**
     * 用户浏览文章
     * @param articleId 文章编号
     * @return 无
     */
    @Anonymous
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
    @AuthOptional
    @GetMapping({"/likeList/{id}", "/likeList"})
    public Result<List<UserLikeArticleVo>> likeList(@PathVariable Integer id) {
        log.info("user like list:{}", id);

        return Result.success(userService.likeList(id));
    }

    /**
     * 用户点赞文章
     * @param articleId 文章编号
     * @return 无
     */
    @AuthRequired
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
    @Anonymous
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
    @AuthRequired
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
    @AuthOptional
    @GetMapping({"/subscribeList/{id}", "/subscribeList"})
    public Result<List<SubscribeVo>> subscribeList(@PathVariable(required = false) Integer id) {
        log.info("user subscribe list: {}", id);

        return Result.success(userService.subscribeList(id));
    }

    /**
     * 查看粉丝列表
     * @return 粉丝列表
     */
    @AuthRequired
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
    @AuthRequired
    @PostMapping("/update")
    public Result<UserVo> update(@RequestBody @Valid UserUpdateDto userUpdateDto) {
        log.info("update user: {}", userUpdateDto);

        return Result.success(userService.update(userUpdateDto));
    }

    /**
     * 查看邮箱列表
     * @return 邮箱列表
     */
    @AuthRequired
    @GetMapping("/emailList")
    public Result<List<Email>> emailList() {
        log.info("email list");

        return Result.success(userService.emailList());
    }

    /**
     * 获取验证码
     * @param email 用户邮箱
     */
    @Anonymous
    @GetMapping("/verificationCode/{email}")
    public Result<String> verificationCode(@PathVariable String email, HttpServletRequest request) {
        log.info("verification code{}", email);

        // ip 拦截
        String clientIp = IpUtil.getClientIp(request);
        if (verificationCodeUtil.limitIp(clientIp)) return Result.Failed(VerificationCodeConstant.VERIFICATION_CODE_SEND_EXCESSIVE);

        userService.sendVerificationCode(email);
        return Result.success();
    }

    /**
     * 获取用户身份
     * @return 用户身份
     */
    @AuthOptional
    @GetMapping({"/identify/{id}", "/identify"})
    public Result<UserIdentifyVo> identify(@PathVariable(required = false) Integer id) {
        log.info("identify id: {}", id);

        return Result.success(userService.identify(id));
    }

    /**
     * 用户重置密码
     * @param userResetPasswordDto 用户重置密码信息
     * @return 无
     */
    @AuthRequired
    @PostMapping("/resetPassword")
    public Result<Void> resetPassword(@RequestBody @Valid UserResetPasswordDto userResetPasswordDto) {
        log.info("reset password: {}", userResetPasswordDto);

        userService.resetPassword(userResetPasswordDto);
        return Result.success();
    }
}
