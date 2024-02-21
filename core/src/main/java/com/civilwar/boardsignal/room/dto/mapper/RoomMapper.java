package com.civilwar.boardsignal.room.dto.mapper;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoomMapper {

    public static Room toRoom(
        String roomImageUrl,
        CreateRoomRequest request
    ) {
        DaySlot daySlot = DaySlot.of(request.day());
        TimeSlot timeSlot = TimeSlot.of(request.time());
        List<Category> categories = request.categories().stream()
            .map(Category::of)
            .toList();

        return Room.of(
            request.roomTitle(),
            request.description(),
            request.minPartipants(),
            request.maxPartipants(),
            request.place(),
            request.subwayLine(),
            request.subwayStation(),
            daySlot,
            timeSlot,
            request.startTime(),
            request.minAge(),
            request.maxAge(),
            roomImageUrl,
            request.isAllowedOppositeGender(),
            categories
        );
    }

    public static CreateRoomResponse toCreateRoomResponse(Room room) {
        return new CreateRoomResponse(room.getId());
    }
}
