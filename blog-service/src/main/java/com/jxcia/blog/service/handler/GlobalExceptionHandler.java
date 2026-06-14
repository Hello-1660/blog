package com.jxcia.blog.service.handler;

import com.jxcia.blog.common.exception.BaseException;
import com.jxcia.blog.common.exception.ServiceException;
import com.jxcia.blog.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Result<Void> handleBaseException(BaseException e, HttpServletRequest request) {
        log.warn("business error on path {}: {}", request.getRequestURI(), e.getMessage());
        return Result.Failed(e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("service error on path {}: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.Failed(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message;
        if (e.getBindingResult().getFieldError() != null) {
            message = e.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e.getBindingResult().getGlobalError() != null) {
            message = e.getBindingResult().getGlobalError().getDefaultMessage();
        } else {
            message = "参数校验失败";
        }
        log.warn("validation failed on path {}: {}", request.getRequestURI(), message);
        return Result.validateFailed(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("validation failed on path {}: {}", request.getRequestURI(), message);
        return Result.validateFailed(message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("unhandled error on path {}: {}", request.getRequestURI(), e.getMessage(), e);
        return Result.Failed("服务器内部错误");
    }
}
