package com.civilwar.boardsignal.chat.dto.response;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record LastChatMessageDto(
    Long roomId,
    String content,
    MessageType messageType,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt
) {

}
