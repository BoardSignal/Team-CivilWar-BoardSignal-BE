package com.civilwar.boardsignal.chat.dto.request;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;

public record ChatMessageRequest(
    Long roomId,
    Long userId,
    String content,
    MessageType type
) {

}
