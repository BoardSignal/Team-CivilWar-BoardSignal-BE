package com.civilwar.boardsignal.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValidationErrorCode implements ErrorCode {

    TYPE_VALIDATED("존재하지 않는 값입니다", "B_001");

    private final String message;
    private final String code;
}
