package com.civilwar.boardsignal.notification.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    ALREADY_TOKEN_EXISTS("해당 토큰은 이미 등록되어 있습니다.", "N_001");

    private final String message;
    private final String code;

}
