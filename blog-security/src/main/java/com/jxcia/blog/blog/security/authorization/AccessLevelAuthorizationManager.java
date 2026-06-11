package com.jxcia.blog.blog.security.authorization;

import com.jxcia.blog.blog.security.annotation.Anonymous;
import com.jxcia.blog.blog.security.annotation.AuthOptional;
import com.jxcia.blog.blog.security.annotation.AuthRequired;
import com.jxcia.blog.blog.security.enums.AccessLevel;
import com.jxcia.blog.blog.security.service.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class AccessLevelAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext>, InitializingBean {
    @Autowired
    private ApplicationContext applicationContext;
    // 遍历顺序
    private List<Map.Entry<String, AccessLevel>> orderedEntries;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> supplier, RequestAuthorizationContext ctx) {
        AccessLevel level = lookupAccessLevel(ctx.getRequest());
        return switch (level) {
            case ANONYMOUS -> new AuthorizationDecision(true);
            case OPTIONAL -> new AuthorizationDecision(true);
            case REQUIRED -> new AuthorizationDecision(hasValidAuth(supplier.get()));
        };
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        LinkedHashMap<String, AccessLevel> levels = new LinkedHashMap<>();
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapping.getHandlerMethods().entrySet()) {
            HandlerMethod method = entry.getValue();
            Set<String> patterns = entry.getKey().getDirectPaths();

            AccessLevel level = findAnnotation(method);

            for (String pattern : patterns) {
                levels.put(pattern, level);
            }
        }

        // 按照路径长度降序排序，匹配更精细
        this.orderedEntries = levels.entrySet().stream()
                .sorted((a, b) -> b.getKey().length() - a.getKey().length())
                .toList();
    }


    /**
     * 判断 Authentication 是否有效
     * @param auth Authentication
     * @return 是否有效
     */
    private boolean hasValidAuth(Authentication auth) {
        return auth != null && auth.getPrincipal() instanceof CustomUserDetails;
    }

    /**
     * 获取接口访问等级
     * @param request 请求
     * @return 接口访问等级
     */
    private AccessLevel lookupAccessLevel(HttpServletRequest request) {
        String path = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();

        for (Map.Entry<String, AccessLevel> entry : orderedEntries) {
            if (matcher.match(entry.getKey(), path)) return entry.getValue();
        }

        // 没有标注，必须登录
        return AccessLevel.REQUIRED;
    }

    /**
     * 返回访问等级
     * @param method 方法
     * @return 访问等级
     */
    private AccessLevel findAnnotation(HandlerMethod method) {
        // 方法级注解
        if (method.hasMethodAnnotation(AuthRequired.class)) return AccessLevel.REQUIRED;
        if (method.hasMethodAnnotation(AuthOptional.class)) return AccessLevel.OPTIONAL;
        if (method.hasMethodAnnotation(Anonymous.class)) return AccessLevel.ANONYMOUS;

        // 类级注解
        Class<?> beanType = method.getBeanType();
        if (beanType.isAnnotationPresent(AuthRequired.class)) return AccessLevel.REQUIRED;
        if (beanType.isAnnotationPresent(AuthOptional.class)) return AccessLevel.OPTIONAL;
        if (beanType.isAnnotationPresent(Anonymous.class)) return AccessLevel.ANONYMOUS;

        return AccessLevel.REQUIRED;
    }
}
