package com.civilwar.boardsignal.chat.handler;

import com.civilwar.boardsignal.common.exception.ValidationException;
import java.nio.charset.StandardCharsets;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    //예외 메시지 생성
    private Message<byte[]> prepareErrorMessage(String errorMessage, String code) {

        //헤더 유형 -> 에러
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(code);
        //헤더 내용 변경 불가 설정
        accessor.setLeaveMutable(true);

        //에러 메시지 생성 후 전달
        return MessageBuilder.createMessage(errorMessage.getBytes(StandardCharsets.UTF_8),
            accessor.getMessageHeaders());
    }

    //웹 소켓 통신 중 발생한 예외 -> MessageDeliveryException으로 배달
    private Throwable convertThrowException(Throwable throwable) {
        if (throwable instanceof MessageDeliveryException) {
            return throwable.getCause();
        }
        return throwable;
    }

    //예외 발생 시 실행되는 메서드
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
        Throwable ex) {

        //예외 변환
        Throwable exception = convertThrowException(ex);

        if (exception instanceof ValidationException ve) {
            //예외 메시지 생성
            return prepareErrorMessage(ve.getMessage(), ve.getCode());
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
}
