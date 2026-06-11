package com.jxcia.blog.blog.security.authorization;

import com.jxcia.blog.blog.security.metadata.DynamicSecurityMetadataSource;
import com.jxcia.blog.blog.security.service.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.function.Supplier;


/**
 * 动态权限决策管理器
 */
@Slf4j
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication auth = authenticationSupplier.get();

        // 未登录，拒绝
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) return new AuthorizationDecision(false);
        // 检查是否是管理员
        if (!hasAdmin(auth)) return new AuthorizationDecision(false);

        // 检查权限
        Collection<ConfigAttribute> requiredAttrs = dynamicSecurityMetadataSource.getAllConfigAttributes(context.getRequest());
        // 不在权限表里，登录直接访问
        if (CollectionUtils.isEmpty(requiredAttrs)) return new AuthorizationDecision(true);
        // 匹配权限
        AntPathMatcher matcher = new AntPathMatcher();
        for (ConfigAttribute attr : requiredAttrs) {
            for (GrantedAuthority grant : auth.getAuthorities()) {
                if (matcher.match(attr.getAttribute(), grant.getAuthority())) {
                    return new AuthorizationDecision(true);
                }
            }
        }

        return new AuthorizationDecision(false);
    }

    /**
     * 判断是否是管理员
     * @param auth
     * @return
     */
    private boolean hasAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
