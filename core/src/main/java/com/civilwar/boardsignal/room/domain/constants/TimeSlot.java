package com.civilwar.boardsignal.room.domain.constants;

import static com.civilwar.boardsignal.room.exception.RoomErrorCode.NOT_FOUND_TIME_SLOT;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeSlot {

    AM("오전"),
    PM("오후");

    private final String description;

    public static TimeSlot of(String input) {
        return Arrays.stream(values())
            .filter(category -> category.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_TIME_SLOT));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.description);
    }
}
