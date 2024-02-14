package com.civilwar.boardsignal.room.domain.constants;

import static com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode.NOT_FOUND_BOARD_GAME;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DaySlot {

    WEEKDAY("평일"),
    WEEKEND("주말");

    private final String description;

    public static DaySlot of(String input) {
        return Arrays.stream(values())
            .filter(category -> category.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_BOARD_GAME));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.description);
    }
}
