package com.civilwar.boardsignal.chat.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompLogHandler implements ChannelInterceptor {

    //클라이언트에서 요청을 보낼 때 실행
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        //Stomp 헤더 접근
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (command!=null) {
            log.info(command.name());
        }

        return message;
    }
}
