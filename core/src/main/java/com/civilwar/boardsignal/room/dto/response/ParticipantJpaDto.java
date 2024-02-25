package com.civilwar.boardsignal.room.dto.response;

import com.civilwar.boardsignal.user.domain.constants.AgeGroup;

public record ParticipantJpaDto(
    Long userId,
    String nickname,
    AgeGroup ageGroup,
    Boolean isLeader,
    double mannerScore
) {

}
