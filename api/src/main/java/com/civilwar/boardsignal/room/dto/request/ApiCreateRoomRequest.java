package com.civilwar.boardsignal.room.dto.request;

import java.util.List;

public record ApiCreateRoomRequest(
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
    List<String> categories,
    boolean isAllowedOppositeGender
) {

}
