package com.jxcia.blog.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class VerificationCodeUtil {
    @Value("${verification-code.length}")
    private int CodeLength;
    @Value("${verification-code.ttl}")
    private int ttl;
    @Value("${verification-code.min-interval-time}")
    private int minIntervalTime;
    @Value("${verification-code.minute-ip-limit}")
    private int minuteIpLimit;
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
        try {
            redisTemplate.opsForValue()
                    .set(email, code, Duration.ofMinutes(ttl));
        } catch (Exception e) {
            log.warn("Redis 不可用，验证码写入失败: email={}", email);
            throw new RuntimeException("验证码服务暂时不可用", e);
        }
    }

    /**
     * 获取验证码
     * @param email 用户邮箱，用于标识用户
     * @return 验证码
     */
    public String getCode(String email) {
        try {
            return redisTemplate.opsForValue().get(email);
        } catch (Exception e) {
            log.warn("Redis 不可用，验证码读取失败: email={}", email);
            return null;
        }
    }

    /**
     * 设置验证码
     * @param email 邮箱（用户邮箱 + 标识头）
     * @return 是否生成成功
     */
    public boolean setCode(String email) {
        try {
            long remainTime = getTtl(email);
            if (remainTime > (ttl * 60L - minIntervalTime)) return false;

            setVerificationCode(email, generateCode(CodeLength));
            return true;
        } catch (Exception e) {
            log.warn("Redis 不可用，验证码发送失败: email={}", email);
            return false;
        }
    }

    /**
     * 验证验证码是否正确
     * @param email 用户邮箱
     * @param code 验证码
     */
    public boolean verify(String email, String code) {
        return code.equals(getCode(email));
    }

    /**
     * 获取验证码过期时间
     * @param email 用户邮箱
     * @return 剩余时间，如果没有获取过，返回 -2
     */
    private long getTtl(String email) {
        try {
            Long expire = redisTemplate.getExpire(email, TimeUnit.SECONDS);
            return expire == null ? -2 : expire;
        } catch (Exception e) {
            log.warn("Redis 不可用，TTL 查询失败: email={}", email);
            return -2;
        }
    }

    /**
     * ip 限流
     * @param ip ip + 标识头
     * @return 是否拦截
     */
    public boolean limitIp (String ip) {
        try {
            Long count = redisTemplate.opsForValue().increment(ip);
            if (count == null) return false;
            if (count == 1) redisTemplate.expire(ip, Duration.ofMinutes(1));
            return count > minuteIpLimit;
        } catch (Exception e) {
            log.warn("Redis 不可用，IP 限流降级放行: ip={}", ip);
            return false;
        }
    }
}