package com.jxcia.blog.pojo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            Field passwordField = findField(value.getClass(), "password");
            Field confirmPasswordField = findField(value.getClass(), "confirmPassword");

            if (passwordField == null || confirmPasswordField == null) return true;

            passwordField.setAccessible(true);
            confirmPasswordField.setAccessible(true);

            Object password = passwordField.get(value);
            Object confirmPassword = confirmPasswordField.get(value);

            if (password == null && confirmPassword == null) return true;
            if (password == null || confirmPassword == null) return false;

            return password.equals(confirmPassword);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
