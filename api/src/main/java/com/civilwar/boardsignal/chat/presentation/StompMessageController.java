package com.civilwar.boardsignal.chat.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.auth.infrastructure.JwtTokenProvider;
import com.civilwar.boardsignal.chat.application.ChatMessageService;
import com.civilwar.boardsignal.chat.dto.ApiChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.request.ChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageResponse;
import com.civilwar.boardsignal.chat.mapper.ChatMessageApiMapper;
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

    @MessageMapping("/chats/{roomId}")
    public void sendMessage(
        @DestinationVariable(value = "roomId") Long roomId,
        @Header(AUTHORIZATION) String token,
        @Payload ApiChatMessageRequest apiChatMessageRequest
    ) {

        //AccessToken -> UserId 조회
        String accessToken = token.split(" ")[1];
        TokenPayload payLoad = jwtTokenProvider.getPayLoad(accessToken);
        Long userId = payLoad.userId();

        log.info("roomId = {}, userId = {}, request.content = {}, request.type = {}",
            roomId, userId, apiChatMessageRequest.content(), apiChatMessageRequest.type());

        //채팅 저장
        ChatMessageRequest chatMessageRequest = ChatMessageApiMapper.toChatMessageRequest(roomId,
            userId, apiChatMessageRequest);
        ChatMessageResponse chatMessageResponse = chatMessageService.recordChat(chatMessageRequest);

        //채팅 전송
        simpMessageSendingOperations.convertAndSend("/topic/chats/" + roomId, chatMessageResponse);
    }

}
