package com.jxcia.blog.service.service.user;

import com.jxcia.blog.common.result.PageResult;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.vo.UserLikeArticleVo;
import com.jxcia.blog.pojo.vo.UserLoginVo;
import com.jxcia.blog.pojo.vo.UserRegisterVo;
import com.jxcia.blog.pojo.vo.UserVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
    UserLoginVo login(@Valid UserLoginDto userLoginDto);

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

    /**
     * 用户浏览文章
     * @param articleId 文章编号
     */
    void browse(Integer articleId);

    /**
     * 用户喜欢列表
     * @return 文章列表
     */
    List<UserLikeArticleVo> likeList();

    /**
     * 用户点赞文章
     * @param articleId 文章
     */
    void likeArticle(Integer articleId);

    /**
     * 根据用户编号查询用户
     * @param id 用户编号
     * @return 用户信息
     */
    UserVo getUserById(Integer id);

    /**
     * 关注用户
     * @param subUserId 关注用户编号
     */
    void subscribe(Integer subUserId);
}
