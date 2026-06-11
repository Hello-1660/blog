package com.jxcia.blog.blog.security.annotation;

import java.lang.annotation.*;

/**
 * 封闭接口，要检查 token
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthRequired {
}
