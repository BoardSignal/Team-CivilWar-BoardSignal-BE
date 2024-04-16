package com.civilwar.boardsignal.auth.dto.request;

public record UserLoginRequest(
    String email,
    String nickname,
    String imageUrl,
    String provider,
    String providerId
) {

}
