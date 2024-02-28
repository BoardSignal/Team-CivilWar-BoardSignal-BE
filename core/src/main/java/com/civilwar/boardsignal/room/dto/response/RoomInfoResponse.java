package com.civilwar.boardsignal.room.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record RoomInfoResponse(
    Long roomId,
    String title,
    String description,
    String startTime,
    String place,
    int minAge,
    int maxAge,
    int minParticipants,
    int maxParticipants,
    String imageUrl,
    Boolean isLeader,
    String isFix,
    String allowedGender,
    List<String> categories,
    List<ParticipantResponse> participantResponse,
    @JsonFormat(pattern = "MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt

) {

}
