package com.civilwar.boardsignal.auth.dto.request;

public record UserLoginRequest(
    String email,
    String name,
    String nickname,
    String imageUrl,
    String birthYear,
    String ageRange,
    String gender,
    String provider,
    String providerId
) {

}
