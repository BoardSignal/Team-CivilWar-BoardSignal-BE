package com.civilwar.boardsignal.room.dto.response;

import com.civilwar.boardsignal.room.domain.entity.Room;

public record KickOutFacadeResponse(
    Room room,
    String kickOutUserNickname
) {

}
