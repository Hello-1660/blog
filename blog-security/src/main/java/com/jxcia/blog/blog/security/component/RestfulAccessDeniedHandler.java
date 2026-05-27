package com.jxcia.blog.blog.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxcia.blog.common.constant.AdminConstant;
import com.jxcia.blog.common.result.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

/**
 * 自定义无权限访问的返回结果
 */
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control","no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(objectMapper.writeValueAsString(Result.forbidden(AdminConstant.NOT_PERMISSION)));
        response.getWriter().flush();
    }
}
