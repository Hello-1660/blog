package com.jxcia.blog.common.exception;

/**
 * 服务异常（系统级错误）
 */
public class ServiceException extends BaseException {
    public ServiceException(String message) {
        super(message);
    }
}
