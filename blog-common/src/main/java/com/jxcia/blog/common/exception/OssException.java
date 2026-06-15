package com.jxcia.blog.common.exception;

/**
 * OSS 服务异常
 */
public class OssException extends ServiceException {
    public OssException(String message) {
        super(message);
    }

    public OssException(String message, Throwable cause) {
        super(message, cause);
    }
}
