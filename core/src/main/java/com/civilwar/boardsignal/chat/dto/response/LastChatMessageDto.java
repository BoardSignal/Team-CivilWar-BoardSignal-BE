package com.civilwar.boardsignal.chat.dto.response;

public record LastChatMessageDto(
    Long roomId,
    String content
) {

}
