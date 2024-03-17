package com.civilwar.boardsignal.chat.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.auth.infrastructure.JwtTokenProvider;
import com.civilwar.boardsignal.chat.application.ChatMessageService;
import com.civilwar.boardsignal.chat.dto.ApiChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.request.ChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageResponse;
import com.civilwar.boardsignal.chat.mapper.ChatMessageApiMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StompMessageController {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    //AccessToken -> UserId 조회
    private Long getUserId(String token) {
        String accessToken = token.split(" ")[1];
        TokenPayload payLoad = jwtTokenProvider.getPayLoad(accessToken);
        return  payLoad.userId();
    }

    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
        @DestinationVariable(value = "roomId") Long roomId,
        @Header(AUTHORIZATION) String token,
        @Payload ApiChatMessageRequest apiChatMessageRequest
    ) {
        Long userId = getUserId(token);

        log.info("roomId = {}, userId = {}, request.content = {}, request.type = {}",
            roomId, userId, apiChatMessageRequest.content(), apiChatMessageRequest.type());

        //채팅 저장
        ChatMessageRequest chatMessageRequest = ChatMessageApiMapper.toChatMessageRequest(roomId,
            userId, apiChatMessageRequest);
        ChatMessageResponse chatMessageResponse = chatMessageService.recordChat(chatMessageRequest);

        //채팅 전송
        simpMessageSendingOperations.convertAndSend("/topic/chats/" + roomId, chatMessageResponse);
    }

    //Disconnect 시키기 전, 프론트에서 송신할 메시지 경로
    @MessageMapping("/chats/exit/{roomId}")
    public void exit(
        @DestinationVariable(value = "roomId") Long roomId,
        @Header(AUTHORIZATION) String token
    ) {
        Long userId = getUserId(token);

        LocalDateTime exitTime = chatMessageService.exitChatRoom(userId, roomId);

        log.info("User Exit Time -> {}", exitTime);
    }


}
