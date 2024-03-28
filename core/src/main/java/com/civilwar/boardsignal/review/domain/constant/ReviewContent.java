package com.civilwar.boardsignal.review.domain.constant;

import static com.civilwar.boardsignal.review.exception.ReviewErrorCode.NOT_FOUND_CONTENT;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewContent {
    TIME_COMMITMENT("시간 약속을 잘 지켜요"),
    GOOD_MANNER("친절하고 매너가 좋아요"),
    FAST_RESPONSE("응답이 빨라요");

    private final String description;

    public static ReviewContent of(String input) {
        return Arrays.stream(values())
            .filter(reviewContent -> reviewContent.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_CONTENT));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.description);
    }

}
