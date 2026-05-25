package com.jxcia.blog.blog.security.util;

import com.jxcia.blog.blog.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public static boolean hasData(Authentication auth) {
        if (auth == null) return false;
        return auth.getPrincipal() instanceof CustomUserDetails;
    }
}
