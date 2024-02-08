package com.civilwar.boardsignal.room.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DaySlot {

    WEEKDAY("평일"),
    WEEKEND("주말");

    private final String description;
}
