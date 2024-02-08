package com.civilwar.boardsignal.room.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TimeSlot {

    AM("오전"),
    PM("오후");

    private final String description;
}
