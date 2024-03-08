package com.civilwar.boardsignal.chat.mapper;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMessageMapper {

    public static ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        return new ChatMessageResponse(chatMessage.getUserId(), chatMessage.getContent(),
            chatMessage.getMessageType(), chatMessage.getCreatedAt());
    }

}
