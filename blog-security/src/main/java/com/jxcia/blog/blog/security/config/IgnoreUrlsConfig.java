package com.jxcia.blog.blog.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 访问白名单配置类
 */
@Data
@ConfigurationProperties(prefix = "security.ignored")
public class IgnoreUrlsConfig {
    // 用户访问白名单
    private List<String> urls = new ArrayList<>();
}
