package com.jxcia.blog.service.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * ip 工具类
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";

    public static String getClientIp(HttpServletRequest request) {
        String ip = null;

        // 处理 Nginx 代理
        ip = request.getHeader("x-forwarded-for");
        if (isValid(ip)) {
            // 多级代理，取第一个
            int index = ip.indexOf(',');
            if (index != -1) ip = ip.substring(0, index);
            return ip.trim();
        }

        // 处理 Nginx 常用头
        ip = request.getHeader("X-Real-IP");
        if (isValid(ip)) return ip.trim();

        // 处理直连 ip
        return request.getRemoteAddr();
    }


    /**
     * 判断 ip 是否有效
     * @param ip ip
     * @return 是否有效
     */
    private static boolean isValid(String ip) {
        return ip != null && !ip.isEmpty() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
