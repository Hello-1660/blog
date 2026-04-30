package com.jxcia.blog.blog.security.fliter;

import com.jxcia.blog.blog.security.config.IgnoreUrlsConfig;
import com.jxcia.blog.blog.security.service.CustomUserDetails;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * jwt 拦截器，
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中提取 token
        String token = parseJwt(request);

        // 白名单，直接放行
        if (isWitheList(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwtTokenUtil.validateToken(token)) {
            // 从 token 中获取邮箱
            String email = jwtTokenUtil.getClaimsEmailFromToken(token);
            // 根据邮箱从数据中中查询用户
            log.info("checked email: {}", email);
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
            // 创建 SpringSecurity 令牌
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将认证信息存入 SpringSecurity 上下文当中
            log.info("authenticated user: {}", userDetails.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头获取 jwt
     * @param request 请求头
     * @return jwt
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(tokenHeader);
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(tokenHead)) {
            return headerAuth.substring(tokenHead.length());
        }
        
        return null;
    }

    /**
     * 判断是否在白名单内
     * @param uri 用户访问 uri
     * @return 是否在白名单内
     */
    private boolean isWitheList(String uri) {
        List<String> urls = ignoreUrlsConfig.getUrls();
        for (String pattern : urls) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }
}
