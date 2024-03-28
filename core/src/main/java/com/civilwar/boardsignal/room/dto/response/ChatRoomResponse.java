package com.civilwar.boardsignal.room.dto.response;

public record ChatRoomResponse(
    Long id,
    String title,
    String imageUrl,
    int headCount
) {

}
