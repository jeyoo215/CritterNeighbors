package com.critter.critter_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync // 스프링의 비동기 연산 기능을 전역적으로 활성화
public class AsyncConfig {
}