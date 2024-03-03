package com.civilwar.boardsignal.auth.dto.response;

public record LoginUserInfoResponse(
    Long id,
    String email,
    String nickname,
    String ageGroup,
    String gender,
    Boolean isJoined
) {

}
