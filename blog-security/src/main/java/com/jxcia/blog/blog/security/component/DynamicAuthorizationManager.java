package com.jxcia.blog.blog.security.component;

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
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        // 获取认证信息
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null) return new AuthorizationDecision(false);

        // 获取当前请求所需要的权限
        Collection<ConfigAttribute> configAttribute = getAuthorities(context);
        // 没有配置权限，直接放行
        if (CollectionUtils.isEmpty(configAttribute)) return new AuthorizationDecision(true);

        AntPathMatcher antMatcher = new AntPathMatcher();
        // 比较权限
        for (ConfigAttribute attribute : configAttribute) {
            String needAuthority = attribute.getAttribute();

            for (GrantedAuthority grantAuthority : authentication.getAuthorities()) {
                // 拥有权限，放行
                if (antMatcher.match(needAuthority, grantAuthority.getAuthority())) return new AuthorizationDecision(true);
            }
        }

        // 没有权限，拒绝
        return new AuthorizationDecision(false);
    }

    /**
     * 从动态权限服务获取当前请求所需权限
     * @param context
     * @return
     */
    private Collection<ConfigAttribute> getAuthorities(RequestAuthorizationContext context) {
        return dynamicSecurityMetadataSource.getAllConfigAttributes(context.getRequest());
    }
}
