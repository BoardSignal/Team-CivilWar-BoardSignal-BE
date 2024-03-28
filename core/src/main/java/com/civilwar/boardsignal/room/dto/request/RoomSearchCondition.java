package com.civilwar.boardsignal.room.dto.request;

import java.util.List;

public record RoomSearchCondition(
    String searchKeyword,
    List<String> station,
    List<String> time,
    List<String> category,
    String gender

) {

}
