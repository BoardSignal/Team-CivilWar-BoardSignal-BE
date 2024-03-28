package com.civilwar.boardsignal.chat.dto.response;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import java.time.LocalDateTime;

public record ChatMessageDto(
    Long userId,
    String nickname,
    String userImageUrl,
    String content,
    MessageType type,
    LocalDateTime createdAt
) {

}
