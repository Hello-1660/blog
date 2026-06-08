package com.jxcia.blog.service.util;

import com.jxcia.blog.common.constant.EmailExceptionConstant;
import com.jxcia.blog.common.exception.EmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Component
public class SampleMailUtil {
    // 配置常量
    private static final String SMTP_HOST = "smtpdm.aliyun.com";
    private static final int SMTP_PORT = 80;
    @Value("${email.send-address}")
    private String USER_NAME;
    @Value("${email.password}")
    private String PASSWORD;
    @Value("${email.reply-address}")
    private String REPLY;
    @Value("${email.send-name}")
    private String SEND_USERNAME;

    protected String genMessageID(String mailFrom) {
        // 生成Message-ID:
        if (!mailFrom.contains("@")) {
            throw new IllegalArgumentException("Invalid email format: " + mailFrom);
        }
        String domain = mailFrom.split("@")[1];
        UUID uuid = UUID.randomUUID();
        return "<" + uuid.toString() + "@" + domain + ">";
    }

    private void setRecipients(MimeMessage message, Message.RecipientType type, String[] recipients)
            throws MessagingException {
        // 设置收件人地址
        if (recipients == null || recipients.length == 0) {
            return; // 空列表不设置
        }
        InternetAddress[] addresses = new InternetAddress[recipients.length];
        for (int h = 0; h < recipients.length; h++) {
            addresses[h] = new InternetAddress(recipients[h]);
        }
        message.setRecipients(type, addresses);
    }

    public boolean send(String receiveAddress, String verificationCode) {
        // 配置发送邮件的环境属性
        final Properties props = new Properties();

        // 表示SMTP发送邮件，需要进行身份验证
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        //设置端口：
        props.put("mail.smtp.port", SMTP_PORT);//或"25", 如果使用ssl，则去掉使用80或25端口的配置，进行如下配置：

        props.put("mail.smtp.from", USER_NAME);    //mailfrom 参数
        props.put("mail.user", USER_NAME);// 发件人的账号（在控制台创建的发信地址）
        props.put("mail.password", PASSWORD);// 发信地址的smtp密码（在控制台选择发信地址进行设置）
        System.setProperty("mail.mime.splitlongparameters", "false");//用于解决附件名过长导致的显示异常

        // 构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, PASSWORD);
            }
        };

        //使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);

        String messageIDValue = genMessageID(USER_NAME);
        MimeMessage message = new MimeMessage(mailSession) {
            @Override
            protected void updateMessageID() throws MessagingException {
                setHeader("Message-ID", messageIDValue);
            }
        };

        try {
            // 设置发件人邮件地址和名称。填写控制台配置的发信地址。和上面的mail.user保持一致。名称用户可以自定义填写。
            InternetAddress from = new InternetAddress(USER_NAME, SEND_USERNAME);//from 参数,可实现代发，注意：代发容易被收信方拒信或进入垃圾箱。
            message.setFrom(from);

            setRecipients(message, Message.RecipientType.TO, new String[]{receiveAddress});

            InternetAddress replyToAddress = new InternetAddress(REPLY);
            message.setReplyTo(new Address[]{replyToAddress});//可选。设置回信地址
            message.setSentDate(new Date());
            message.setSubject("信风邮箱验证码");

            //发送附件和内容：
            // 创建多重消息
            Multipart multipart = new MimeMultipart();

            // 创建一个BodyPart用于HTML内容
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent("验证码" + verificationCode, "text/html;charset=UTF-8");//设置邮件的内容，会覆盖前面的message.setContent
            multipart.addBodyPart(htmlPart);

            // 添加完整消息
            message.setContent(multipart);
            // 发送附件代码，结束
            Transport.send(message);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            return false;
        }

    }
}