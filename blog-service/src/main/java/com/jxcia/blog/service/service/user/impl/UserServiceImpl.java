package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.*;
import com.jxcia.blog.common.exception.*;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.entity.*;
import com.jxcia.blog.pojo.vo.*;
import com.jxcia.blog.service.mapper.user.*;
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
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleBrowseLogMapper articleBrowseLogMapper;
    @Autowired
    private UserLikeArticleMapper userLikeArticleMapper;
    @Autowired
    private SubscribeMapper subscribeMapper;

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


        return UserRegisterVo.
                builder()
                .email(userRegisterDto.getEmail())
                .password(userRegisterDto.getPassword())
                .build();
    }

    /**
     * 用户登录
     *
     * @param userLoginDto 登录数据
     * @return token
     */
    @Override
    public UserLoginVo login(UserLoginDto userLoginDto) {
        User user = userMapper.findByEmail(userLoginDto.getEmail());
        // 账号不存在
        if (user == null) throw new UserLoginException(UserLoginExceptionConstant.USER_NOT_FIND);

        // 密码错误
        boolean matches = passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword());
        if (!matches) throw new UserLoginException(UserLoginExceptionConstant.PASSWORD_ERROR);

        // 返回登录信息
        String token = jwtTokenUtil.generateUserToken(user);
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, userLoginVo);
        userLoginVo.setToken(token);

        return userLoginVo;
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

    /**
     * 获取用户文章列表
     *
     * @return 文章列表
     */
    @Override
    public List<Article> getArticleList() {
        Integer userId = SecurityContextUtil.getId();

        return articleMapper.getByUserId(userId);
    }

    /**
     * 用户浏览文章
     *
     * @param articleId 文章编号
     */
    @Override
    public void browse(Integer articleId) {
        Integer userId = SecurityContextUtil.getId();

        ArticleBrowse articleBrowse = ArticleBrowse.builder()
                .userId(userId)
                .articleId(articleId)
                .createTime(LocalDateTime.now())
                .build();

        articleBrowseLogMapper.insert(articleBrowse);
    }

    /**
     * 用户喜欢列表
     *
     * @return 文章列表
     */
    @Override
    public List<UserLikeArticleVo> likeList(Integer userId) {
        if (userId == null) userId = SecurityContextUtil.getId();
        // 查询用户点赞文章编号
        List<UserLikeArticle> userLikeArticleList = userLikeArticleMapper.getArticleIdsByUserId(userId);

        if (userLikeArticleList == null || userLikeArticleList.isEmpty()) return null;

        List<Integer> articleIdList = userLikeArticleList.stream().map(UserLikeArticle::getUserId).toList();
        List<Article> articleList = articleMapper.getByArticleIds(articleIdList);

        return articleList.stream().map(a -> {
            UserLikeArticle ua = findUserLikeArticleByArticleId(a.getId(), userLikeArticleList);

            return UserLikeArticleVo.builder()
                    .userId(a.getUserId())
                    .articleId(a.getId())
                    .likeTime(ua.getLikeTime())
                    .icon(a.getIcon())
                    .title(a.getTitle())
                    .build();
        }).toList();
    }

    /**
     * 用户点赞文章
     *
     * @param articleId 文章编号
     */
    @Override
    public void likeArticle(Integer articleId) {
        Integer userId = SecurityContextUtil.getId();

        if (userId == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);
        if (articleId == null) throw new UserException(UserExceptionConstant.ARTICLE_NOT_EXISTS);

        UserLikeArticle userLikeArticle = UserLikeArticle.builder()
                .userId(userId)
                .articleId(articleId)
                .likeTime(LocalDateTime.now())
                .build();

        UserLikeArticle ula = userLikeArticleMapper.getByUserLikeArticle(userLikeArticle);

        // 用户点过赞就删除，没有就新增
        if (ula == null) {
            userLikeArticleMapper.insert(userLikeArticle);
        } else {
            userLikeArticleMapper.delete(userLikeArticle);
        }

    }

    /**
     * 根据用户编号查询用户
     *
     * @param id 用户编号
     * @return 用户信息
     */
    @Override
    public UserVisitVo getUserById(Integer id) {
        if (id == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);
        // 获取用户信息
        User user = userMapper.getUserById(id);
        if (user == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);

        // 获取用户关注列表
        List<SubscribeVo> subscribeVoList = subscribeMapper.getSubscribeVoByUserId(user.getId());

        List<UserLikeArticleVo> userLikeArticleVoList = null;
        // 查看用户喜欢是否公开，公开则返回，私密则不返回
        if (user.getLikeShowStatus() == UserStatusConstant.USER_LIKE_PUBLIC) {
            userLikeArticleVoList = likeList(user.getId());
        }

        UserVisitVo userVisitVo = new UserVisitVo();
        BeanUtils.copyProperties(user, userVisitVo);
        userVisitVo.setSubscribeList(subscribeVoList);
        userVisitVo.setUserLikeArticleList(userLikeArticleVoList);

        return userVisitVo;
    }

    /**
     * 关注用户
     *
     * @param subUserId 关注用户编号
     */
    @Override
    public void subscribe(Integer subUserId) {
        if (subUserId == null) throw new SubscribeException(SubScribeExceptionConstant.SUBSCRIBE_USER_NOT_FOUND);

        User user = userMapper.getUserById(subUserId);
        if (user == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);

        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);

        if (userId.equals(subUserId)) throw new SubscribeException(SubScribeExceptionConstant.CANNOT_SUBSCRIBE_ONESELF);

        Subscribe subscribe = Subscribe.builder()
                .userId(userId)
                .subUserId(subUserId)
                .sort(SubscribeConstant.NOT_TOP)
                .createTime(LocalDateTime.now())
                .build();

        // 查询用户是已经关注
        Subscribe ss = subscribeMapper.getBySubscribe(subscribe);

        // 已经关注则取关，没有则关注
        if (ss == null) {
            subscribeMapper.insert(subscribe);
        } else {
            subscribeMapper.deleteBySubscribe(ss);
        }
    }

    /**
     * 获取关注列表
     *
     * @return 关注列表
     */
    @Override
    public List<SubscribeVo> subscribeList() {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        return subscribeMapper.getSubscribeVoByUserId(userId);
    }

    /**
     * 获取粉丝列表
     * @return 粉丝列表
     */
    @Override
    public List<SubscribeVo> fansList() {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        return subscribeMapper.getSubscribeVoBySubscribeId(userId);
    }

    /**
     * 更新用户信息
     *
     * @param user 更新用户信息
     * @return 用户信息
     */
    @Override
    public UserVo update(User user) {
        Integer userId = SecurityContextUtil.getId();
        if (userId == null) throw new UserNotExistsException(UserExceptionConstant.USER_NOT_EXISTS);

        user.setId(userId);
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.update(user);

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);

        return userVo;
    }


    /**
     * 根据文章编号查询文章点赞记录
     * @param articleId 文章编号
     * @param userLikeArticleList 文章点赞记录列表
     * @return 文章点赞记录
     */
    private UserLikeArticle findUserLikeArticleByArticleId(Integer articleId, List<UserLikeArticle> userLikeArticleList) {
        if (articleId == null) return null;

        for (UserLikeArticle ua : userLikeArticleList) {
            if (ua.getArticleId().equals(articleId)) return ua;
        }

        return null;
    }
}
