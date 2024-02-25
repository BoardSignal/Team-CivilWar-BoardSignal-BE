package com.civilwar.boardsignal.room.dto.response;

public record ParticipantResponse(
    Long userId,
    String nickname,
    String ageGroup,
    Boolean isLeader,
    double mannerScore
) {
}
