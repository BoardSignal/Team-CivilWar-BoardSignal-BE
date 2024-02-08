package com.civilwar.boardsignal.room.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {

    NON_FIX("미확정"),
    FIX("확정");

    private final String description;
}
