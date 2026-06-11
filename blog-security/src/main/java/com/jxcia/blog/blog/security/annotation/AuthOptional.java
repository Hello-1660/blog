package com.jxcia.blog.blog.security.annotation;

import java.lang.annotation.*;

/**
 * 半开放接口，访问私有状态需要检查 token
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthOptional {
}
