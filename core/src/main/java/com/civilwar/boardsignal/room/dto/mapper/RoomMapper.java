package com.civilwar.boardsignal.room.dto.mapper;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.response.FixRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.response.ParticipantResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoomMapper {

    public static Room toRoom(
        String roomImageUrl,
        CreateRoomRequest request,
        Gender allowedGender
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
            allowedGender,
            categories
        );
    }

    public static CreateRoomResponse toCreateRoomResponse(Room room) {
        return new CreateRoomResponse(room.getId());
    }

    public static GetAllRoomResponse toGetAllRoomResponse(Room room) {
        List<String> categories = room.getRoomCategories().stream()
            .map(roomCategory -> roomCategory.getCategory().getDescription())
            .toList();

        return new GetAllRoomResponse(
            room.getId(),
            room.getTitle(),
            room.getDescription(),
            room.getSubwayStation(),
            room.getStartTime(),
            room.getMinAge(),
            room.getMaxAge(),
            room.getAllowedGender().getDescription(),
            room.getImageUrl(),
            room.getMinParticipants(),
            room.getMaxParticipants(),
            categories,
            room.getCreatedAt(),
            room.getHeadCount()
        );
    }

    public static RoomPageResponse<GetAllRoomResponse> toRoomPageResponse(Slice<Room> pages) {

        Slice<GetAllRoomResponse> dto = pages.map(RoomMapper::toGetAllRoomResponse);

        return new RoomPageResponse<>(
            dto.getContent(),
            dto.getSize(),
            dto.hasNext()
        );
    }

    public static RoomInfoResponse toRoomInfoResponse(
        Room room,
        String time,
        String place,
        Boolean isLeader,
        List<ParticipantResponse> participants
    ) {
        return new RoomInfoResponse(
            room.getId(),
            room.getTitle(),
            room.getDescription(),
            time,
            place,
            room.getMinAge(),
            room.getMaxAge(),
            room.getMinParticipants(),
            room.getMaxParticipants(),
            room.getImageUrl(),
            isLeader,
            room.getStatus().getDescription(),
            room.getAllowedGender().getDescription(),
            room.getRoomCategories().stream()
                .map(roomCategory -> roomCategory.getCategory().getDescription())
                .toList(),
            participants,
            room.getCreatedAt()
        );
    }

    public static ParticipantResponse toParticipantResponse(ParticipantJpaDto participantJpaDto) {
        return new ParticipantResponse(
            participantJpaDto.userId(),
            participantJpaDto.nickname(),
            participantJpaDto.ageGroup().getDescription(),
            participantJpaDto.isLeader(),
            participantJpaDto.mannerScore()
        );
    }

    public static FixRoomResponse toFixRoomResponse(Room room, MeetingInfo meetingInfo) {
        return new FixRoomResponse(room.getId(), meetingInfo.getId());
    }
}
