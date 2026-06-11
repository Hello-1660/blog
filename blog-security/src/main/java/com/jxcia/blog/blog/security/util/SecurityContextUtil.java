package com.jxcia.blog.blog.security.util;

import com.jxcia.blog.blog.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * securityContext 工具类
 */
public class SecurityContextUtil {

    /**
     * 获取 id
     * @return 用户 id
     */
    public static Integer getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        // 当存在用户信息时返回 id, 没有则返回 null
        if (principal instanceof CustomUserDetails user) {
            return user.getId();
        } else {
            return null;
        }
    }


    /**
     * 获取用户邮箱
     * @return 用户邮箱
     */
    public static String getEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;

        Object principal = auth.getPrincipal();
        // 当存在用户信息时返回 id, 没有则返回 null
        if (principal instanceof CustomUserDetails user) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    /**
     * 获取用户权限集合
     * @return 用户权限集合
     */
    public static List<GrantedAuthority> getAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails user)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(user.getAuthorities());
    }

    /**
     * 判断是否存有用户信息
     * @return 是否存有用户信息
     */
    public static boolean isAuthenticated() {
        return getId() != null;
    }
}
