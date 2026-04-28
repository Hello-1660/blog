package com.jxcia.blog.service.handler;

import com.jxcia.blog.common.exception.BaseException;
import com.jxcia.blog.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public Result<Void> exception(BaseException e, HttpServletRequest request) {
        log.error("error on path {}: {}", request.getRequestURI(), e.getMessage());
        return Result.Failed(e.getMessage());
    }
}
