package com.civilwar.boardsignal.user.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    NOT_FOUND_GENDER("성별 타입이 잘못 되었습니다", "C_001"),
    NOT_FOUND_AGE_GROUP("잘못된 연령대입니다.", "C_002");

    private final String message;
    private final String code;
}
