package com.civilwar.boardsignal.review.domain.constant;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewRecommend {

    LIKE("좋아요", 1),
    NON_REVIEW("선택 안 함", 0),
    DISLIKE("싫어요", -1);

    private final String message;
    private final int score;

    public static ReviewRecommend of(String input) {
        return Arrays.stream(values())
            .filter(recommend -> recommend.isEqual(input))
            .findAny()
            .orElseThrow();
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.message);
    }

}
