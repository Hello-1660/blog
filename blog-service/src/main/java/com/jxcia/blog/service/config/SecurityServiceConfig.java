package com.jxcia.blog.service.config;

import com.jxcia.blog.blog.security.service.CustomUserDetails;
import com.jxcia.blog.pojo.entity.User;
import com.jxcia.blog.service.mapper.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class SecurityServiceConfig {
    @Autowired
    private UserMapper userMapper;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userMapper.findByEmail(email);
            if (user == null) return null;

            // TODO 设置角色
            return CustomUserDetails.builder()
                    .email(email)
                    .password(user.getPassword())
                    .id(user.getId())
                    .build();
        };
    }
}
