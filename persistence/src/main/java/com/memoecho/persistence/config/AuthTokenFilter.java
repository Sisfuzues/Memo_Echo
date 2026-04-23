package com.memoecho.persistence.config;

import com.memoecho.persistence.service.Impl.UserDetailServiceImpl;
import com.memoecho.persistence.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if(jwt != null && jwtUtil.validateJwtToken(jwt)) {
                Long userId = jwtUtil.getUserIdFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                System.out.println("========== 无 JWT 或验证失败，继续请求 ==========");
            }
        } catch (Exception e) {
            System.out.println("========== JWT 过滤器异常: " + e.getMessage() + " ==========");
            e.printStackTrace();
        }
        
        // ✅ 关键：无论有没有 JWT，都必须继续过滤链！
        filterChain.doFilter(request, response);
    }
    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(headerAuth != null && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
