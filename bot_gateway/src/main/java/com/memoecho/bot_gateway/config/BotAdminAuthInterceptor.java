package com.memoecho.bot_gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoecho.common.response.ApiResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class BotAdminAuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;
    private final SecretKey signingKey;

    public BotAdminAuthInterceptor(
            ObjectMapper objectMapper,
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.objectMapper = objectMapper;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = parseBearerToken(request);
        if (token == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录或登录状态已失效");
            return false;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (!"ADMIN".equals(claims.get("role", String.class))) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "无管理员权限，禁止访问");
                return false;
            }

            return true;
        } catch (Exception e) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录或登录状态已失效");
            return false;
        }
    }

    private String parseBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.fail(status, message));
    }
}
