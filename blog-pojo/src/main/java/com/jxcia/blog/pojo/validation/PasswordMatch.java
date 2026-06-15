package com.jxcia.blog.pojo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface PasswordMatch {
    String message() default "两次密码输入不一致";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
