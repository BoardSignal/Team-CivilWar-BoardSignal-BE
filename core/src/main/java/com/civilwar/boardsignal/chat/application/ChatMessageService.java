package com.civilwar.boardsignal.chat.application;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.chat.dto.request.ChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageResponse;
import com.civilwar.boardsignal.chat.mapper.ChatMessageMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageResponse recordChat(ChatMessageRequest chatMessageRequest) {

        //엔티티 변환
        ChatMessage chatMessage = ChatMessage.of(chatMessageRequest.roomId(),
            chatMessageRequest.userId(),
            chatMessageRequest.content(), chatMessageRequest.type());

        //채팅 로그 기록
        chatMessageRepository.save(chatMessage);

        return ChatMessageMapper.toChatMessageResponse(
            chatMessage);
    }

}
