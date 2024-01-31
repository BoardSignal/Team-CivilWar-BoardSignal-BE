package com.civilwar.boardsignal.auth.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    AUTH_TOKEN_EXPIRED("만료된 토큰입니다", "A_001"),
    AUTH_TOKEN_INVALID("잘못된 토큰입니다", "A_002"),
    AUTH_NOT_EXIST_USER("존재하지 않는 회원입니다", "A_003");

    private final String message;
    private final String code;
}
