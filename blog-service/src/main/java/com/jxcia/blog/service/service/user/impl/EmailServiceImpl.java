package com.jxcia.blog.service.service.user.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.EmailConstant;
import com.jxcia.blog.common.constant.EmailExceptionConstant;
import com.jxcia.blog.common.exception.EmailException;
import com.jxcia.blog.mapper.user.ArticleMapper;
import com.jxcia.blog.mapper.user.EmailMapper;
import com.jxcia.blog.mapper.user.SubscribeMapper;
import com.jxcia.blog.mapper.user.UserMapper;
import com.jxcia.blog.pojo.entity.Article;
import com.jxcia.blog.pojo.entity.Email;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.service.service.user.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 邮箱 serviceImpl
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private SubscribeMapper subscribeMapper;
    @Autowired
    private EmailMapper emailMapper;

    /**
     * 推送粉丝
     *
     * @param articleId 推送文章编号
     */
    @Override
    public void massSendFans(Integer articleId) {
        Integer userId = SecurityContextUtil.getId();
        User user = userMapper.getUserById(userId);
        Article article = articleMapper.getByArticleId(articleId);

        // 邮件内容
        String json = emailContent2Json(user, article);
        // 粉丝编号列表
        List<Integer> fansIdList = subscribeMapper.getUserIdListBySubscribeId(userId);
        if (fansIdList.isEmpty()) {
            return;
        }
        List<Email> emailList = new ArrayList<>();
        for (Integer fansId : fansIdList) {
            emailList.add(Email.builder()
                    .title("文章推送")
                    .content(json)
                    .receiverId(fansId)
                    .senderId(userId)
                    .createTime(LocalDateTime.now())
                    .status(EmailConstant.UNREAD)
                    .build()
            );
        }
        // 推送
        emailMapper.insertByEmailList(emailList);
    }

    /**
     * 阅读邮件
     *
     * @param id 邮件编号
     * @return 邮件
     */
    @Override
    public Email read(Integer id) {
        Email email = emailMapper.getById(id);
        if (email == null) throw new EmailException(EmailExceptionConstant.NOT_FOUND);

        // 设为已读
        Email setEmail = Email.builder().id(id).status(EmailConstant.READ).build();
        emailMapper.update(setEmail);

        return email;
    }

    /**
     * 全部已读
     */
    @Override
    public void allRead() {
        Integer userId = SecurityContextUtil.getId();
        emailMapper.UpdateStatusByUserId(userId, EmailConstant.READ);
    }

    /**
     * 批量删除邮件
     *
     * @param ids 邮件列表
     */
    @Transactional
    @Override
    public void deleteByEmailList(List<Integer> ids) {
        userMapper.deleteByIdList(ids);
    }

    /**
     * 获取推送文章邮件内容
     * @param user 作者
     * @param article 文章
     * @return 邮件内容
     */
    private String emailContent2Json(User user, Article article) {
        return  "[" +
                "{\"type\": \"text\", \"value\": \"您关注的\"}," +
                "{\"type\": \"link\", \"text\": \"" +
                user.getNickname() +
                "\", \"route\": \"/user/" +
                user.getId() +
                "\"}," +
                "{\"type\": \"text\", \"value\": \"发布了新文章\"}," +
                "{\"type\": \"link\", \"text\": \"" +
                article.getTitle() +
                "\", \"route\": \"/article/" +
                article.getId() +
                "\"}," +
                "{\"type\": \"text\", \"value\": \"快来看看吧！\"}" +
                "]";
    }
}
