package com.civilwar.boardsignal.room.dto.response;

import java.util.List;

public record CreateRoomRequest(
    String roomTitle,
    String description,
    int minPartipants,
    int maxPartipants,
    String day,
    String time,
    String startTime,
    int minAge,
    int maxAge,
    String subwayLine,
    String subwayStation,
    String place,
    List<String> categories
) {

}
