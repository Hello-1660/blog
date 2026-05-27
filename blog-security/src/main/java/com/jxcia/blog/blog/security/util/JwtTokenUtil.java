package com.jxcia.blog.blog.security.util;

import com.jxcia.blog.pojo.entity.Admin;
import com.jxcia.blog.pojo.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt 工具类
 */
@Slf4j
public class JwtTokenUtil {
    // 用户编号键名
    private static final String ID = "sub";
    // 用户邮箱键名
    private static final String EMAIL = "email";
    private static final String TYPE = "T";
    // 用户类型值
    private static final String USER = "u";
    // 管理员类型值
    private static final String ADMIN = "a";
    // 密钥
    @Value("${jwt.secret}")
    private String secret;
    // 过期时间
    @Value("${jwt.expiration}")
    private Long expiration;
    // 负载头
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    // 对称密钥
    private Key key;


    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 根据负载生成 token
     * @param claims 负载
     * @return token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .expiration(generateExpiration(expiration))
                .signWith(key)
                .compact();
    }

    /**
     * 生成 token 过期日期
     * @param expiration 有效时间
     * @return 过期日期
     */
    private Date generateExpiration(Long expiration) {
        return new Date(System.currentTimeMillis() + expiration);
    }

    /**
     * 生成用户 token
     * @param user 用户
     * @return token
     */
    public String generateUserToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(ID, user.getId().toString());
        claims.put(TYPE, USER);
        claims.put(EMAIL, user.getEmail());

        return generateToken(claims);
    }

    /**
     * 生成管理员 token
     * @param admin 管理员
     * @return token
     */
    public String generateAdminToken(Admin admin) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(ID, admin.getId().toString());
        claims.put(TYPE, ADMIN);
        claims.put(EMAIL, admin.getEmail());

        return generateToken(claims);
    }

    /**
     * 获取全部负载
     * @param token token
     * @return 负载
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取编号
     * @param token token
     * @return 编号
     */
    public Integer getClaimsIdFromToken(String token) {
        return Integer.valueOf(getClaimsFromToken(token).get(ID, String.class));
    }

    /**
     * 获取身份
     * @param token token
     * @return 身份
     */
    public String getClaimsTypeFromToken(String token) {
        return getClaimsFromToken(token).get(TYPE, String.class);
    }

    /**
     * 获取邮箱
     * @param token token
     * @return 邮箱
     */
    public String getClaimsEmailFromToken(String token) {
        return getClaimsFromToken(token).get(EMAIL, String.class);
    }

    /**
     * 验证 token 是否有效
     * @param token token
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid token format: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Token signature invalid: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token claims empty: {}", e.getMessage());
        } catch (NullPointerException e) {
            log.warn("Token is null: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 判断 token 是否过期
     * @param token token
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 刷新 token
     * @param token 旧 token
     * @return 新 token
     */
    public String refreshToken(String token) {
        if (!validateToken(token) || isTokenExpired(token)) return null;

        // 重新生成 claims
        Integer id = getClaimsIdFromToken(token);
        String type = getClaimsTypeFromToken(token);

        if (USER.equals(type)) {
            return generateUserToken(User.builder().id(id).build());
        } else if (ADMIN.equals(type)) {
            return generateAdminToken(Admin.builder().build());
        } else {
            return null;
        }
    }

    /**
     * 提取 token，去除 tokenHead
     * @param token 原始 token
     * @return 处理后 token
     */
    public String processToken(String token) {
        return token.substring(tokenHead.length());
    }

    /**
     * 判断是否是管理员账号
     * @param type 账号类型
     * @return 是返回 true, 否返回 false
     */
    public static boolean isAdmin (String type) {
        return ADMIN.equals(type);
    }

    /**
     * 判断是否是用户账号
     * @param type 账号类型
     * @return 是返回 true, 否返回 false
     */
    public static boolean isUser (String type) {
        return USER.equals(type);
    }
}
