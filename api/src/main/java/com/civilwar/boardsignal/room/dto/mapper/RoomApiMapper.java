package com.civilwar.boardsignal.room.dto.mapper;

import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoomApiMapper {

    public static CreateRoomRequest toCreateRoomRequest(
        MultipartFile image,
        ApiCreateRoomRequest request
    ) {
        return new CreateRoomRequest(
            request.time(),
            request.description(),
            request.minParticipants(),
            request.maxParticipants(),
            request.day(),
            request.time(),
            request.startTime(),
            request.minAge(),
            request.maxAge(),
            request.subwayLine(),
            request.subwayStation(),
            request.place(),
            request.categories(),
            request.isAllowedOppositeGender(),
            image
        );
    }
}
