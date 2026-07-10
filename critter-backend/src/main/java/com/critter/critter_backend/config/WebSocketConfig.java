package com.critter.critter_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 데이터를 구독할 때 사용할 프리픽스 설정
        config.enableSimpleBroker("/topic", "/queue");
        
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 프리픽스 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 리액트(프론트엔드)에서 소켓 연결을 시도할 엔드포인트 주소 설정
        registry.addEndpoint("/ws-ecosystem")
                .setAllowedOrigins("http://localhost:5173") 
                .setAllowedOriginPatterns("*") 
                .withSockJS()
                .setInterceptors(new HttpSessionHandshakeInterceptor());
    }
}