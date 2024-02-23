package com.civilwar.boardsignal.room.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record GetAllRoomResponse(
    Long id,
    String title,
    String description,
    String station,
    String time,
    int minAge,
    int maxAge,
    boolean isAllowedAppositeGender,
    String imageUrl,
    int minParticipants,
    int maxParticipants,
    List<String> categories,
    LocalDateTime createdAt
) {

}
