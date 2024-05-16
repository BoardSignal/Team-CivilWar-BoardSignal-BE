package com.civilwar.boardsignal.chat.dto.response;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import java.time.LocalDateTime;

public record LastChatMessageDto(
    Long roomId,
    String content,
    MessageType messageType,
    LocalDateTime createdAt
) {

}
