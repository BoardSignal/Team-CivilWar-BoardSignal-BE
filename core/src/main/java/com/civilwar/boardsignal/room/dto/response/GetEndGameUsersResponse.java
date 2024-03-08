package com.civilwar.boardsignal.room.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record GetEndGameUsersResponse(
    Long roomId,
    String title,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime meetingTime,
    String weekDay,
    int peopleCount,
    String line,
    String station,
    String meetingPlace,
    String allowedGender,
    int minAge,
    int maxAge,
    int minParticipant,
    int maxParticipant,
    List<String> categories,
    @JsonFormat(pattern = "MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    int headCount,
    List<ParticipantResponse> participantsInfos
) {

}