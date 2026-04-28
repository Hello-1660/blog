package com.jxcia.blog.blog.security.crypto;

/**
 * 密码编码器接口
 */
public interface PasswordEncoder {

    /**
     * 对明文密码进行加密
     * @param rawPassword 明文密码
     * @return 加密密码
     */
    String encode(CharSequence rawPassword);

    /**
     * 检验明文密码是否与编码后的密码匹配
     * @param rawPassword 明文密码
     * @param encodedPassword 加密密码
     * @return 匹配结果
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);
}
