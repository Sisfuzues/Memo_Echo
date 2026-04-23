package com.memoecho.persistence.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



/**
 * JWT 认证入口点
 *
 * 当用户访问受保护的资源但没有提供有效的认证信息时，
 * Spring Security 会调用此类的 commence 方法
 *
 * 使用场景：
 * 1. 用户未登录就访问需要认证的接口
 * 2. 用户提供的 JWT Token 无效或已过期
 * 3. 用户提供的 JWT Token 格式错误
 *
 * 实现 AuthenticationEntryPoint 接口可以自定义 401 Unauthorized 的响应内容
 */
@Slf4j(topic = "认证失败")
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 1. 记录错误日志
        // 记录未授权访问的详细信息，便于排查问题
        log.info("Unauthorized error: {}", authException.getMessage());

        // 2. 设置响应内容类型为 JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // MediaType.APPLICATION_JSON_VALUE = "application/json"

        // 3. 设置 HTTP 状态码为 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // SC_UNAUTHORIZED = 401

        // 4. 构建响应体
        // 使用 Map 构建 JSON 格式的响应数据
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        // status: HTTP 状态码（401）

        body.put("error", "Unauthorized");
        // error: 错误类型

        body.put("message", authException.getMessage());
        // message: 详细的错误信息（例如："Bad credentials"）

        body.put("path", request.getServletPath());
        // path: 请求的路径（例如："/api/test/user"）

        // 5. 将 Map 转换为 JSON 并写入响应
        ObjectMapper mapper = new ObjectMapper();
        // ObjectMapper 是 Jackson 库的核心类，用于 Java 对象和 JSON 之间的转换
        mapper.writeValue(response.getOutputStream(), body);
        // writeValue: 将对象序列化为 JSON 并写入输出流
    }
}
