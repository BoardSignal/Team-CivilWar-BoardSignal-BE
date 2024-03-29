package com.civilwar.boardsignal.room.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomErrorCode implements ErrorCode {


    NOT_FOUND_ROOM("해당 모임을 찾을 수 없습니다", "R_001"),
    INVALID_PARTICIPANT("이 방에 존재하지 않는 참가자 입니다.", "R_002"),
    IS_NOT_LEADER("방장이 아닙니다", "R_003"),
    NOT_FOUND_TIME_SLOT("시간대 값이 잘못 되었습니다.", "R_004"),
    NOT_FOUND_DAY_SLOT("날짜 값이 잘못 되었습니다.", "R_005"),
    ALREADY_PARTICIPANT("이미 모임에 참여하였습니다", "R_006"),
    INVALID_DATE("오늘보다 이전 날짜로 잘못된 날짜입니다.", "R_007"),
    INVALID_HEADCOUNT("인원이 초과되었습니다", "R_008"),
    INVALID_GENDER("성별이 모임의 조건과 일치하지 않습니다", "R_009"),
    INVALID_AGE("조건에 해당하는 연령만 입장 가능합니다", "R_0010"),
    CAN_NOT_PARTICIPANT("추방 당한 방에는 재입장할 수 없습니다", "R_011");


    private final String message;
    private final String code;

}
