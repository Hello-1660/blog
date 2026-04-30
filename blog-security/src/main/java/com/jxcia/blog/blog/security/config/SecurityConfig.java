package com.jxcia.blog.blog.security.config;

import com.jxcia.blog.blog.security.component.DynamicAuthorizationManager;
import com.jxcia.blog.blog.security.component.RestAuthenticationEntryPoint;
import com.jxcia.blog.blog.security.component.RestfulAccessDeniedHandler;
import com.jxcia.blog.blog.security.fliter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private DynamicAuthorizationManager dynamicAuthorizationManager;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(ignoreUrlsConfig.getUrls().toArray(new String[0])).permitAll() // 白名单
                        .requestMatchers(HttpMethod.OPTIONS).permitAll() // 跨域 OPTIONS
                        .anyRequest().access(dynamicAuthorizationManager)
                )
                .csrf(AbstractHttpConfigurer::disable) // 关闭 csrf
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(restfulAccessDeniedHandler) // 权限不足
                        .authenticationEntryPoint(restAuthenticationEntryPoint) //  未登录
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
