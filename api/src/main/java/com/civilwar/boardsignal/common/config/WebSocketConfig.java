package com.civilwar.boardsignal.common.config;

import com.civilwar.boardsignal.chat.handler.StompAuthenticationHandler;
import com.civilwar.boardsignal.chat.handler.StompExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 클라이언트 인증 핸들러
//    private final StompAuthenticationHandler stompAuthenticationHandler;
    // WebSocket 내 예외 핸들러
//    private final StompExceptionHandler stompExceptionHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //websocket 연결 엔드포인트 설정
        registry
            .addEndpoint("/ws/chats")
            .setAllowedOriginPatterns("*")
            .withSockJS();
        //예외 핸들러 등록
//        registry.setErrorHandler(stompExceptionHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //클라이언트 메시지 수신 구독 설정 경로
        registry.enableSimpleBroker("/topic");
        //클라이언트 메시지 송신 경로 prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    //클라이언트 -> 서버 요청 시, 인증 핸들러 등록
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(stompAuthenticationHandler);
    }
}
