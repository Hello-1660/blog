package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.enums.AccountType;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.*;
import com.jxcia.blog.common.exception.*;
import com.jxcia.blog.pojo.dto.UserLoginDto;
import com.jxcia.blog.pojo.dto.UserRegisterDto;
import com.jxcia.blog.pojo.dto.UserResetPasswordDto;
import com.jxcia.blog.pojo.dto.UserUpdateDto;
import com.jxcia.blog.pojo.entity.*;
import com.jxcia.blog.pojo.vo.*;
import com.jxcia.blog.mapper.user.*;
import com.jxcia.blog.service.service.user.UserService;
import com.jxcia.blog.service.util.SampleMailUtil;
import com.jxcia.blog.service.util.VerificationCodeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
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
    @Autowired
    private EmailMapper emailMapper;
    @Autowired
    private IdentifyMapper identifyMapper;
    @Autowired
    private VerificationCodeUtil verificationCodeUtil;
    @Autowired
    private SampleMailUtil sampleMailUtil;

    /**
     * 用户注册
     *
     * @param userRegisterDto 用户注册信息
     * @return 用户登录信息回调
     */
    @Override
    public UserRegisterVo register(UserRegisterDto userRegisterDto) {
        // 处理验证码
        boolean verify = verificationCodeUtil.verify(
                VerificationCodeConstant.VERIFICATION_CODE_REGISTER_PRO + userRegisterDto.getEmail(),
                userRegisterDto.getVerificationCode());
        if (!verify) throw new UserRegisterException(VerificationCodeConstant.VERIFICATION_CODE_ERROR);

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
        String accessToken = jwtTokenUtil.generateUserAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getId(), user.getEmail(), AccountType.USER);
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, userLoginVo);

        userLoginVo.setToken(accessToken);
        userLoginVo.setRefreshToken(refreshToken);

        return userLoginVo;
    }

    /**
     * 获取用户详情信息
     *
     * @return 用户信息
     */
    @Override
    public UserVo getUser(Integer id) {
        // 判断是否为查看自己
        if (id == null) id = SecurityContextUtil.getId();
        // 如果是查看自己，必须登录
        if (id == null) throw new UserNotLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        User user = userMapper.getUserById(id);

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
    public List<Article> getArticleList(Integer id) {
        // 查看自己
        if (id == null) {
            id = SecurityContextUtil.getId();
            // 查看自己必须登录
            if (id == null) throw new UserNotLoginException(UserExceptionConstant.USER_NOT_LOGIN);
            return articleMapper.getByUserId(id);
        } else {
            // 访问其他用户只返回已发布作品
            return articleMapper.getByUserId(id).stream()
                    .filter(a -> a.getStatus().equals(ArticleStatusConstant.PUBLIC))
                    .toList();
        }
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
        // 是否查看自己
        if (userId == null) {
            userId = SecurityContextUtil.getId();
            // 查看自己必修登录
            if (userId == null) throw new UserNotLoginException(UserExceptionConstant.USER_NOT_LOGIN);
        } else {
            // 查看其他用户
            User user = userMapper.getUserById(userId);
            // 不展示喜欢列表返回空列表
            if (user.getLikeShowStatus() == UserStatusConstant.USER_LIKE_PRIVATE) return Collections.emptyList();
            // 否则返回喜欢列表
        }
        return getUserLikeArticleVoList(userId);
    }

    /**
     * 获取用户喜欢列表
     * @param userId 用户编号
     * @return 用户喜欢列表
     */
    private List<UserLikeArticleVo> getUserLikeArticleVoList(Integer userId) {
        // 查询用户点赞文章编号
        List<UserLikeArticle> userLikeArticleList = userLikeArticleMapper.getArticleIdsByUserId(userId);

        if (userLikeArticleList == null || userLikeArticleList.isEmpty()) return Collections.emptyList();

        List<Integer> articleIdList = userLikeArticleList.stream().map(UserLikeArticle::getArticleId).toList();
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

        // 获取关注数
        Integer subscribeNumber = subscribeMapper.getSubscribeNumberByUserId(user.getId());
        // 获取粉丝数
        Integer fansNumber = subscribeMapper.getUserNumberBySubUserId(user.getId());

        List<UserLikeArticleVo> userLikeArticleVoList = null;
        // 查看用户喜欢是否公开，公开则返回，私密则不返回
        if (user.getLikeShowStatus() == UserStatusConstant.USER_LIKE_PUBLIC) {
            userLikeArticleVoList = likeList(user.getId());
        }

        UserVisitVo userVisitVo = new UserVisitVo();
        BeanUtils.copyProperties(user, userVisitVo);
        userVisitVo.setSubscribeNumber(subscribeNumber);
        userVisitVo.setFansNumber(fansNumber);
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

        if (subUserId.equals(userId)) throw new SubscribeException(SubScribeExceptionConstant.CANNOT_SUBSCRIBE_ONESELF);

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
    public List<SubscribeVo> subscribeList(Integer id) {
        // 查看自己
        if (id == null) id = SecurityContextUtil.getId();
        if (id == null) throw new UserNotLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        return subscribeMapper.getSubscribeVoByUserId(id);
    }

    /**
     * 获取粉丝列表
     * @return 粉丝列表
     */
    @Override
    public List<SubscribeVo> fansList() {
        Integer userId = SecurityContextUtil.getId();

        return subscribeMapper.getSubscribeVoBySubscribeId(userId);
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateDto 更新用户信息
     * @return 用户信息
     */
    @Override
    public UserVo update(UserUpdateDto userUpdateDto) {
        Integer userId = SecurityContextUtil.getId();

        // 原对象
        User user = userMapper.getUserById(userId);

        // 修改属性
        user.setNickname(userUpdateDto.getNickname());
        user.setIcon(userUpdateDto.getIcon());
        user.setEmail(userUpdateDto.getEmail());
        user.setDescription(userUpdateDto.getDescription());
        user.setThemeId(userUpdateDto.getThemeId());
        user.setLikeShowStatus(userUpdateDto.getLikeShowStatus());

        userMapper.update(user);

        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);

        return userVo;
    }

    /**
     * 获取邮箱列表
     *
     * @return 邮箱列表
     */
    @Override
    public List<Email> emailList() {
        Integer userId = SecurityContextUtil.getId();

        return emailMapper.getListByUserId(userId);
    }

    /**
     * 发送验证码
     *
     * @param email 验证码
     */
    @Override
    public void sendVerificationCode(String email) {
        String codeHead = VerificationCodeConstant.VERIFICATION_CODE_REGISTER_PRO + email;
        // 账号检查，同一账号一分钟只能发送一次
        if (!verificationCodeUtil.setCode(codeHead))
            throw new UserRegisterException(VerificationCodeConstant.VERIFICATION_CODE_SEND_EXCESSIVE);

        String code = verificationCodeUtil.getCode(codeHead);
        boolean result = sampleMailUtil.send(email, code);
        if (!result) throw new UserRegisterException(VerificationCodeConstant.VERIFICATION_CODE_SEND_ERROR);
    }

    /**
     * 获取用户身份
     *
     * @return 用户身份
     */
    @Override
    public UserIdentifyVo identify(Integer id) {
        // 查看自己
        if (id == null) id = SecurityContextUtil.getId();
        if (id == null) throw new UserNotLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        return identifyMapper.getIdentifyVoByUserId(id);
    }

    /**
     * 用户重置密码
     *
     * @param userResetPasswordDto 用户重置密码信息
     */
    @Override
    public void resetPassword(UserResetPasswordDto userResetPasswordDto) {
        boolean verify = verificationCodeUtil.verify(
                userResetPasswordDto.getVerificationCode(), VerificationCodeConstant.VERIFICATION_CODE_LIMIT_IP_PRO + userResetPasswordDto.getEmail()
        );

        // 校验数据
        if (!verify) throw new UserException(VerificationCodeConstant.VERIFICATION_CODE_ERROR);

        // 修改用户密码
        Integer userId = SecurityContextUtil.getId();
        User build = User.builder()
                .id(userId)
                .password(userResetPasswordDto.getPassword())
                .build();

        userMapper.update(build);
    }

    /**
     * 获取用户互动信息
     *
     * @param id 用户编号
     * @return 用户互动信息
     */
    @Override
    public UserMsgVo userMsg(Integer id) {
        UserMsgVo userMsgVo = new UserMsgVo();

        // 访问自己
        if (id == null) id = SecurityContextUtil.getId();
        // 访问自己必须登录
        if (id == null) throw new UserNotLoginException(UserExceptionConstant.USER_NOT_LOGIN);

        // 填充粉丝数和关注数
        userMsgVo.setFansNum(subscribeMapper.getUserNumberBySubUserId(id));
        userMsgVo.setSubscribeNum(subscribeMapper.getSubscribeNumberByUserId(id));
        userMsgVo.setUserIdentifyVo(identify(id));

        return userMsgVo;
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
