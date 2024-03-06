package com.civilwar.boardsignal.room.domain.constants;

import static com.civilwar.boardsignal.room.exception.RoomErrorCode.INVALID_WEEKDAY;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WeekDay {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String description;

    public static WeekDay of(String input) {
        return Arrays.stream(values())
            .filter(weekDay -> weekDay.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(INVALID_WEEKDAY));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.description);
    }
}
