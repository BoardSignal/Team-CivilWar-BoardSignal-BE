package com.civilwar.boardsignal.chat.mapper;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageResponse;
import com.civilwar.boardsignal.chat.dto.response.ChatPageResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatMessageMapper {

    public static ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage) {
        return new ChatMessageResponse(chatMessage.getUserId(), chatMessage.getContent(),
            chatMessage.getMessageType(), chatMessage.getCreatedAt());
    }

    public static ChatPageResponse<ChatMessageDto> toChatPageResponse(
        Slice<ChatMessageDto> slices) {

        return new ChatPageResponse<>(
            slices.getContent(),
            slices.getNumber(),
            slices.getSize(),
            slices.hasNext()
        );
    }

}
