package com.civilwar.boardsignal.room.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    String allowedGender,
    String imageUrl,
    int minParticipants,
    int maxParticipants,
    List<String> categories,
    @JsonFormat(pattern = "MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    int headCount
) {

}
