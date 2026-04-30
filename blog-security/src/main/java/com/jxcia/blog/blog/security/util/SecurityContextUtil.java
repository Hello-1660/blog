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
        CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
        return details.getId();
    }
}
