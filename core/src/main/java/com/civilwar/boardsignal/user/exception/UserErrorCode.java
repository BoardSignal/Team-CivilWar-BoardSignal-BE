package com.civilwar.boardsignal.user.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND_BY_PROVIDER_ID("providerId 에 해당하는 사용자가 존재하지 않습니다", "U_001");

    private final String message;
    private final String code;
}
