package com.civilwar.boardsignal.chat.handler;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.civilwar.boardsignal.auth.exception.AuthErrorCode;
import com.civilwar.boardsignal.auth.infrastructure.JwtTokenProvider;
import com.civilwar.boardsignal.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class StompAuthenticationHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    private void validateHeader(String token) {
        if (StringUtils.hasText(token)) {
            String accessToken = token.split(" ")[1];
            jwtTokenProvider.validateToken(accessToken);
        } else {
            throw new ValidationException(AuthErrorCode.AUTH_REQUIRED);
        }
    }

    //클라이언트에서 요청을 보내기 전 실행되는 메서드
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        //Stomp 헤더 접근
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        //첫 연결이거나 메시지를 보낼 때 검증
        if (command == StompCommand.CONNECT || command == StompCommand.SEND) {
            //AccessToken 검증
            String token = accessor.getFirstNativeHeader(AUTHORIZATION);
            validateHeader(token);
        }

        return message;
    }

}
