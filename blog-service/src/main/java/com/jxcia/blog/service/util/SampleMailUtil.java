package com.jxcia.blog.service.util;

import com.jxcia.blog.common.exception.ServiceException;
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
            throw new ServiceException("Invalid email format: " + mailFrom);
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
        return sendHtmlMail(receiveAddress, "信风邮箱验证码", "验证码" + verificationCode);
    }

    public boolean sendHtmlMail(String receiveAddress, String subject, String htmlContent) {
        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.from", USER_NAME);
        props.put("mail.user", USER_NAME);
        props.put("mail.password", PASSWORD);
        System.setProperty("mail.mime.splitlongparameters", "false");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER_NAME, PASSWORD);
            }
        };

        Session mailSession = Session.getInstance(props, authenticator);
        String messageIDValue = genMessageID(USER_NAME);
        MimeMessage message = new MimeMessage(mailSession) {
            @Override
            protected void updateMessageID() throws MessagingException {
                setHeader("Message-ID", messageIDValue);
            }
        };

        try {
            InternetAddress from = new InternetAddress(USER_NAME, SEND_USERNAME);
            message.setFrom(from);
            setRecipients(message, Message.RecipientType.TO, new String[]{receiveAddress});
            InternetAddress replyToAddress = new InternetAddress(REPLY);
            message.setReplyTo(new Address[]{replyToAddress});
            message.setSentDate(new Date());
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html;charset=UTF-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            Transport.send(message);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("send mail failed to {}: {}", receiveAddress, e.getMessage());
            return false;
        }
    }
}