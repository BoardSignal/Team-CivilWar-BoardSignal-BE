package com.civilwar.boardsignal.room.dto.response;

public record ChatRoomDto(
    Long id,
    String title,
    String imageUrl,
    int headCount
) {

}
