package com.civilwar.boardsignal.room.dto.response;

import java.util.List;

public record RoomPageResponse<T>(
    List<T> roomsInfos,
    int size,
    boolean hasNext
) {

}
