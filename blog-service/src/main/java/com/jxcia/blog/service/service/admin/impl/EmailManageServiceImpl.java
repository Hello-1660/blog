package com.jxcia.blog.service.service.admin.impl;

import com.jxcia.blog.blog.security.util.SecurityContextUtil;
import com.jxcia.blog.common.constant.EmailConstant;
import com.jxcia.blog.mapper.user.EmailMapper;
import com.jxcia.blog.mapper.user.UserMapper;
import com.jxcia.blog.pojo.dto.EmailManageDto;
import com.jxcia.blog.pojo.entity.Email;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.service.service.admin.EmailManageService;
import com.jxcia.blog.service.util.SampleMailUtil;
import com.jxcia.blog.service.websocket.EmailWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EmailManageServiceImpl implements EmailManageService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailMapper emailMapper;
    @Autowired
    private SampleMailUtil sampleMailUtil;
    @Autowired
    private EmailWebSocketHandler emailWebSocketHandler;

    @Override
    public void send(EmailManageDto dto) {
        Integer senderId = SecurityContextUtil.getId();
        List<Integer> receiverIds = dto.getReceiverIds();

        // 全站发送：获取所有活跃用户
        if (receiverIds == null || receiverIds.isEmpty()) {
            List<User> allUsers = userMapper.getAllActiveEmails();
            receiverIds = new ArrayList<>();
            for (User u : allUsers) {
                receiverIds.add(u.getId());
            }
        }

        if (receiverIds.isEmpty()) return;

        // 站内邮件
        if (Boolean.TRUE.equals(dto.getInternal())) {
            List<Email> emailList = new ArrayList<>();
            for (Integer receiverId : receiverIds) {
                Email email = Email.builder()
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .receiverId(receiverId)
                        .senderId(senderId)
                        .createTime(LocalDateTime.now())
                        .status(EmailConstant.UNREAD)
                        .build();
                emailList.add(email);
            }
            emailMapper.insertByEmailList(emailList);

            // WebSocket通知
            for (Integer receiverId : receiverIds) {
                emailWebSocketHandler.notifyNewEmail(receiverId);
            }
        }

        // 邮箱邮件（SMTP）
        if (Boolean.TRUE.equals(dto.getExternal())) {
            List<User> users = userMapper.getAllActiveEmails();
            for (User user : users) {
                if (receiverIds.contains(user.getId()) && user.getEmail() != null && !user.getEmail().isEmpty()) {
                    sampleMailUtil.sendHtmlMail(user.getEmail(), dto.getTitle(), dto.getContent());
                }
            }
        }
    }
}
