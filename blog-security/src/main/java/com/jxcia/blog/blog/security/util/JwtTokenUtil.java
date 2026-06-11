package com.jxcia.blog.blog.security.util;

import com.jxcia.blog.blog.security.enums.AccountType;
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
import java.util.*;

/**
 * jwt 工具类
 */
@Slf4j
public class JwtTokenUtil {
    // 用户编号键名
    private static final String ID = "sub";
    // 用户邮箱键名
    private static final String EMAIL = "email";
    // 用户身份键名
    private static final String IDENTIFY = "type";
    // 用户角色列表
    private static final String ROLES = "roles";
    // 密钥
    @Value("${jwt.secret}")
    private String secret;
    // 过期时间
    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;
    @Value("${jwt.accessExpiration}")
    private Long accessExpiration;
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
    private String generateToken(Map<String, Object> claims, Long expirationMs) {
        return Jwts.builder()
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
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
        return getClaimsFromToken(token).get(IDENTIFY, String.class);
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
     * 获取角色权限列表
     *
     * @param token token
     * @return 权限列表
     */
    public List<String> getClaimsRolesFromToken(String token) {
        return getClaimsFromToken(token).get(ROLES, List.class);
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
     * 判断是否是管理员账号
     * @param type 账号类型
     * @return 是返回 true, 否返回 false
     */
    public static boolean isAdmin (String type) {
        return AccountType.ADMIN.toString().equals(type);
    }

    /**
     * 判断是否是用户账号
     * @param type 账号类型
     * @return 是返回 true, 否返回 false
     */
    public static boolean isUser (String type) {
        return AccountType.USER.toString().equals(type);
    }

    /**
     * 生成 AccessToken
     * @param user 用户信息
     * @return AccessToken
     */
    public String generateUserAccessToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();

        claims.put(ID, user.getId().toString());
        claims.put(IDENTIFY, AccountType.USER.toString());
        claims.put(EMAIL, user.getEmail());
        claims.put(ROLES, List.of("ROLE_USER"));

        return generateToken(claims, accessExpiration);
    }

    public String generateAdminAccessToken(Admin admin) {
        HashMap<String, Object> claims = new HashMap<>();

        claims.put(ID, admin.getId().toString());
        claims.put(IDENTIFY, AccountType.ADMIN.toString());
        claims.put(EMAIL, admin.getEmail());
        claims.put(ROLES, List.of("ROLE_ADMIN"));

        return generateToken(claims, accessExpiration);
    }

    /**
     * 生成 Refresh Token
     * @param id 用户编号
     * @param email 用户邮箱
     * @param type 用户类型
     * @return refresh token
     */
    public String generateRefreshToken(Integer id, String email, AccountType type) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(ID, id.toString());
        claims.put(IDENTIFY, type.toString());
        claims.put(EMAIL, email);

        return generateToken(claims, refreshExpiration);
    }

    /**
     * 从 token 中提取 jti
     * @param token token
     * @return jtl
     */
    public String getClaimsJtiFromToken(String token) {
        return getClaimsFromToken(token).getId();
    }

    /**
     * 获取 token 过期时间
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }
}
