package com.jxcia.blog.service.util;

import com.jxcia.blog.common.constant.UserRegisterExceptionConstant;
import com.jxcia.blog.common.exception.UserRegisterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class VerificationCodeUtil {
    @Value("${verification-code.length}")
    private int CodeLength;
    @Value("${verification-code.ttl}")
    private int ttl;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 生成数字验证码
     * @param length 验证码长度
     * @return 验证码
     */
    private String generateCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((char) ((int) (Math.random() * 10) + 48));
        }
        return code.toString();
    }

    /**
     * 存入 redis 当中
     * @param email 用户邮箱，用于标识用户
     * @param code 验证码
     */
    private void setVerificationCode(String email, String code) {
        redisTemplate.opsForValue()
                .set(email, code, Duration.ofMinutes(ttl));
    }

    /**
     * 获取验证码
     * @param email 用户邮箱，用于标识用户
     * @return 验证码
     */
    public String getCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    /**
     * 设置验证码
     * @param email 用户邮箱
     */
    public void setCode(String email) {
        setVerificationCode(email, generateCode(CodeLength));
    }

    /**
     * 验证验证码是否正确
     * @param email 用户邮箱
     * @param code 验证码
     */
    public void verify(String email, String code) {
        String verificationCode = getCode(email);
        if (verificationCode == null) throw new UserRegisterException(UserRegisterExceptionConstant.VERIFICATION_CODE_EXPIRED);
        if (!verificationCode.equals(code)) throw new UserRegisterException(UserRegisterExceptionConstant.VERIFICATION_CODE_ERROR);
    }

    /**
     * 获取验证码过期时间
     * @param email 用户邮箱
     * @return 剩余时间，如果没有获取过，返回 -2
     */
    public long getTtl(String email) {
        Long expire = redisTemplate.getExpire(email, TimeUnit.SECONDS);
        return expire == null ? -2 : expire;
    }
}