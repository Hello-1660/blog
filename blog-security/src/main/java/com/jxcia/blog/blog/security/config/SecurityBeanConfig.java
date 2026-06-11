package com.jxcia.blog.blog.security.config;

import com.jxcia.blog.blog.security.authorization.AccessLevelAuthorizationManager;
import com.jxcia.blog.blog.security.authorization.DynamicAuthorizationManager;
import com.jxcia.blog.blog.security.crypto.PasswordEncoder;
import com.jxcia.blog.blog.security.crypto.Pbkdf2PasswordEncoder;
import com.jxcia.blog.blog.security.filter.JwtAuthenticationFilter;
import com.jxcia.blog.blog.security.handler.RestAuthenticationEntryPoint;
import com.jxcia.blog.blog.security.handler.RestfulAccessDeniedHandler;
import com.jxcia.blog.blog.security.metadata.DynamicSecurityMetadataSource;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityBeanConfig {
    @Bean
    public AccessLevelAuthorizationManager accessDecisionManager() {
        return new AccessLevelAuthorizationManager();
    }

    @Bean
    public DynamicAuthorizationManager dynamicAuthorizationManager() {
        return new DynamicAuthorizationManager();
    }

    @Bean
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource() {
        return new DynamicSecurityMetadataSource();
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler() {
        return new RestfulAccessDeniedHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil() {
        return new JwtTokenUtil();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }
}
