package com.civilwar.boardsignal.auth.dto;

public record OAuthUserInfo(
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
