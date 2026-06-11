package com.jxcia.blog.blog.security.filter;

import com.jxcia.blog.blog.security.service.AdminUserDetailService;
import com.jxcia.blog.blog.security.service.CustomUserDetails;
import com.jxcia.blog.blog.security.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.jxcia.blog.common.constant.TokenConstant.BLACKLIST_PREFIX;

/**
 * jwt 拦截器，
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private AdminUserDetailService adminUserDetailService;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中提取 token
        String token = parseJwt(request);

        if (jwtTokenUtil.validateToken(token)) {
            String jti = jwtTokenUtil.getClaimsJtiFromToken(token);
            if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti))) {
                // token 已登出
                filterChain.doFilter(request, response);
                return;
            }

            // 从 token 中获取邮箱
            String email = jwtTokenUtil.getClaimsEmailFromToken(token);
            String type = jwtTokenUtil.getClaimsTypeFromToken(token);
            Integer id = jwtTokenUtil.getClaimsIdFromToken(token);
            List<String> roles = jwtTokenUtil.getClaimsRolesFromToken(token);
            // 根据邮箱从数据中中查询用户
            log.info("checked email: {}", email);
            // 获取账号详情
            UsernamePasswordAuthenticationToken authentication = createAuthentication(type, email, id, roles);
            if (authentication != null) {
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 获取用户信息
     * @param type 用户身份
     * @param email 用户邮箱
     * @param id 用户编号
     * @param roles 用户角色列表
     * @return 用户信息
     */
    private UsernamePasswordAuthenticationToken createAuthentication(String type, String email, Integer id, List<String> roles) {
       if (JwtTokenUtil.isAdmin(type)) {
           CustomUserDetails userDetails = (CustomUserDetails) adminUserDetailService.loadUserByUsername(email);
           return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
       }

       if (JwtTokenUtil.isUser(type)) {
           List<GrantedAuthority> authorities = roles.stream()
                   .map(SimpleGrantedAuthority::new)
                   .collect(Collectors.toList());

           CustomUserDetails userDetails = CustomUserDetails.builder()
                   .id(id)
                   .email(email)
                   .authorities(authorities)
                   .build();

           return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
       }

       return null;
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
}
