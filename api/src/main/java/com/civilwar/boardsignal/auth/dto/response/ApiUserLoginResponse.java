package com.civilwar.boardsignal.auth.dto.response;

public record ApiUserLoginResponse(
    boolean isJoined,
    String accessToken
) {

}
