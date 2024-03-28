package com.civilwar.boardsignal.boardgame.domain.constant;

import static com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode.NOT_FOUND_DIFFICULTY;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    EASY("쉬움"),
    NORMAL("보통"),
    HARD("어려움");

    private final String description;

    public static Difficulty of(String input) {
        return Arrays.stream(values())
            .filter(category -> category.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_DIFFICULTY));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.description);
    }
}
