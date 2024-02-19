package com.civilwar.boardsignal.review.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewContent {
    TIME_COMMITMENT("시간 약속을 잘 지켜요"),
    GOOD_MANNER("친절하고 매너가 좋아요"),
    FAST_RESPONSE("응답이 빨라요");

    private final String description;

}
