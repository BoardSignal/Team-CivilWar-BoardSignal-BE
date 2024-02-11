package com.civilwar.boardsignal.boardgame.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardGameErrorCode implements ErrorCode {

    NOT_FOUND_BOARD_GAME("존재하지 않는 보드게임입니다", "B_001");

    private final String message;
    private final String code;
}
