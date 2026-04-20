package com.memoecho.bot_gateway.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j(topic =  "napcat配置")
public class NapcatFeignConf {
    @Value("${napcat.token}")
    private String napcatToken;

    @Bean
    public RequestInterceptor requestInterceptor(){

        return requestTemplate -> {
//            log.warn("拦截器已成功触发:{}", requestTemplate.url());
            //如果是发向napcat的，拦截
            if(requestTemplate.feignTarget().url().contains("3011")){
//                log.warn("已经获取token:{}",napcatToken);
                requestTemplate.header("Authorization",napcatToken);
            }
        };
    }
}
