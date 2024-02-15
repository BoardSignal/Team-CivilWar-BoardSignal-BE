package com.civilwar.boardsignal.room.dto.mapper;

import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomApiMapper {

    public static CreateRoomRequest toCreateRoomRequest(ApiCreateRoomRequest request) {
        return new CreateRoomRequest(
            request.time(),
            request.description(),
            request.minPartipants(),
            request.maxPartipants(),
            request.day(),
            request.time(),
            request.startTime(),
            request.minAge(),
            request.maxAge(),
            request.subwayLine(),
            request.subwayStation(),
            request.place(),
            request.categories()
        );
    }
}
