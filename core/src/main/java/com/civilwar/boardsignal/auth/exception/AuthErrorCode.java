package com.civilwar.boardsignal.auth.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    AUTH_NOT_EXIST_USER("존재하지 않는 회원입니다", "A_000"),
    AUTH_TOKEN_MALFORMED("유효하지 않은 토큰입니다", "A_001"),
    AUTH_TOKEN_EXPIRED("기한이 만료된 토큰입니다", "A_002"),
    AUTH_TOKEN_UNSUPPORTED("지원하지 않는 토큰입니다", "A_003"),
    AUTH_TOKEN_ILLEGAL("claims 정보가 비어있습니다", "A_004"),
    AUTH_TOKEN_NOT_SIGNATURE("Jwt 서명이 로컬로 산정된 서명과 일치하지 않습니다", "A_005"),
    AUTH_REQUIRED("인증이 필요합니다", "A_006");

    private final String message;
    private final String code;
}
