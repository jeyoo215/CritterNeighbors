package com.critter.critter_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 URL 경로에 대해
                .allowedOrigins("http://localhost:5173") // 2. 리액트 주소 완벽 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. Preflight(OPTIONS)를 포함한 모든 메서드 허용
                .allowedHeaders("*") // 4. 모든 헤더 허용
                .allowCredentials(true); // 5. 쿠키/세션 통신 필요시 허용
    }
}