package com.jxcia.blog.service.controller.token;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import com.jxcia.blog.common.constant.TokenConstant;
import com.jxcia.blog.common.result.Result;
import com.jxcia.blog.mapper.admin.AdminMapper;
import com.jxcia.blog.mapper.user.UserMapper;
import com.jxcia.blog.pojo.entity.AccessToken;
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
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;
    @Value("${jwt.tokenHead}")
    private String head;

    @Anonymous
    @PostMapping("/refresh")
    public Result<AccessToken> refresh(@RequestHeader("Authorization") String authHeader) {
        String refreshToken = authHeader.replace(head, "");

        if (!jwtTokenUtil.validateToken(refreshToken)) return Result.unauthorized("登录已过期");

        String jti = jwtTokenUtil.getClaimsJtiFromToken(refreshToken);
        if (isBlacklisted(jti)) {
            return Result.unauthorized("登录已过期");
        }

        String type = jwtTokenUtil.getClaimsTypeFromToken(refreshToken);
        Integer id = jwtTokenUtil.getClaimsIdFromToken(refreshToken);
        String email = jwtTokenUtil.getClaimsEmailFromToken(refreshToken);

        String accessToken = JwtTokenUtil.isAdmin(type)
                ? jwtTokenUtil.generateAdminAccessToken(adminMapper.getByEmail(email))
                : jwtTokenUtil.generateUserAccessToken(userMapper.getUserById(id));

        return Result.success(AccessToken.builder()
                .token(accessToken)
                .tokenHead(head)
                .build());
    }


    @AuthRequired
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

    /**
     * 检查 token 是否在黑名单中。
     * Redis 不可用时降级放行（fail-open）。
     */
    private boolean isBlacklisted(String jti) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(TokenConstant.BLACKLIST_PREFIX + jti));
        } catch (Exception e) {
            log.warn("Redis 不可用，黑名单检查降级放行: jti={}", jti);
            return false;
        }
    }
}
