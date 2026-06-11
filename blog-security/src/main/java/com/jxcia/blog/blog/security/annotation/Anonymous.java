package com.jxcia.blog.blog.security.annotation;

import java.lang.annotation.*;

/**
 * 完全开发接口，不检查 token
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {
}
