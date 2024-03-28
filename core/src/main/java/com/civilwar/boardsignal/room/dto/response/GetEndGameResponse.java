package com.civilwar.boardsignal.room.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record GetEndGameResponse(
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    int headCount,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime fixTime,
    String fixLine,
    String fixStation,
    String fixPlace,
    boolean reviewCompleted
) {

}
