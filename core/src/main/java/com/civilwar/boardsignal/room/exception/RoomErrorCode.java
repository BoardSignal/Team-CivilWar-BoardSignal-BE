package com.civilwar.boardsignal.room.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements ErrorCode {

    NOT_FOUND_ROOM("해당 모임을 찾을 수 없습니다", "R_001");


    private final String message;
    private final String code;

}
