package com.civilwar.boardsignal.review.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    NOT_FOUND_CONTENT("존재하지 않는 리뷰 항목입니다", "R_001"),
    ALREADY_EXIST_REVIEW("해당 모임에 이미 리뷰하였습니다", "R_002");


    private final String message;
    private final String code;
}
