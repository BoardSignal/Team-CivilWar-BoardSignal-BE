package com.civilwar.boardsignal.room.dto.response;

import com.civilwar.boardsignal.user.domain.constants.AgeGroup;

public record ParticipantJpaDto(
    Long userId,
    String nickname,
    AgeGroup ageGroup,
    String profileImageUrl,
    Boolean isLeader,
    double mannerScore
) {

}
