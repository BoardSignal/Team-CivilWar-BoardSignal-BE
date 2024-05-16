package com.civilwar.boardsignal.room.dto.response;

import com.civilwar.boardsignal.chat.dto.response.LastChatMessageDto;

public record ChatRoomResponse(
    Long id,
    String title,
    String imageUrl,
    int headCount,
    int unreadChatCount,
    LastChatMessageDto lastChatMessage
) {

}
