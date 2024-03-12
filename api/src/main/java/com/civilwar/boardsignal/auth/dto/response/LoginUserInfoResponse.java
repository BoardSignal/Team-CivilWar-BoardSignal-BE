package com.civilwar.boardsignal.auth.dto.response;

public record LoginUserInfoResponse(
    Long id,
    String email,
    String name,
    String nickname,
    int birth,
    int age,
    String ageGroup,
    String gender,
    Boolean isJoined
) {

}
