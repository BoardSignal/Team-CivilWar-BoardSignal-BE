package com.civilwar.boardsignal.auth.dto;

public record OAuthUserInfo(
    String email,
    String nickname,
    String imageUrl,
    String provider,
    String providerId
) {

}
