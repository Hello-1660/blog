package com.jxcia.blog.blog.security.crypto;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

/**
 * 密码编码器接口实现类
 * 基于 PBKDF2 的密码编码器实现
 */
public class Pbkdf2PasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    // 2026年推荐值
    private static final int DEFAULT_ITERATIONS = 310000;
    // 128位盐
    private static final int SALT_LENGTH = 16;
    // 256位哈希
    private static final int HASH_LENGTH = 32;

    private final int iterations;

    public Pbkdf2PasswordEncoder() {
        this(DEFAULT_ITERATIONS);
    }

    public Pbkdf2PasswordEncoder(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("iterations must be greater than 0");
        }
        this.iterations = iterations;
    }

    /**
     * 对明文密码进行加密
     *
     * @param rawPassword 明文密码
     * @return 加密密码
     */
    @Override
    public String encode(CharSequence rawPassword) {
        // 生成随机盐
        byte[] salt = generateSalt();
        // 计算 PBKDF2 哈希
        byte[] hash = pbkdf2(rawPassword.toString().toCharArray(), salt, iterations, HASH_LENGTH);
        // 编码为存储格式 算法：迭代次数:盐(Base64):哈希(Base64)
        return encodeToString(iterations, salt, hash);
    }

    /**
     * 检验明文密码是否与编码后的密码匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密密码
     * @return 匹配结果
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        try {
            // 解析存储串
            DecodedParams params = decodeFromString(encodedPassword);
            // 使用相同的盐和迭代次数计算哈希
            byte[] hashOfInput = pbkdf2(rawPassword.toString().toCharArray(), params.salt, params.iterations, params.hash.length);
            // 恒定时间比较
            return MessageDigest.isEqual(params.hash, hashOfInput);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成盐值
     * @return 盐值
     */
    private byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }


    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int hashLengthBytes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, hashLengthBytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } finally {
            // 清除内存中的密码数组
            Arrays.fill(password, '0');
        }
    }

    private String encodeToString(int iterations, byte[] salt, byte[] hash) {
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        String hashB64 = Base64.getEncoder().encodeToString(hash);
        return String.format("%s:%d:%s:%s", Pbkdf2PasswordEncoder.ALGORITHM, iterations, saltB64, hashB64);
    }


    private DecodedParams decodeFromString(String encodedPassword) {
        String[] parts = encodedPassword.split(":");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid password");
        }

        String algorithm = parts[0];
        if (!ALGORITHM.equals(algorithm)) {
            throw new IllegalArgumentException("Invalid algorithm");
        }

        int iterations = Integer.parseInt(parts[1]);
        byte[] salt = Base64.getDecoder().decode(parts[2]);
        byte[] hash = Base64.getDecoder().decode(parts[3]);

        return new DecodedParams(salt, iterations, hash);
    }


    private record DecodedParams(byte[] salt, int iterations, byte[] hash) {
    }
}
