package com.jxcia.blog.blog.security.fliter;

import com.jxcia.blog.blog.security.component.AdminUserDetailService;
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
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.context.SecurityContextHolder;
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

        if (jwtTokenUtil.validateToken(token)) {
            // 从 token 中获取邮箱
            String email = jwtTokenUtil.getClaimsEmailFromToken(token);
            String type = jwtTokenUtil.getClaimsTypeFromToken(token);
            Integer id = jwtTokenUtil.getClaimsIdFromToken(token);
            // 根据邮箱从数据中中查询用户
            log.info("checked email: {}", email);
            // 获取账号详情
            CustomUserDetails userDetails = getCustomUserDetails(type, email, id);
            if (userDetails != null) {// 创建 SpringSecurity 令牌
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息存入 SpringSecurity 上下文当中
                log.info("authenticated user: {}", userDetails.getUsername());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 根据 type 获取 CustomUserDetails
     * @param type 账号类型
     * @param email 账号邮箱
     * @param id 账号编号
     * @return 用户详情
     */
    private CustomUserDetails getCustomUserDetails(String type, String email, Integer id) {
        CustomUserDetails userDetails = null;

        if (JwtTokenUtil.isAdmin(type)) {
            // 当前账号是管理员，配置权限
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        } else if (JwtTokenUtil.isUser(type)) {
            // 普通用户直接返回信息
            userDetails = CustomUserDetails.builder()
                    .id(id)
                    .email(email)
                    .build();
        }

        if (userDetails == null) {
            // TODO 抛出没有该用户异常
            return null;
        } else {
            return userDetails;
        }
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
