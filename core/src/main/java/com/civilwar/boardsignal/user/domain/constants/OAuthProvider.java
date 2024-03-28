package com.civilwar.boardsignal.user.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {

    KAKAO("kakao"),
    NAVER("naver");

    private final String type;
}
