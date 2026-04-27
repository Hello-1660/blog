package com.jxcia.blog.common.result;

import lombok.Getter;

/**
 * 统一返回对象结果
 * @param <T>
 */
@Getter
public class Result<T> {
    // 返回状态码
    private final long code;
    // 返回数据
    private final T data;
    // 返回信息
    private final String message;

    private Result (long code, String massage, T data) {
        this.code = code;
        this.message = massage;
        this.data = data;
    }

    /**
     * 获取成功返回结果
     * @return 返回结果对象
     */
    public static Result<Void> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMassage(), null);
    }


    /**
     * 获取成功返回结果对象
     * @param data 返回数据
     * @return 返回结果对象
     * @param <T> 返回数据类型
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMassage(), data);
    }

    /**
     * 获取成返回对象
     * @param message 返回信息
     * @param data 返回数据
     * @return 返回结果对象
     * @param <T> 返回数据类型
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 获取返回失败结果对象
     * @return 返回结果对象
     */
    public static Result<Void> validateFailed () {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getMassage(), null);
    }

    /**
     * 获取返回失败结果对象
     * @param massage 异常信息
     * @return 返回结果对象
     */
    public static Result<Void> validateFailed(String massage) {
        return new Result<>(ResultCode.VALIDATE_FAILED.getCode(), massage, null);
    }

    /**
     * 获取返回失败结果对象
     * @return 返回结果对象
     */
    public static Result<Void> unauthorized () {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMassage(), null);
    }

    /**
     * 获取返回失败结果对象
     * @param massage 提示信息
     * @return 返回结果对象
     */
    public static Result<Void> unauthorized(String massage) {
        return new Result<>(ResultCode.UNAUTHORIZED.getCode(), massage, null);
    }

    /**
     * 获取返回失败结果对象
     * @return 返回结果对象
     */
    public static Result<Void> forbidden () {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMassage(), null);
    }

    /**
     * 获取返回失败结果对象
     * @param massage 提示信息
     * @return 返回结果对象
     */
    public static Result<Void> forbidden(String massage) {
        return new Result<>(ResultCode.FORBIDDEN.getCode(), massage, null);
    }
}
