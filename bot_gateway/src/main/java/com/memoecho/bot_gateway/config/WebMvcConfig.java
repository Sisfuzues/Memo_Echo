package com.memoecho.bot_gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final BotAdminAuthInterceptor botAdminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(botAdminAuthInterceptor)
                .addPathPatterns("/bot/**")
                .excludePathPatterns("/bot/webhook");
    }
}
