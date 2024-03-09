package com.civilwar.boardsignal.boardgame.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoardGameErrorCode implements ErrorCode {


    NOT_FOUND_BOARD_GAME("존재하지 않는 보드게임입니다", "B_001"),
    NOT_FOUND_DIFFICULTY("존재하지 않는 난이도입니다.", "B_002"),
    AlREADY_TIP_ADDED("이미 등록한 공략이 있는 보드게임입니다.", "B_003"),
    NOT_FOUND_TIP_USER("해당 공략을 등록한 회원이 존재하지 않습니다.", "B_004"),
    NOT_FOUND_TIP("존재하지 않는 공략입니다.", "B_005"),
    ALREADY_WISHED("이미 찜한 보드게임입니다.", "B_006"),
    NOT_FOUND_WISH("찜한 적이 없는 보드게임입니다", "B_007"),
    NOT_FOUND_BOARD_GAME_CATEGORY("존재하지 않는 보드게임 카테고리입니다.", "B_008");

    private final String message;
    private final String code;
}
