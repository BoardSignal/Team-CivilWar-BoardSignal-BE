package com.civilwar.boardsignal.room.dto.response;

import com.civilwar.boardsignal.room.domain.entity.Room;
import java.util.List;

public record DeleteRoomFacadeResponse(
    Room room,
    List<ParticipantJpaDto> participants
) {

}
