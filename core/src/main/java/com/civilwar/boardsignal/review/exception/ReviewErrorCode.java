package com.civilwar.boardsignal.review.exception;

import com.civilwar.boardsignal.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    NOT_FOUND_CONTENT("존재하지 않는 리뷰 항목입니다", "R_001");


    private final String message;
    private final String code;
}
