package com.jxcia.blog.common.exception;

/**
 * 服务异常（系统级错误）
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
