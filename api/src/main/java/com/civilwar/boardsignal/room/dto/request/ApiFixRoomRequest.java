package com.civilwar.boardsignal.room.dto.request;

import java.time.LocalDateTime;

public record ApiFixRoomRequest(
    LocalDateTime meetingTime,
    String weekDay,
    int peopleCount,
    String line,
    String station,
    String meetingPlace
) {

}
