package com.jxcia.blog.common.result;

import lombok.Getter;

/**
 * API正确返回码枚举
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数校验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");

    private final long code;
    private final String massage;

    private ResultCode(long code, String massage) {
        this.code = code;
        this.massage = massage;
    }
}
