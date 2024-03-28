package com.civilwar.boardsignal.chat.dto.response;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record GetChatMessageResponse(
    Long userId,
    String nickname,
    String userImageUrl,
    String content,
    MessageType type,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    LocalDateTime createdAt,
    Boolean isChecked
) {

}
