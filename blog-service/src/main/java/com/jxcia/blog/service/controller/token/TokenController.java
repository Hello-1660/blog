package com.jxcia.blog.service.controller.token;

import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.common.constant.TokenConstant;
import com.jxcia.blog.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/token")
public class TokenController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${jwt.tokenHead}")
    private String head;

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String accessToken = authHeader.replace(head, "");

        if (!jwtTokenUtil.validateToken(accessToken)) return Result.success();


        String jti = jwtTokenUtil.getClaimsJtiFromToken(accessToken);
        Date expiration = jwtTokenUtil.getExpirationDateFromToken(accessToken);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if (ttl > 0) {
            try {
                redisTemplate.opsForValue().set(TokenConstant.BLACKLIST_PREFIX + jti, "1", ttl, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Redis 不可用，token 黑名单写入失败: jti={}", jti);
            }
        }

        return Result.success();
    }
}
