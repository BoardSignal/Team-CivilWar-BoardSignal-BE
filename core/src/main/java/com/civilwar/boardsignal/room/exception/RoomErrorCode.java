package com.civilwar.boardsignal.room.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements ErrorCode {


    NOT_FOUND_ROOM("해당 모임을 찾을 수 없습니다", "R_001"),
    INVALID_PARTICIPANT("이 방에 존재하지 않는 참가자 입니다.", "R_002"),
    INVALID_WEEKDAY("요일 형식이 잘못 되었습니다", "R_003"),
    IS_NOT_LEADER("방장이 아닙니다", "R_004"),
    NOT_FOUND_TIME_SLOT("시간대 값이 잘못 되었습니다.", "R_005"),
    NOT_FOUND_DAY_SLOT("날짜 값이 잘못 되었습니다.", "R_006");


    private final String message;
    private final String code;

}
