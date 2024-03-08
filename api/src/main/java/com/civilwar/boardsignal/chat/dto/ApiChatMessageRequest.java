package com.civilwar.boardsignal.chat.dto;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import jakarta.validation.constraints.NotBlank;

public record ApiChatMessageRequest(
    @NotBlank
    String content,
    MessageType type
) {

}
