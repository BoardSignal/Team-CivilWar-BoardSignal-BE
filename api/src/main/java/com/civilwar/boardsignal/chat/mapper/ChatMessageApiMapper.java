package com.civilwar.boardsignal.chat.mapper;

import com.civilwar.boardsignal.chat.dto.ApiChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.request.ChatMessageRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMessageApiMapper {

    public static ChatMessageRequest toChatMessageRequest(Long roomId, Long userId,
        ApiChatMessageRequest apiChatMessageRequest) {
        return new ChatMessageRequest(roomId, userId, apiChatMessageRequest.content(),
            apiChatMessageRequest.type());
    }

}
