package com.jxcia.blog.blog.security.util;

import com.jxcia.blog.blog.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * securityContext 工具类
 */
public class SecurityContextUtil {

    /**
     * 获取 id
     * @return
     */
    public static Integer getId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        // 当存在用户信息时返回 id, 没有则返回 null
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        } else {
            return null;
        }
    }

    /**
     * 判断是否存储数据
     * @param auth 用户详细
     * @return 是否含有信息
     */
    public static boolean isValid(Authentication auth) {
        if (auth == null) return true;
        return !(auth.getPrincipal() instanceof CustomUserDetails);
    }

    /**
     * 获取用户权限
     * @param auth 用户详细
     * @return 详情列表
     */
    public static List<GrantedAuthority> getAuthorities(Authentication auth) {
        if (isValid(auth)) return new ArrayList<>();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();

        if (principal == null)  {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(principal.getAuthorities());
        }
    }
}
