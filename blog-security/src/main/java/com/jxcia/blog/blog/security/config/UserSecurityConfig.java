package com.jxcia.blog.blog.security.config;

import com.jxcia.blog.blog.security.authorization.DynamicAuthorizationManager;
import com.jxcia.blog.blog.security.handler.RestAuthenticationEntryPoint;
import com.jxcia.blog.blog.security.handler.RestfulAccessDeniedHandler;
import com.jxcia.blog.blog.security.filter.JwtAuthenticationFilter;
import com.jxcia.blog.blog.security.metadata.DynamicSecurityMetadataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class UserSecurityConfig {
    @Autowired
    private DynamicAuthorizationManager dynamicAuthorizationManager;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @Order(2)
    public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login").permitAll() // 放行登录接口
                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().access(dynamicAuthorizationManager) // 动态鉴权
                )
                .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
