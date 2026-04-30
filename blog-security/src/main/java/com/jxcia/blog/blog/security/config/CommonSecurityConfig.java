package com.jxcia.blog.blog.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxcia.blog.blog.security.component.DynamicAuthorizationManager;
import com.jxcia.blog.blog.security.component.DynamicSecurityMetadataSource;
import com.jxcia.blog.blog.security.component.RestAuthenticationEntryPoint;
import com.jxcia.blog.blog.security.component.RestfulAccessDeniedHandler;
import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.crypto.Pbkdf2PasswordEncoder;
import com.jxcia.blog.blog.security.fliter.JwtAuthenticationFilter;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringSecurity 配置类，配置所有通过 bean
 */
@Configuration
public class CommonSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }

    @Bean
    public DynamicAuthorizationManager dynamicAuthorizationManager() {
        return new DynamicAuthorizationManager();
    }

    @Bean
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSourceWithJwt() {
        return new DynamicSecurityMetadataSource();
    }

    @Bean
    public IgnoreUrlsConfig ignoreUrlsConfig() {
        return new IgnoreUrlsConfig();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
        return new RestfulAccessDeniedHandler();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
