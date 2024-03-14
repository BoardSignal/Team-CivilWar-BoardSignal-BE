package com.civilwar.boardsignal.user.dto.response;

import java.util.List;

public record LoginUserInfoResponse(
    Long id,
    String email,
    String name,
    String nickname,
    int birth,
    int age,
    String ageGroup,
    String gender,
    Boolean isJoined,
    String subwayLine,
    String subwayStation,
    List<String> categories
) {

}
